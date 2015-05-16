/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.connection;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.Error;

/**
 * Exception which is used to report that a particular request failed on the
 * remote device (switch).
 *
 * Instances of this class never carry a stack trace, as they logically originated
 * outside of the current call path. They can have a no-null {@link #getCause()}.
 */
public final class OutboundQueueDeviceRequestFailed extends OutboundQueueException {
    private static final long serialVersionUID = 1L;
    private final Error error;

    public OutboundQueueDeviceRequestFailed(final String message, @Nonnull final Error error) {
        super(message);
        this.error = Preconditions.checkNotNull(error);
    }

    public OutboundQueueDeviceRequestFailed(final String message, @Nonnull final Error error, final Throwable cause) {
        super(message, cause);
        this.error = Preconditions.checkNotNull(error);
    }

    @Nonnull public Error getError() {
        return error;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
