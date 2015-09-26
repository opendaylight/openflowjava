/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueue;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractStackedOutboundQueue implements OutboundQueue {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractStackedOutboundQueue.class);

    protected static final AtomicLongFieldUpdater<AbstractStackedOutboundQueue> LAST_XID_UPDATER = AtomicLongFieldUpdater
            .newUpdater(AbstractStackedOutboundQueue.class, "lastXid");

    @GuardedBy("unflushedSegments")
    protected volatile StackedSegment firstSegment;
    @GuardedBy("unflushedSegments")
    protected final List<StackedSegment> unflushedSegments = new ArrayList<>(2);
    @GuardedBy("unflushedSegments")
    protected final List<StackedSegment> uncompletedSegments = new ArrayList<>(2);

    private volatile long lastXid = -1;

    @GuardedBy("unflushedSegments")
    protected Integer shutdownOffset;

    // Accessed from Netty only
    protected int flushOffset;

    protected final AbstractOutboundQueueManager<?, ?> manager;

    AbstractStackedOutboundQueue(final AbstractOutboundQueueManager<?, ?> manager) {
        this.manager = Preconditions.checkNotNull(manager);
        firstSegment = StackedSegment.create(0L);
        uncompletedSegments.add(firstSegment);
        unflushedSegments.add(firstSegment);
    }

    /**
     * Write some entries from the queue to the channel. Guaranteed to run
     * in the corresponding EventLoop.
     *
     * @param channel Channel onto which we are writing
     * @param now
     * @return Number of entries written out
     */
    abstract int writeEntries(@Nonnull final Channel channel, final long now);

    abstract boolean pairRequest(final OfHeader message);

    boolean needsFlush() {
        // flushOffset always points to the first entry, which can be changed only
        // from Netty, so we are fine here.
        if (firstSegment.getBaseXid() + flushOffset > lastXid) {
            return false;
        }

        if (shutdownOffset != null && flushOffset >= shutdownOffset) {
            return false;
        }

        return firstSegment.getEntry(flushOffset).isCommitted();
    }

    long startShutdown(final Channel channel) {
        /*
         * We are dealing with a multi-threaded shutdown, as the user may still
         * be reserving entries in the queue. We are executing in a netty thread,
         * so neither flush nor barrier can be running, which is good news.
         * We will eat up all the slots in the queue here and mark the offset first
         * reserved offset and free up all the cached queues. We then schedule
         * the flush task, which will deal with the rest of the shutdown process.
         */
        synchronized (unflushedSegments) {
            // Increment the offset by the segment size, preventing fast path allocations,
            // since we are holding the slow path lock, any reservations will see the queue
            // in shutdown and fail accordingly.
            final long xid = LAST_XID_UPDATER.addAndGet(this, StackedSegment.SEGMENT_SIZE);
            shutdownOffset = (int) (xid - firstSegment.getBaseXid() - StackedSegment.SEGMENT_SIZE);

            return lockedShutdownFlush();
        }
    }

    boolean finishShutdown() {
        synchronized (unflushedSegments) {
            lockedShutdownFlush();
        }

        return !needsFlush();
    }

    @GuardedBy("unflushedSegments")
    private long lockedShutdownFlush() {
        long entries = 0;

        // Fail all queues
        final Iterator<StackedSegment> it = uncompletedSegments.iterator();
        while (it.hasNext()) {
            final StackedSegment segment = it.next();

            entries += segment.failAll(OutboundQueueException.DEVICE_DISCONNECTED);
            if (segment.isComplete()) {
                LOG.trace("Cleared segment {}", segment);
                it.remove();
            }
        }

        return entries;
    }
}
