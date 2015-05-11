/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.connection;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.FutureCallback;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

@Beta
public interface OutboundQueue {
    /**
     * Reserve an entry in the outbound queue.
     * @return XID for the new message, or null if the queue is full
     */
    Long reserveEntry();

    /**
     * Commit the specified offset using a message. Specified callback will
     * be invoked once the we have ascertained the message's correctness.
     *
     * @param xid Previously-reserved XID
     * @param message Message which should be sent out, or null if the reservation
     *                should be cancelled.
     * @param callback Callback to be invoked, or null if no callback should be invoked.
     * @throws IllegalArgumentException if the slot is already committed or was never reserved.
     */
    void commitEntry(@Nonnull Long xid, @Nullable OfHeader message, @Nullable FutureCallback<OfHeader> callback);
}
