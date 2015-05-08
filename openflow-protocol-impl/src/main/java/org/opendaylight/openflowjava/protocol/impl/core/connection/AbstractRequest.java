/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

abstract class AbstractRequest implements GenericFutureListener<Future<Void>> {
    private final ByteBuf message;

    protected AbstractRequest(final ByteBuf message) {
        this.message = Preconditions.checkNotNull(message);
    }

    final ByteBuf getMessage() {
        return message;
    }

    @Override
    public final void operationComplete(final Future<Void> future) {
        if (future.isSuccess()) {
            onSuccess();
        } else {
            onFailure(future.cause());
        }
    }

    protected abstract long getXid();
    protected abstract void onFailure(final Throwable cause);
    protected abstract void onSuccess();
}
