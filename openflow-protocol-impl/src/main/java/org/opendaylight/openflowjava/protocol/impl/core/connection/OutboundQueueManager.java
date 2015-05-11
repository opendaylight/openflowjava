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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
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
    private final ConnectionAdapterImpl parent;
    private final InetSocketAddress address;
    private final long maxWorkTime;
    private final int queueSize;
    private final T handler;

    // Passed to executor to request triggering of flush
    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            flush();
        }
    };

    private OutboundQueueImpl currentQueue;
    private long lastBarrierNanos = System.nanoTime();
    private int nonBarrierMessages;

    /*
     * Instead of using an AtomicBoolean object, we use these two. It saves us
     * from allocating an extra object.
     */
    @SuppressWarnings("rawtypes")
    private static final AtomicIntegerFieldUpdater<OutboundQueueManager> FLUSH_SCHEDULED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(OutboundQueueManager.class, "flushScheduled");
    private volatile int flushScheduled = 0;

    OutboundQueueManager(final ConnectionAdapterImpl parent, final InetSocketAddress address, final T handler, final int queueSize) {
        this.parent = Preconditions.checkNotNull(parent);
        this.handler = Preconditions.checkNotNull(handler);
        Preconditions.checkArgument(queueSize >= 0);
        this.queueSize = queueSize;
        this.address = address;
        this.maxWorkTime = TimeUnit.MICROSECONDS.toNanos(DEFAULT_WORKTIME_MICROS);

        createQueue();
    }

    T getHandler() {
        return handler;
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
        final long baseXid = lastXid.getAndAdd(queueSize);

        final OutboundQueueImpl cached = queueCache.poll();
        final OutboundQueueImpl queue;
        if (cached != null) {
            queue = cached.reuse(baseXid);
            LOG.debug("Reusing queue {} as {}", cached, queue);
        } else {
            queue = new OutboundQueueImpl(this, baseXid, queueSize);
            LOG.debug("Allocated new queue {}", queue);
        }

        activeQueues.add(queue);
        currentQueue = queue;
        handler.onConnectionQueueChanged(queue);
    }

    OfHeader flushEntry() {
        if (currentQueue.isFlushed()) {
            LOG.debug("Queue {} is fully flushed", currentQueue);
            createQueue();
        }

        final OfHeader message = currentQueue.flushEntry();
        if (message == null) {
            return null;
        }

        if (message instanceof BarrierInput) {
            nonBarrierMessages = 0;
            lastBarrierNanos = System.nanoTime();
        } else {
            nonBarrierMessages++;
            if (nonBarrierMessages >= queueSize) {
                final Long xid = currentQueue.reserveEntry();
                if (xid != null) {
                    currentQueue.commitEntry(xid, handler.createBarrierRequest(xid), null);
                    LOG.debug("Scheduled barrier request after {} non-barrier messages", nonBarrierMessages);
                    nonBarrierMessages = 0;
                } else {
                    LOG.debug("Failed to schedule barrier request, will retry");
                }
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
        Iterator<OutboundQueueImpl> it = activeQueues.iterator();
        while (it.hasNext()) {
            final OutboundQueueImpl queue = it.next();
            final int offset = queue.completionOffset(message);

            if (offset >= 0) {
                LOG.debug("Queue {} accepted response {}", queue, message);

                // This has been a barrier request, we need to flush all
                // previous queues
                if (queue.isBarrier(offset) && activeQueues.size() > 1) {
                    LOG.debug("Queue {} indicated request was a barrier", queue);

                    it = activeQueues.iterator();
                    while (it.hasNext()) {
                        final OutboundQueueImpl q = it.next();
                        if (queue.equals(q)) {
                            // Ensures this iterator points to the same queue
                            break;
                        }

                        LOG.debug("Queue {} is implied finished", q);
                        q.completeAll();
                        it.remove();
                        retireQueue(q);
                    }
                }

                if (queue.isFinished()) {
                    LOG.debug("Queue {} is finished", queue);
                    it.remove();
                    retireQueue(queue);
                }

                return true;
            }
        }

        LOG.debug("Failed to find completion for message {}", message);
        return false;
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
        scheduleFlush(parent.getChannel().pipeline().lastContext().executor());
    }

    /**
     * Perform a single flush operation.
     */
    protected void flush() {
        final long start = System.nanoTime();
        final long deadline = start + maxWorkTime;

        LOG.debug("Dequeuing messages to channel {}", parent.getChannel());

        long messages = 0;
        for (;; ++messages) {
            if (!parent.getChannel().isWritable()) {
                LOG.trace("Channel is no longer writable");
                break;
            }

            final OfHeader message = flushEntry();
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

        scheduleFlush(parent.getChannel().pipeline().lastContext().executor());
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
}
