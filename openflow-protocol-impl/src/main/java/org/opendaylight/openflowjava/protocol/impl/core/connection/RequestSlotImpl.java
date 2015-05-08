/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.connection.RequestSlot;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class RequestSlotImpl implements RequestSlot {
    private static final Logger LOG = LoggerFactory.getLogger(RequestSlotImpl.class);
    private final BarrierBatchBuffer parent;
    private final BarrierBatch batch;
    private final int offset;

    RequestSlotImpl(final BarrierBatchBuffer parent, final BarrierBatch batch, final int offset) {
        this.parent = Preconditions.checkNotNull(parent);
        this.batch = Preconditions.checkNotNull(batch);
        this.offset = offset;
    }

    @Override
    public void commit(final OfHeader message, final FutureCallback<?> callback) {
        final ByteBuf buf;
        try {
            buf = parent.serialize(message);
        } catch (Exception e) {
            LOG.warn("Failed to serialize outgoing message {}", message);
            callback.onFailure(e);
            return;
        }

        final AbstractRequest request = new AbstractRequest(buf) {
            @Override
            protected void onSuccess() {
                callback.onSuccess(null);
            }

            @Override
            protected void onFailure(final Throwable cause) {
                callback.onFailure(cause);
            }

            @Override
            protected long getXid() {
                return message.getXid();
            }
        };

        LOG.debug("Committing request {} for {}@{}", request, offset, batch);
        batch.commitRequest(offset, request);
        parent.scheduleFlush();
        LOG.debug("Finished request for {}@[]", offset, batch);
    }
}
