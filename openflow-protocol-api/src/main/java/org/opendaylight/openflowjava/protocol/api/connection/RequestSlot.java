/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.connection;

import com.google.common.util.concurrent.FutureCallback;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

public interface RequestSlot {
    /**
     * Commit this slot to contain specified message. Once the message completes processing,
     * specified callback will be invoked.
     *
     * @param message OpenFlow message
     * @param callback Completion callback
     */
    void commit(@Nonnull OfHeader message, @Nonnull FutureCallback<?> callback);
}
