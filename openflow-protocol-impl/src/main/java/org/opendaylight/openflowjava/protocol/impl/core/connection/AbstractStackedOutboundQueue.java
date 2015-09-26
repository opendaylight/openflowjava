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
import javax.annotation.Nonnull;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

abstract class AbstractStackedOutboundQueue implements OutboundQueue {

    protected final AbstractOutboundQueueManager<?, ?> manager;

    AbstractStackedOutboundQueue(final AbstractOutboundQueueManager<?, ?> manager) {
        this.manager = Preconditions.checkNotNull(manager);
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

    abstract boolean needsFlush();

    abstract long startShutdown(final Channel channel);

    abstract boolean finishShutdown();
}
