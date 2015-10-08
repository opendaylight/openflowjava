/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.util.concurrent.FutureCallback;
import io.netty.channel.Channel;
import java.util.Iterator;
import javax.annotation.Nonnull;
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
        final OutboundQueueEntry entry = getEntry(xid);

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
    int writeEntries(@Nonnull final Channel channel, final long now) {
        final int entries = super.writeEntries(channel, now);
        if (uncompletedSegments.size() > 1) {
            final Iterator<StackedSegment> iter = uncompletedSegments.iterator();
            while (iter.hasNext()) {
                final StackedSegment completeSegment = iter.next();
                if (iter.hasNext()) {
                    completeSegment.completeAll();
                    iter.remove();
                    completeSegment.recycle();
                }
            }
        }

        return entries;
    }
}
