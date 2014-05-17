/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

/**
 * Channel handler which bypasses wraps on top of normal Netty pipeline, allowing
 * writes to be enqueued from any thread, it then schedules a task pipeline task,
 * which shuffles messages from the queue into the pipeline.
 *
 * Note this is an *Inbound* handler, as it reacts to channel writability changing,
 * which in the Netty vocabulary is an inbound event. This has already changed in
 * the Netty 5.0.0 API, where Handlers are unified.
 */
final class ChannelOutboundQueue extends ChannelInboundHandlerAdapter {
    public interface MessageHolder<T> {
        /**
         * Take ownership of the encapsulated promise. Guaranteed to be
         * called at most once.
         *
         * @return promise encapsulated in the holder, may be null
         * @throws IllegalStateException if the promise is no longer
         *         available (for example because it has already been taken).
         */
        ChannelPromise takePromise();

        /**
         * Take ownership of the encapsulated message. Guaranteed to be
         * called at most once.
         *
         * @return message encapsulated in the holder, may not be null
         * @throws IllegalStateException if the message is no longer
         *         available (for example because it has already been taken).
         */
        T takeMessage();
    }

    private static final long DEFAULT_WORKTIME_MICROS = TimeUnit.MILLISECONDS.toMicros(100);
    private static final Logger LOG = LoggerFactory.getLogger(ChannelOutboundQueue.class);

    // Passed to executor to request triggering of flush
    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            flush();
        }
    };

    /*
     * Instead of using an AtomicBoolean object, we use these two. Note that flushScheduled
     * seems to be unused, but we access it via the updater
     */
    private static final AtomicIntegerFieldUpdater<ChannelOutboundQueue> FLUSH_SCHEDULED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundQueue.class, "flushScheduled");
    private volatile int flushScheduled = 0;

    private final BlockingQueue<MessageHolder<?>> queue;
    private final Channel channel;
    private final long maxWorkTime;

    ChannelOutboundQueue(final Channel channel, final int queueDepth) {
        Preconditions.checkArgument(queueDepth > 0, "Queue depth has to be positive");
        this.queue = new ArrayBlockingQueue<>(queueDepth, true);
        this.channel = Preconditions.checkNotNull(channel);
        this.maxWorkTime = DEFAULT_WORKTIME_MICROS;
    }

    public void enqueue(final MessageHolder<?> promise) throws InterruptedException {
        LOG.trace("Enqueuing message {}", promise);
        queue.put(promise);
        LOG.trace("Message enqueued");

        conditionalFlush();
    }

    /**
     * Schedule a queue flush if it is not empty and the channel is found
     * to be writable.
     */
    private void conditionalFlush() {
        if (queue.isEmpty()) {
            LOG.trace("Queue is empty, not flush needed");
            return;
        }
        if (!channel.isWritable()) {
            LOG.trace("Channel {} is not writable, not issuing a flush", channel);
            return;
        }

        scheduleFlush(channel.pipeline().lastContext().executor());
    }

    /*
     * The synchronized keyword should be unnecessary, really, but it enforces
     * queue order should something go terribly wrong. It should be completely
     * uncontended.
     */
    private synchronized void flush() {
        final Stopwatch w = new Stopwatch().start();

        LOG.debug("Dequeuing messages to channel {}", channel);

        long messages = 0;
        for (;; ++messages) {
            if (!channel.isWritable()) {
                LOG.trace("Channel is no longer writable");
                break;
            }
            if (w.elapsed(TimeUnit.MICROSECONDS) >= maxWorkTime) {
                LOG.trace("Exceeded allotted work time {}us", maxWorkTime);
                break;
            }

            final MessageHolder<?> h = queue.poll();
            if (h == null) {
                LOG.trace("The queue is completely drained");
                break;
            }

            final ChannelPromise p = h.takePromise();
            if (p != null) {
                channel.write(h.takeMessage(), p);
            } else {
                channel.write(h.takeMessage());
            }
        }

        if (messages > 0) {
            LOG.debug("Flushing {} message(s) to channel {}", messages, channel);
            channel.flush();
        }

        w.stop();
        LOG.debug("Flushed {} messages in {}us to channel {}",
                messages, w.elapsed(TimeUnit.MICROSECONDS), channel);

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
            LOG.warn("Channel {} queue {} flusher found unscheduled", channel, queue);
        }

        conditionalFlush();
    }

    private void scheduleFlush(final EventExecutor executor) {
        if (FLUSH_SCHEDULED_UPDATER.compareAndSet(this, 0, 1)) {
            executor.execute(flushRunnable);
        } else {
            LOG.trace("Flush task is already present");
        }
    }

    private void scheduleFlush(final ChannelHandlerContext ctx) {
        Preconditions.checkState(ctx.channel() == channel, "Inconsistent channel %s with context %s", channel, ctx);
        scheduleFlush(ctx.pipeline().lastContext().executor());
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        scheduleFlush(ctx);
    }

    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) {
        scheduleFlush(ctx);
    }

    @Override
    public String toString() {
        return String.format("Channel %s queue [%s messages flushing=%s]", channel, queue.size(), flushScheduled);
    }
}
