/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import java.util.Iterator;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is designed for stacking Statistics and propagate immediate response for all
 * another requests.
 */
public class StackedOutboundQueueNoBarrier extends AbstractStackedOutboundQueue {

    private static final Logger LOG = LoggerFactory.getLogger(StackedOutboundQueueNoBarrier.class);

    StackedOutboundQueueNoBarrier(final AbstractOutboundQueueManager<?, ?> manager) {
        super(manager);
    }

    /*
     * This method is expected to be called from multiple threads concurrently
     */
    @Override
    public void commitEntry(final Long xid, final OfHeader message, final FutureCallback<OfHeader> callback) {
        final StackedSegment fastSegment = firstSegment;
        final long calcOffset = xid - fastSegment.getBaseXid();
        Preconditions.checkArgument(calcOffset >= 0, "Commit of XID %s does not match up with base XID %s", xid, fastSegment.getBaseXid());

        Verify.verify(calcOffset <= Integer.MAX_VALUE);
        final int fastOffset = (int) calcOffset;

        final OutboundQueueEntry entry;
        if (fastOffset >= StackedSegment.SEGMENT_SIZE) {
            LOG.debug("Queue {} falling back to slow commit of XID {} at offset {}", this, xid, fastOffset);

            final StackedSegment segment;
            final int slowOffset;
            synchronized (unflushedSegments) {
                final StackedSegment slowSegment = firstSegment;
                final long slowCalcOffset = xid - slowSegment.getBaseXid();
                Verify.verify(slowCalcOffset >= 0 && slowCalcOffset <= Integer.MAX_VALUE);
                slowOffset = (int) slowCalcOffset;

                LOG.debug("Queue {} recalculated offset of XID {} to {}", this, xid, slowOffset);
                segment = unflushedSegments.get(slowOffset / StackedSegment.SEGMENT_SIZE);
            }

            final int segOffset = slowOffset % StackedSegment.SEGMENT_SIZE;
            entry = segment.getEntry(segOffset);
            LOG.debug("Queue {} slow commit of XID {} completed at offset {} (segment {} offset {})", this, xid, slowOffset, segment, segOffset);
        } else {
            entry = fastSegment.getEntry(fastOffset);
        }

        if (message instanceof FlowModInput) {
            callback.onSuccess(null);
            entry.commit(message, null);
        } else {
            entry.commit(message, callback);
        }

        LOG.trace("Queue {} committed XID {}", this, xid);
        manager.ensureFlushing();
    }

    @Override
    boolean pairRequest(final OfHeader message) {
        Iterator<StackedSegment> it = uncompletedSegments.iterator();
        while (it.hasNext()) {
            final StackedSegment queue = it.next();
            final OutboundQueueEntry entry = queue.pairRequest(message);
            if (entry == null) {
                continue;
            }

            LOG.trace("Queue {} accepted response {}", queue, message);

            // we need to flush all previous queues
            if (uncompletedSegments.size() > 1) {
                it = uncompletedSegments.iterator();
                while (it.hasNext()) {
                    final StackedSegment q = it.next();

                    // We want to complete all queues before the current one, we will
                    // complete the current queue below
                    if (!queue.equals(q)) {
                        LOG.trace("Queue {} is implied finished", q);
                        q.completeAll();
                        it.remove();
                        q.recycle();
                    } else {
                        break;
                    }
                }
            }

            if (queue.isComplete()) {
                LOG.trace("Queue {} is finished", queue);
                it.remove();
                queue.recycle();
            }

            return true;
        }

        LOG.debug("Failed to find completion for message {}", message);
        return false;
    }
}

