/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import javax.annotation.Nonnull;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueImpl implements OutboundQueue {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueImpl.class);
    private static final AtomicIntegerFieldUpdater<OutboundQueueImpl> CURRENT_OFFSET_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(OutboundQueueImpl.class, "currentOffset");
    private static final long FLUSH_RETRY_NANOS = 1;
    private final OutboundQueueManager<?> manager;
    private final OutboundQueueEntry[] queue;
    private final long baseXid;

    // Updated concurrently
    private volatile int reserveOffset;

    // Updated from Netty only
    private int flushOffset;
    private int completeCount;

    OutboundQueueImpl(final OutboundQueueManager<?> manager, final long baseXid, final int maxQueue) {
        Preconditions.checkArgument(maxQueue > 0);
        this.baseXid = baseXid;
        this.manager = Preconditions.checkNotNull(manager);
        queue = new OutboundQueueEntry[maxQueue];
        for (int i = 0; i < maxQueue; ++i) {
            queue[i] = new OutboundQueueEntry();
        }
    }

    private OutboundQueueImpl(final OutboundQueueManager<?> manager, final long baseXid, final OutboundQueueEntry[] queue) {
        this.baseXid = baseXid;
        this.manager = Preconditions.checkNotNull(manager);
        this.queue = Preconditions.checkNotNull(queue);
        for (OutboundQueueEntry element : queue) {
            element.reset();
        }
    }

    OutboundQueueImpl reuse(final long baseXid) {
        return new OutboundQueueImpl(manager, baseXid, queue);
    }

    @Override
    public Long reserveEntry() {
        final int offset = CURRENT_OFFSET_UPDATER.getAndIncrement(this);
        if (offset >= queue.length) {
            LOG.debug("Queue {} offset {}/{}, not allowing reservation", this, offset, queue.length);
            return null;
        }

        final Long xid = baseXid + offset;
        LOG.debug("Queue {} allocated XID {} at offset {}", this, xid, offset);
        return xid;
    }

    @Override
    public void commitEntry(final Long xid, final OfHeader message, final FutureCallback<OfHeader> callback) {
        final int offset = (int)(xid - baseXid);
        if (message != null) {
            Preconditions.checkArgument(xid.equals(message.getXid()), "Message %s has wrong XID %s, expected %s", message, message.getXid(), xid);
        }

        queue[offset].commit(message, callback);
        LOG.debug("Queue {} XID {} at offset {} (of {}) committed", this, xid, offset, reserveOffset);

        manager.ensureFlushing(this);
    }

    boolean isBarrier(final int offset) {
        return queue[offset].getMessage() instanceof BarrierInput;
    }

    /**
     * An empty queue is a queue which has no further unflushed entries.
     *
     * @return True if this queue does not have unprocessed entries.
     */
    boolean isEmpty() {
        return reserveOffset <= flushOffset;
    }

    /**
     * A queue is finished when all of its entries have been completed.
     *
     * @return False if there are any uncompleted requests.
     */
    boolean isFinished() {
        return completeCount == queue.length;
    }

    boolean isFlushed() {
        return flushOffset >= queue.length;
    }

    OfHeader flushEntry() {
        for (;;) {
            // No message ready
            if (isEmpty()) {
                LOG.debug("Flush offset {} is uptodate with reserve", flushOffset);
                return null;
            }

            boolean retry = true;
            while (!queue[flushOffset].isCommitted()) {
                if (!retry) {
                    LOG.debug("Offset {} not ready yet, giving up", flushOffset);
                    return null;
                }

                LOG.debug("Offset {} not ready yet, retrying", flushOffset);
                LockSupport.parkNanos(FLUSH_RETRY_NANOS);
                retry = false;
            }

            final OfHeader msg = queue[flushOffset++].getMessage();
            if (msg != null) {
                return msg;
            }
        }
    }

    /**
     * Return the offset of the message completed by the response.
     *
     * @param response Response message
     * @return Offset of completed message, or -1 if no match is found
     */
    int completionOffset(@Nonnull final OfHeader response) {
        final Long xid = response.getXid();

        // FIXME: deal with wraparound, too
        if (xid < baseXid || xid >= baseXid + queue.length) {
            LOG.debug("Queue {} {}/{} ignoring XID {}", this, baseXid, queue.length, xid);
            return -1;
        }

        final int offset = (int)(xid - baseXid);
        if (!queue[offset].complete(response)) {
            return -1;
        }

        completeCount++;

        // This has been a barrier -- make sure we complete all preceding requests
        if (isBarrier(offset)) {
            LOG.debug("Barrier completed, cascading requests {} to {}", baseXid, xid);
            for (int i = 0; i < offset; ++i) {
                if (queue[i].complete(null)) {
                    completeCount++;
                }
            }
        }
        return offset;
    }

    void completeAll() {
        for (OutboundQueueEntry entry : queue) {
            if (entry.complete(null)) {
                completeCount++;
            }
        }
    }

    int failAll(final Throwable cause) {
        int ret = 0;
        for (OutboundQueueEntry entry : queue) {
            if (!entry.isCompleted()) {
                entry.fail(cause);
                ret++;
            }
        }

        return ret;
    }
}
