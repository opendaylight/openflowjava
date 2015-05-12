/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutor;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandler;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
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
     * We maintain a cache of this many previous queues for later reuse.
     */
    private static final int QUEUE_CACHE_SIZE = 4;

    private final Queue<OutboundQueueImpl> queueCache = new ArrayDeque<>(QUEUE_CACHE_SIZE);
    private final Queue<OutboundQueueImpl> activeQueues = new LinkedList<>();
    private final AtomicLong lastXid = new AtomicLong();
    private final SerializationFactory serialiationFactory;
    private final ConnectionAdapterImpl parent;
    private final InetSocketAddress address;
    private final long maxPeriodicBarrierNanos;
    private final long maxFlushNanos;
    private final int queueSize;
    private final T handler;

    private long lastBarrierNanos = System.nanoTime();
    private OutboundQueueImpl currentQueue;
    private int nonBarrierMessages;

    /*
     * Instead of using an AtomicBoolean object, we use these two. It saves us
     * from allocating an extra object.
     */
    @SuppressWarnings("rawtypes")
    private static final AtomicIntegerFieldUpdater<OutboundQueueManager> FLUSH_SCHEDULED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(OutboundQueueManager.class, "flushScheduled");
    private volatile int flushScheduled = 0;

    // Passed to executor to request triggering of flush
    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            flush();
        }
    };
    private final Runnable barrierRunnable = new Runnable() {
        @Override
        public void run() {
            barrier();
        }
    };

    OutboundQueueManager(final ConnectionAdapterImpl parent, final InetSocketAddress address, final T handler,
        final int queueSize, final long maxBarrierNanos) {
        this.parent = Preconditions.checkNotNull(parent);
        this.handler = Preconditions.checkNotNull(handler);
        Preconditions.checkArgument(queueSize > 0);
        this.queueSize = queueSize;
        Preconditions.checkArgument(maxBarrierNanos > 0);
        this.maxFlushNanos = TimeUnit.MICROSECONDS.toNanos(DEFAULT_WORKTIME_MICROS);
        this.maxPeriodicBarrierNanos = maxBarrierNanos;
        this.address = address;

        createQueue();
        scheduleBarrierTimer(lastBarrierNanos);
    }

    T getHandler() {
        return handler;
    }

    SerializationFactory getSerializationFactory() {
        return serialiationFactory;
    }

    @Override
    public void close() {
        handler.onConnectionQueueChanged(null);
    }

    private void retireQueue(final OutboundQueueImpl queue) {
        if (queueCache.offer(queue)) {
            LOG.debug("Saving queue {} for later reuse", queue);
        } else {
            LOG.debug("Queue {} thrown away", queue);
        }
    }

    private void createQueue() {
        final long baseXid = lastXid.getAndAdd(queueSize + 1);

        final OutboundQueueImpl cached = queueCache.poll();
        final OutboundQueueImpl queue;
        if (cached != null) {
            queue = cached.reuse(baseXid);
            LOG.debug("Reusing queue {} as {}", cached, queue);
        } else {
            queue = new OutboundQueueImpl(this, baseXid, queueSize + 1);
            LOG.debug("Allocated new queue {}", queue);
        }

        activeQueues.add(queue);
        currentQueue = queue;
        handler.onConnectionQueueChanged(queue);
    }

    private void scheduleBarrierTimer(final long now) {
        long next = lastBarrierNanos + maxPeriodicBarrierNanos;
        if (next < now) {
            next = now;
        }

        final long delay = next - now;
        LOG.debug("Scheduling barrier timer {}us from now", TimeUnit.NANOSECONDS.toMicros(delay));
        parent.getChannel().eventLoop().schedule(barrierRunnable, next - now, TimeUnit.NANOSECONDS);
    }

    private void scheduleBarrierMessage() {
        final Long xid = currentQueue.reserveEntry(true);
        Verify.verifyNotNull(xid);

        currentQueue.commitEntry(xid, handler.createBarrierRequest(xid), null);
        LOG.debug("Scheduled barrier request after {} non-barrier messages", nonBarrierMessages);

        // We can see into the future when compared to flushEntry(), as that
        // codepath may be lagging behind on messages. Resetting the counter
        // here ensures that flushEntry() will not attempt to issue a flush
        // request. Note that we do not reset current time, as that should
        // reflect when we sent the message for real.
        nonBarrierMessages = 0;
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
    OutboundQueueEntry flushEntry(final long now) {
        if (currentQueue.isFlushed()) {
            LOG.debug("Queue {} is fully flushed", currentQueue);
            createQueue();
        }

        final OutboundQueueEntry entry = currentQueue.flushEntry();
        if (entry == null) {
            return null;
        }

        if (!entry.isBarrier()) {
            nonBarrierMessages++;
            if (nonBarrierMessages >= queueSize) {
                scheduleBarrierMessage();
            }
        } else {
            nonBarrierMessages = 0;
            lastBarrierNanos = now;
        }

        return entry;
    }

    /**
     * Invoked whenever a message comes in from the switch. Runs matching
     * on all active queues in an attempt to complete a previous request.
     *
     * @param message Potential response message
     * @return True if the message matched a previous request, false otherwise.
     */
    void onMessage(final OfHeader message) {
        LOG.debug("Attempting to pair message {} to a request", message);

        Iterator<OutboundQueueImpl> it = activeQueues.iterator();
        while (it.hasNext()) {
            final OutboundQueueImpl queue = it.next();
            final OutboundQueueEntry entry = queue.findRequest(message);

            if (entry == null) {
                continue;
            }

            LOG.debug("Queue {} accepted response {}", queue, message);

            // This has been a barrier request, we need to flush all
            // previous queues
            if (entry.isBarrier() && activeQueues.size() > 1) {
                LOG.debug("Queue {} indicated request was a barrier", queue);

                it = activeQueues.iterator();
                while (it.hasNext()) {
                    final OutboundQueueImpl q = it.next();

                    // We want to complete all queues before the current one, we will
                    // complete the current queue below
                    if (!queue.equals(q)) {
                        LOG.debug("Queue {} is implied finished", q);
                        q.completeAll();
                        it.remove();
                        retireQueue(q);
                    } else {
                        break;
                    }
                }
            }

            if (queue.isFinished()) {
                LOG.debug("Queue {} is finished", queue);
                it.remove();
                retireQueue(queue);
            }

            return;
        }

        LOG.debug("Failed to find completion for message {}", message);
    }

    private void scheduleFlush(final EventExecutor executor) {
        if (FLUSH_SCHEDULED_UPDATER.compareAndSet(this, 0, 1)) {
            LOG.trace("Scheduling flush task");
            executor.execute(flushRunnable);
        } else {
            LOG.trace("Flush task is already present");
        }
    }

    void ensureFlushing(final OutboundQueueImpl queue) {
        Preconditions.checkState(currentQueue.equals(queue));
        scheduleFlush(parent.getChannel().eventLoop());
    }

    /**
     * Periodic barrier check.
     */
    protected void barrier() {
        LOG.debug("Channel {} barrier timer expired", parent.getChannel());
        if (currentQueue == null) {
            LOG.debug("Channel shut down, not processing barrier");
            return;
        }

        final long now = System.nanoTime();
        final long sinceLast = now - lastBarrierNanos;
        if (sinceLast >= maxPeriodicBarrierNanos) {
            scheduleBarrierMessage();
        }

        scheduleBarrierTimer(now);
    }

    /**
     * Perform a single flush operation.
     */
    protected void flush() {
        final long start = System.nanoTime();
        final long deadline = start + maxFlushNanos;

        LOG.debug("Dequeuing messages to channel {}", parent.getChannel());

        final ChannelHandlerContext ctx = null;

        long messages = 0;
        for (;; ++messages) {
            if (!parent.getChannel().isWritable()) {
                LOG.trace("Channel is no longer writable");
                break;
            }

            final OutboundQueueEntry entry = flushEntry(start);
            if (entry == null) {
                LOG.trace("The queue is completely drained");
                break;
            }

            ctx.write(entry.getPdu());

            /*
             * Check every WORKTIME_RECHECK_MSGS for exceeded time.
             *
             * XXX: given we already measure our flushing throughput, we
             *      should be able to perform dynamic adjustments here.
             *      is that additional complexity needed, though?
             */
            if ((messages % WORKTIME_RECHECK_MSGS) == 0 && System.nanoTime() >= deadline) {
                LOG.trace("Exceeded allotted work time {}us",
                        TimeUnit.NANOSECONDS.toMicros(maxFlushNanos));
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

        /*
         * We are almost ready to terminate. This is a bit tricky, because
         * we do not want to have a race window where a message would be
         * stuck on the queue without a flush being scheduled.
         *
         * So we mark ourselves as not running and then re-check if a
         * flush out is needed. That will re-synchronized with other threads
         * such that only one flush is scheduled at any given time.
         */
        if (!FLUSH_SCHEDULED_UPDATER.compareAndSet(this, 1, 0)) {
            LOG.warn("Channel {} queue {} flusher found unscheduled", parent.getChannel(), this);
        }

        conditionalFlush();
    }


    /**
     * Schedule a queue flush if it is not empty and the channel is found
     * to be writable.
     */
    private void conditionalFlush() {
        if (currentQueue.isEmpty()) {
            LOG.trace("Queue is empty, not flush needed");
            return;
        }
        if (!parent.getChannel().isWritable()) {
            LOG.trace("Channel {} is not writable, not issuing a flush", parent.getChannel());
            return;
        }

        scheduleFlush(parent.getChannel().eventLoop());
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

    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        conditionalFlush(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        long entries = 0;
        LOG.debug("Channel shutdown, flushing queue...");
        handler.onConnectionQueueChanged(null);

        final Throwable cause = new RejectedExecutionException("Channel disconnected");
        for (OutboundQueueImpl queue : activeQueues) {
            entries += queue.failAll(cause);
        }
        activeQueues.clear();

        LOG.debug("Flushed {} queue entries", entries);
    }

    @Override
    public String toString() {
        return String.format("Channel %s queue [flushing=%s]", parent.getChannel(), flushScheduled);
    }

    InetSocketAddress getAddress() {
        return address;
    }
}
