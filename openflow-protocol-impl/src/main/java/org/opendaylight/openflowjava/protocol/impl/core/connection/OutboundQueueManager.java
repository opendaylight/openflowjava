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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueException;
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
     * This is the default upper bound we place on the flush task running
     * a single iteration. We relinquish control after about this amount
     * of time.
     */
    private static final long DEFAULT_WORKTIME_MICROS = TimeUnit.MILLISECONDS.toMicros(100);

    /**
     * We re-check the time spent flushing every this many messages. We do this because
     * checking after each message may prove to be CPU-intensive. Set to Integer.MAX_VALUE
     * or similar to disable the feature.
     */
    private static final int WORKTIME_RECHECK_MSGS = 64;

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


    private final Queue<OutboundQueueImpl> activeQueues = new LinkedList<>();
    private final AtomicBoolean flushScheduled = new AtomicBoolean();
    private final ConnectionAdapterImpl parent;
    private final InetSocketAddress address;
    private final long maxBarrierNanos;
    private final long maxWorkTime;
    private final T handler;

    // Updated from netty only
    private long lastBarrierNanos = System.nanoTime();
    private OutboundQueueCacheSlice slice;
    private OutboundQueueImpl currentQueue;
    private boolean barrierTimerEnabled;
    private int nonBarrierMessages;
    private long lastXid = 0;
    private Integer shutdownOffset;

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
        final OutboundQueueCacheSlice slice, final long maxBarrierNanos) {
        this.parent = Preconditions.checkNotNull(parent);
        this.handler = Preconditions.checkNotNull(handler);
        this.slice = Preconditions.checkNotNull(slice);
        Preconditions.checkArgument(maxBarrierNanos > 0);
        this.maxBarrierNanos = maxBarrierNanos;
        this.address = address;
        this.maxWorkTime = TimeUnit.MICROSECONDS.toNanos(DEFAULT_WORKTIME_MICROS);

        LOG.debug("Queue manager instantiated with queue slice {}", slice);
        createQueue();
    }

    T getHandler() {
        return handler;
    }

    @Override
    public void close() {
        handler.onConnectionQueueChanged(null);
        if (slice != null) {
            slice.decRef();
            slice = null;
        }
    }

    private void createQueue() {
        final long baseXid = lastXid;
        lastXid += slice.getQueueSize() + 1;

        final OutboundQueueImpl queue = slice.getQueue(this, baseXid);
        activeQueues.add(queue);
        currentQueue = queue;
        handler.onConnectionQueueChanged(queue);
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
     * Flush an entry from the queue.
     *
     * @param now Time reference for 'now'. We take this as an argument, as
     *            we need a timestamp to mark barrier messages we see swinging
     *            by. That timestamp does not need to be completely accurate,
     *            hence we use the flush start time. Alternative would be to
     *            measure System.nanoTime() for each barrier -- needlessly
     *            adding overhead.
     *
     * @return Entry which was flushed, null if no entry is ready.
     */
    OfHeader flushEntry(final long now) {
        final OfHeader message = currentQueue.flushEntry();
        if (currentQueue.isFlushed()) {
            LOG.debug("Queue {} is fully flushed", currentQueue);
            createQueue();
        }

        if (message == null) {
            return null;
        }

        if (message instanceof BarrierInput) {
            LOG.trace("Barrier message seen, resetting counters");
            nonBarrierMessages = 0;
            lastBarrierNanos = now;
        } else {
            nonBarrierMessages++;
            if (nonBarrierMessages >= slice.getQueueSize()) {
                LOG.trace("Scheduled barrier request after {} non-barrier messages", nonBarrierMessages);
                scheduleBarrierMessage();
            } else if (!barrierTimerEnabled) {
                scheduleBarrierTimer(now);
            }
        }

        return message;
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

        Iterator<OutboundQueueImpl> it = activeQueues.iterator();
        while (it.hasNext()) {
            final OutboundQueueImpl queue = it.next();
            final OutboundQueueEntry entry = queue.pairRequest(message);

            if (entry == null) {
                continue;
            }

            LOG.trace("Queue {} accepted response {}", queue, message);

            // This has been a barrier request, we need to flush all
            // previous queues
            if (entry.isBarrier() && activeQueues.size() > 1) {
                LOG.trace("Queue {} indicated request was a barrier", queue);

                it = activeQueues.iterator();
                while (it.hasNext()) {
                    final OutboundQueueImpl q = it.next();

                    // We want to complete all queues before the current one, we will
                    // complete the current queue below
                    if (!queue.equals(q)) {
                        LOG.trace("Queue {} is implied finished", q);
                        q.completeAll();
                        it.remove();
                        slice.putQueue(q);
                    } else {
                        break;
                    }
                }
            }

            if (queue.isFinished()) {
                LOG.trace("Queue {} is finished", queue);
                it.remove();
                slice.putQueue(queue);
            }

            return true;
        }

        LOG.debug("Failed to find completion for message {}", message);
        return false;
    }

    private void scheduleFlush() {
        if (flushScheduled.compareAndSet(false, true)) {
            LOG.trace("Scheduling flush task on channel {}", parent.getChannel());
            parent.getChannel().eventLoop().execute(flushRunnable);
        } else {
            LOG.trace("Flush task is already present on channel {}", parent.getChannel());
        }
    }

    void ensureFlushing(final OutboundQueueImpl queue) {
        Preconditions.checkState(currentQueue.equals(queue));
        scheduleFlush();
    }

    /**
     * Periodic barrier check.
     */
    protected void barrier() {
        LOG.debug("Channel {} barrier timer expired", parent.getChannel());
        barrierTimerEnabled = false;
        if (shutdownOffset != null) {
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

    private void shutdownFlush() {
        long entries = 0;

        // Fail all queues
        final Iterator<OutboundQueueImpl> it = activeQueues.iterator();
        while (it.hasNext()) {
            final OutboundQueueImpl queue = it.next();

            entries += queue.failAll(OutboundQueueException.DEVICE_DISCONNECTED);
            if (queue.isFinished()) {
                LOG.trace("Cleared queue {}", queue);
                it.remove();
            }
        }

        LOG.debug("Cleared {} queue entries from channel {}", entries, parent.getChannel());

        Preconditions.checkNotNull(currentQueue, "Current queue should not be null yet");
        if (currentQueue.isShutdown(shutdownOffset)) {
            currentQueue = null;
            handler.onConnectionQueueChanged(null);
            LOG.debug("Channel {} shutdown complete", parent.getChannel());
        } else {
            LOG.trace("Channel {} current queue not completely flushed yet", parent.getChannel());
            rescheduleFlush();
        }
    }

    /**
     * Perform a single flush operation. We keep it here so we do not generate
     * syntetic accessors for private fields. Otherwise it could be moved into
     * {@link #flushRunnable}.
     */
    protected void flush() {
        // If the channel is gone, just flush whatever is not completed
        if (shutdownOffset != null) {
            shutdownFlush();
            return;
        }

        final long start = System.nanoTime();
        final long deadline = start + maxWorkTime;

        LOG.debug("Dequeuing messages to channel {}", parent.getChannel());

        long messages = 0;
        for (;; ++messages) {
            if (!parent.getChannel().isWritable()) {
                LOG.debug("Channel {} is no longer writable", parent.getChannel());
                break;
            }

            final OfHeader message = flushEntry(start);
            if (message == null) {
                LOG.trace("The queue is completely drained");
                break;
            }

            final Object wrapper;
            if (address == null) {
                wrapper = new MessageListenerWrapper(message, null);
            } else {
                wrapper = new UdpMessageListenerWrapper(message, null, address);
            }
            parent.getChannel().write(wrapper);

            /*
             * Check every WORKTIME_RECHECK_MSGS for exceeded time.
             *
             * XXX: given we already measure our flushing throughput, we
             *      should be able to perform dynamic adjustments here.
             *      is that additional complexity needed, though?
             */
            if ((messages % WORKTIME_RECHECK_MSGS) == 0 && System.nanoTime() >= deadline) {
                LOG.trace("Exceeded allotted work time {}us",
                        TimeUnit.NANOSECONDS.toMicros(maxWorkTime));
                break;
            }
        }

        if (messages > 0) {
            LOG.debug("Flushing {} message(s) to channel {}", messages, parent.getChannel());
            parent.getChannel().flush();
        }

        final long stop = System.nanoTime();
        LOG.debug("Flushed {} messages in {}us to channel {}",
                messages, TimeUnit.NANOSECONDS.toMicros(stop - start), parent.getChannel());

        rescheduleFlush();
    }

    /**
     * Schedule a queue flush if it is not empty and the channel is found
     * to be writable. May only be called from Netty context.
     */
    private void conditionalFlush() {
        if (currentQueue.needsFlush()) {
            if (shutdownOffset != null || parent.getChannel().isWritable()) {
                scheduleFlush();
            } else {
                LOG.debug("Channel {} is not I/O ready, not scheduling a flush", parent.getChannel());
            }
        } else {
            LOG.trace("Queue is empty, no flush needed");
        }
    }

    private void conditionalFlush(final ChannelHandlerContext ctx) {
        Preconditions.checkState(ctx.channel().equals(parent.getChannel()), "Inconsistent channel %s with context %s", parent.getChannel(), ctx);
        conditionalFlush();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        conditionalFlush(ctx);
    }

    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        /*
         * Tune channel write buffering. We increase the writability window
         * to ensure we can flush an entire queue segment in one go. We definitely
         * want to keep the difference above 64k, as that will ensure we use jam-packed
         * TCP packets. UDP will fragment as appropriate.
         */
        ctx.channel().config().setWriteBufferHighWaterMark(DEFAULT_HIGH_WATERMARK);
        ctx.channel().config().setWriteBufferLowWaterMark(DEFAULT_LOW_WATERMARK);

        super.handlerAdded(ctx);
    }

    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);

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

        LOG.debug("Channel {} initiating shutdown...", parent.getChannel());

        /*
         * We are dealing with a multi-threaded shutdown, as the user may still
         * be reserving entries in the queue. We are executing in a netty thread,
         * so neither flush nor barrier can be running, which is good news.
         *
         * We will eat up all the slots in the queue here and mark the offset first
         * reserved offset and free up all the cached queues. We then schedule
         * the flush task, which will deal with the rest of the shutdown process.
         */
        shutdownOffset = currentQueue.startShutdown();
        if (slice != null) {
            slice.decRef();
            slice = null;
        }

        LOG.trace("Channel {} reserved all entries at offset {}", parent.getChannel(), shutdownOffset);
        scheduleFlush();
    }

    @Override
    public String toString() {
        return String.format("Channel %s queue [flushing=%s]", parent.getChannel(), flushScheduled.get());
    }

    void onEchoRequest(final EchoRequestMessage message) {
        final EchoReplyInput reply = new EchoReplyInputBuilder().setData(message.getData()).setVersion(message.getVersion()).setXid(message.getXid()).build();
        parent.getChannel().writeAndFlush(reply);
    }
}
