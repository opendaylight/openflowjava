/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandler;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueManager<T extends OutboundQueueHandler> extends ChannelInboundHandlerAdapter implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueManager.class);

    /**
     * Default low write watermark. Channel will become writable when number of outstanding
     * bytes dips below this value.
     */
    private static final int DEFAULT_LOW_WATERMARK = 128 * 1024;

    /**
     * Default write high watermark. Channel will become un-writable when number of
     * outstanding bytes hits this value.
     */
    private static final int DEFAULT_HIGH_WATERMARK = DEFAULT_LOW_WATERMARK * 2;

    private final AtomicBoolean flushScheduled = new AtomicBoolean();
    private final StackedOutboundQueue currentQueue;
    private final ConnectionAdapterImpl parent;
    private final InetSocketAddress address;
    private final int maxNonBarrierMessages;
    private final long maxBarrierNanos;
    private final T handler;

    // Accessed concurrently
    private volatile boolean reading;

    // Updated from netty only
    private boolean alreadyReading;
    private boolean barrierTimerEnabled;
    private long lastBarrierNanos = System.nanoTime();
    private int nonBarrierMessages;
    private boolean shuttingDown;

    // Passed to executor to request triggering of flush
    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            flush();
        }
    };

    // Passed to executor to request a periodic barrier check
    private final Runnable barrierRunnable = new Runnable() {
        @Override
        public void run() {
            barrier();
        }
    };

    OutboundQueueManager(final ConnectionAdapterImpl parent, final InetSocketAddress address, final T handler,
        final int maxNonBarrierMessages, final long maxBarrierNanos) {
        this.parent = Preconditions.checkNotNull(parent);
        this.handler = Preconditions.checkNotNull(handler);
        Preconditions.checkArgument(maxNonBarrierMessages > 0);
        this.maxNonBarrierMessages = maxNonBarrierMessages;
        Preconditions.checkArgument(maxBarrierNanos > 0);
        this.maxBarrierNanos = maxBarrierNanos;
        this.address = address;

        currentQueue = new StackedOutboundQueue(this);
        LOG.debug("Queue manager instantiated with queue {}", currentQueue);
        handler.onConnectionQueueChanged(currentQueue);
    }

    T getHandler() {
        return handler;
    }

    @Override
    public void close() {
        handler.onConnectionQueueChanged(null);
    }

    private void scheduleBarrierTimer(final long now) {
        long next = lastBarrierNanos + maxBarrierNanos;
        if (next < now) {
            LOG.trace("Attempted to schedule barrier in the past, reset maximum)");
            next = now + maxBarrierNanos;
        }

        final long delay = next - now;
        LOG.trace("Scheduling barrier timer {}us from now", TimeUnit.NANOSECONDS.toMicros(delay));
        parent.getChannel().eventLoop().schedule(barrierRunnable, next - now, TimeUnit.NANOSECONDS);
        barrierTimerEnabled = true;
    }

    private void scheduleBarrierMessage() {
        final Long xid = currentQueue.reserveBarrierIfNeeded();
        if (xid == null) {
            LOG.trace("Queue {} already contains a barrier, not scheduling one", currentQueue);
            return;
        }

        currentQueue.commitEntry(xid, handler.createBarrierRequest(xid), null);
        LOG.trace("Barrier XID {} scheduled", xid);
    }


    /**
     * Invoked whenever a message comes in from the switch. Runs matching
     * on all active queues in an attempt to complete a previous request.
     *
     * @param message Potential response message
     * @return True if the message matched a previous request, false otherwise.
     */
    boolean onMessage(final OfHeader message) {
        LOG.trace("Attempting to pair message {} to a request", message);

        return currentQueue.pairRequest(message);
    }

    private void scheduleFlush() {
        if (flushScheduled.compareAndSet(false, true)) {
            LOG.trace("Scheduling flush task on channel {}", parent.getChannel());
            parent.getChannel().eventLoop().execute(flushRunnable);
        } else {
            LOG.trace("Flush task is already present on channel {}", parent.getChannel());
        }
    }

    /**
     * Periodic barrier check.
     */
    protected void barrier() {
        LOG.debug("Channel {} barrier timer expired", parent.getChannel());
        barrierTimerEnabled = false;
        if (shuttingDown) {
            LOG.trace("Channel shut down, not processing barrier");
            return;
        }

        final long now = System.nanoTime();
        final long sinceLast = now - lastBarrierNanos;
        if (sinceLast >= maxBarrierNanos) {
            LOG.debug("Last barrier at {} now {}, elapsed {}", lastBarrierNanos, now, sinceLast);
            // FIXME: we should be tracking requests/responses instead of this
            if (nonBarrierMessages == 0) {
                LOG.trace("No messages written since last barrier, not issuing one");
            } else {
                scheduleBarrierMessage();
            }
        }
    }

    private void rescheduleFlush() {
        /*
         * We are almost ready to terminate. This is a bit tricky, because
         * we do not want to have a race window where a message would be
         * stuck on the queue without a flush being scheduled.
         *
         * So we mark ourselves as not running and then re-check if a
         * flush out is needed. That will re-synchronized with other threads
         * such that only one flush is scheduled at any given time.
         */
        if (!flushScheduled.compareAndSet(true, false)) {
            LOG.warn("Channel {} queue {} flusher found unscheduled", parent.getChannel(), this);
        }

        conditionalFlush();
    }

    private void writeAndFlush() {
        final long start = System.nanoTime();

        final int entries = currentQueue.writeEntries(parent.getChannel(), start);
        if (entries > 0) {
            LOG.debug("Flushing channel {}", parent.getChannel());
            parent.getChannel().flush();
        }

        if (LOG.isDebugEnabled()) {
            final long stop = System.nanoTime();
            LOG.debug("Flushed {} messages to channel {} in {}us", entries,
                parent.getChannel(), TimeUnit.NANOSECONDS.toMicros(stop - start));
        }
    }

    /**
     * Perform a single flush operation. We keep it here so we do not generate
     * syntetic accessors for private fields. Otherwise it could be moved into
     * {@link #flushRunnable}.
     */
    protected void flush() {
        // If the channel is gone, just flush whatever is not completed
        if (!shuttingDown) {
            LOG.debug("Dequeuing messages to channel {}", parent.getChannel());
            writeAndFlush();
            rescheduleFlush();
        } else if (currentQueue.finishShutdown()) {
            handler.onConnectionQueueChanged(null);
            LOG.debug("Channel {} shutdown complete", parent.getChannel());
        } else {
            LOG.trace("Channel {} current queue not completely flushed yet", parent.getChannel());
            rescheduleFlush();
        }
    }

    /**
     * Schedule a queue flush if it is not empty and the channel is found
     * to be writable. May only be called from Netty context.
     */
    private void conditionalFlush() {
        if (currentQueue.needsFlush()) {
            if (shuttingDown || parent.getChannel().isWritable()) {
                scheduleFlush();
            } else {
                LOG.debug("Channel {} is not I/O ready, not scheduling a flush", parent.getChannel());
            }
        } else {
            LOG.trace("Queue is empty, no flush needed");
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        conditionalFlush();
    }

    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        /*
         * Tune channel write buffering. We increase the writability window
         * to ensure we can flush an entire queue segment in one go. We definitely
         * want to keep the difference above 64k, as that will ensure we use jam-packed
         * TCP packets. UDP will fragment as appropriate.
         */
        ctx.channel().config().setWriteBufferLowWaterMark(DEFAULT_LOW_WATERMARK);
        ctx.channel().config().setWriteBufferHighWaterMark(DEFAULT_HIGH_WATERMARK);

        super.handlerAdded(ctx);
    }

    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);

        // A simple trade-off. While we could write things right away, if there is a task
        // schedule, let it have the work
        if (flushScheduled.compareAndSet(false, true)) {
            LOG.debug("Channel {} writability changed, invoking flush", parent.getChannel());
            flush();
        } else {
            LOG.debug("Channel {} Writability changed, but task is already scheduled", parent.getChannel());
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        LOG.debug("Channel {} initiating shutdown...", ctx.channel());

        shuttingDown = true;
        final long entries = currentQueue.startShutdown(ctx.channel());
        LOG.debug("Cleared {} queue entries from channel {}", entries, ctx.channel());

        scheduleFlush();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        // non-volatile read if we are called multiple times
        if (!alreadyReading) {
            alreadyReading = true;
            reading = true;
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        alreadyReading = false;
        reading = false;

        // TODO: model this as an atomic gate. We need to sync on it to make sure
        //       that ensureFlushing() suppresses scheudling only if this barrier
        //       has not been crossed.
        synchronized (this) {
            // Run flush regardless of writability. This is not strictly required, as
            // there may be a scheduled flush. Instead of canceling it, which is expensive,
            // we'll steal its work. Note that more work may accumulate in the time window
            // between now and when the task will run, so it may not be a no-op after all.
            //
            // The reason for this is to will the output buffer before we go into selection
            // phase. This will make sure the pipe is full (in which case our next wake up
            // will be the queue becoming writable).
            writeAndFlush();
        }

        LOG.debug("Opportunistic write on channel {}", parent.getChannel());
        writeAndFlush();
    }

    @Override
    public String toString() {
        return String.format("Channel %s queue [flushing=%s]", parent.getChannel(), flushScheduled.get());
    }

    void ensureFlushing() {
        // If the channel is not writable, there's no point in waking up,
        // once we become writable, we will run a full flush
        if (!parent.getChannel().isWritable()) {
            return;
        }

        // We are currently reading something, just a quick sync to ensure we will in fact
        // flush state.
        if (reading) {
            synchronized (this) {
                if (reading) {
                    return;
                }
            }
        }

        // Netty thread is outside our code, we need to schedule a flush
        // to re-synchronize.
        scheduleFlush();
    }

    void onEchoRequest(final EchoRequestMessage message) {
        final EchoReplyInput reply = new EchoReplyInputBuilder().setData(message.getData()).setVersion(message.getVersion()).setXid(message.getXid()).build();
        parent.getChannel().writeAndFlush(reply);
    }

    /**
     * Write a message into the underlying channel.
     *
     * @param now Time reference for 'now'. We take this as an argument, as
     *            we need a timestamp to mark barrier messages we see swinging
     *            by. That timestamp does not need to be completely accurate,
     *            hence we use the flush start time. Alternative would be to
     *            measure System.nanoTime() for each barrier -- needlessly
     *            adding overhead.
     */
    void writeMessage(final OfHeader message, final long now) {
        final Object wrapper;
        if (address == null) {
            wrapper = new MessageListenerWrapper(message, null);
        } else {
            wrapper = new UdpMessageListenerWrapper(message, null, address);
        }
        parent.getChannel().write(wrapper);

        if (message instanceof BarrierInput) {
            LOG.trace("Barrier message seen, resetting counters");
            nonBarrierMessages = 0;
            lastBarrierNanos = now;
        } else {
            nonBarrierMessages++;
            if (nonBarrierMessages >= maxNonBarrierMessages) {
                LOG.trace("Scheduled barrier request after {} non-barrier messages", nonBarrierMessages);
                scheduleBarrierMessage();
            } else if (!barrierTimerEnabled) {
                scheduleBarrierTimer(now);
            }
        }
    }
}
