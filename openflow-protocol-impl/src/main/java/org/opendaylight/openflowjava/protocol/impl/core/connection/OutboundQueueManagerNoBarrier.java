/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import javax.annotation.Nonnull;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandler;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <T>
 */
public class OutboundQueueManagerNoBarrier<T extends OutboundQueueHandler> extends
        AbstractOutboundQueueManager<T, StackedOutboundQueueNoBarrier> {

    OutboundQueueManagerNoBarrier(final ConnectionAdapterImpl parent, final InetSocketAddress address, final T handler) {
        super(parent, address, handler);
    }

    @Override
    protected StackedOutboundQueueNoBarrier initializeStackedOutboudnqueue() {
        return new StackedOutboundQueueNoBarrier(this);
    }

    @Override
    protected Object makeMessageListenerWrapper(@Nonnull final OfHeader msg) {
        Preconditions.checkArgument(msg != null);

        final GenericFutureListener<Future<Void>> listener = choseListener(msg.getXid());

        if (address == null) {
            return new MessageListenerWrapper(msg, listener);
        }
        return new UdpMessageListenerWrapper(msg, listener, address);
    }

    private GenericFutureListener<Future<Void>> choseListener(final long xid) {
        if (currentQueue.uncompletedSegments.size() > 1) {
            return makeQueueCleanListener(xid);
        }
        return LOG_ENCODER_LISTENER;
    }

    static final Logger LOGGER = LoggerFactory.getLogger("LogEncoderListener");

    private GenericFutureListener<Future<Void>> makeQueueCleanListener(final long xid) {
        return new GenericFutureListener<Future<Void>>() {

            @Override
            public void operationComplete(final Future<Void> future) throws Exception {
                if (future.cause() != null) {
                    LOGGER.warn("Message encoding fail !", future.cause());
                }
                /* clean old uncompleted queue */
                currentQueue.completeOldSegments(xid);
            }
        };
    }

}
