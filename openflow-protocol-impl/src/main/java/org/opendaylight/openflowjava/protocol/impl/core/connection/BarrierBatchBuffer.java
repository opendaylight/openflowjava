/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import javax.annotation.Nonnull;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BarrierBatchBuffer {
    private static final Logger LOG = LoggerFactory.getLogger(BarrierBatchBuffer.class);
    private static final AtomicIntegerFieldUpdater<BarrierBatchBuffer> FLUSH_SCHEDULED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(BarrierBatchBuffer.class, "flushScheduled");
    /**
     * This is the default upper bound we place on the flush task running
     * a single iteration. We relinquish control after about this amount
     * of time.
     */
    private static final long DEFAULT_WORKTIME_MICROS = TimeUnit.MILLISECONDS.toMicros(100);

    private static final long RESERVE_PARK_NANOS = 0;

    /**
     * We re-check the time spent flushing every this many messages. We do this because
     * checking after each message may prove to be CPU-intensive. Set to Integer.MAX_VALUE
     * or similar to disable the feature.
     */
    private static final int WORKTIME_RECHECK_MSGS = 64;

    // Passed to executor to request triggering of flush
    private final Runnable flushRunnable = new Runnable() {
        @Override
        public void run() {
            BarrierBatchBuffer.this.flush();
        }
    };
    private final SerializationFactory serializationFactory;
    private final InetSocketAddress address = null;
    private final Channel channel;
    private final long maxWorkTime;
    private volatile BarrierBatch currentBatch;
    @SuppressWarnings("unused")
    private volatile int flushScheduled;

    BarrierBatchBuffer(final SerializationFactory serializationFactory, final Channel channel, final int maxBatchSize) {
        this.serializationFactory = Preconditions.checkNotNull(serializationFactory);
        this.channel = Preconditions.checkNotNull(channel);
        this.currentBatch = BarrierBatch.initial(maxBatchSize);
        this.maxWorkTime = TimeUnit.MICROSECONDS.toNanos(DEFAULT_WORKTIME_MICROS);
    }

    /**
     * Flush task. Responsible for writing the contents of BarrierBatch
     * into the channel, enqueueing the appropriate barrier message and
     * allocating the next BarrierBatch.
     */
    void flush() {
        final long start = System.nanoTime();
        final long deadline = start + maxWorkTime;

        LOG.debug("Dequeuing messages to channel {}", channel);

        long messages = 0;
        BarrierBatch batch = currentBatch;
        for (;; ++messages) {
            if (!channel.isWritable()) {
                LOG.trace("Channel is no longer writable");
                break;
            }

            if (batch.isComplete()) {
                batch = batch.createNextBatch();
                currentBatch = batch;
                LOG.debug("Next barrier batch prepared");
                continue;
            }

            final boolean progress = batch.writeRequest(channel, address);
            if (!progress) {
                LOG.trace("Batch {} reported no progress, yielding task");
                break;
            }

            /*
             * Check every WORKTIME_RECHECK_MSGS for exceeded time.
             *
             * XXX: given we already measure our flushing throughput, we
             *      should be able to perform dynamic adjustments here.
             *      is that additional complexity needed, though?
             */
            if ((messages % WORKTIME_RECHECK_MSGS) == 0 && System.nanoTime() >= deadline) {
                LOG.trace("Exceeded allotted work time {}us", TimeUnit.NANOSECONDS.toMicros(maxWorkTime));
                break;
            }
        }

        if (messages > 0) {
            LOG.debug("Flushing {} message(s) to channel {}", messages, channel);
            channel.flush();
        }

        // Skip nanoTime() and micros computation
        if (LOG.isDebugEnabled()) {
            final long stop = System.nanoTime();
            LOG.debug("Flushed {} messages in {}us to channel {}",
                messages, TimeUnit.NANOSECONDS.toMicros(stop - start), channel);
        }

        /*
         * We are almost ready to terminate. This is a bit tricky, because
         * we do not want to have a race window where a message would be
         * stuck on the queue without a flush being scheduled.
         *
         * So we mark ourselves as not running and then re-check if a
         * flush out is needed. That will re-synchronize with other threads
         * such that only one flush is scheduled at any given time.
         */
        if (!FLUSH_SCHEDULED_UPDATER.compareAndSet(this, 1, 0)) {
            LOG.warn("Channel {} flusher found unscheduled", channel);
        }

        conditionalFlush(batch);
    }

    /**
     * Schedule a queue flush if it is not empty and the channel is found
     * to be writable.
     */
    private void conditionalFlush(final BarrierBatch batch) {
        if (batch.isEmpty()) {
            LOG.trace("Queue is empty, no flush needed");
            return;
        }
        if (!channel.isWritable()) {
            LOG.trace("Channel {} is not writable, not issuing a flush", channel);
            return;
        }

        scheduleFlush();
    }

    void scheduleFlush() {
        if (FLUSH_SCHEDULED_UPDATER.compareAndSet(this, 0, 1)) {
            LOG.trace("Scheduling flush task");
            channel.pipeline().lastContext().executor().execute(flushRunnable);
        } else {
            LOG.trace("Flush task is already present");
        }
    }

    RequestSlotImpl reserve() {
        for (;;) {
            final BarrierBatch batch = currentBatch;
            if (batch == null) {
                LOG.debug("Buffer {} is shutting down, failing reserve", this);
                return null;
            }

            final int offset = batch.reserveRequest();
            if (offset < 0) {
                // Current batch is full, make sure flush() is scheduled and retry
                LOG.trace("Buffer {} batch is full, do we have a flush task?", this);
                scheduleFlush();
                LockSupport.parkNanos(RESERVE_PARK_NANOS);
                LOG.trace("Retrying buffer {}", this);
                continue;
            }

            return new RequestSlotImpl(this, batch, offset);
        }
    }

    @Nonnull ByteBuf serialize(final OfHeader message) throws Exception {
        final ByteBuf ret = channel.alloc().buffer();
        serializationFactory.messageToBuffer(message.getVersion(), ret, message);
        return ret;
    }
}
