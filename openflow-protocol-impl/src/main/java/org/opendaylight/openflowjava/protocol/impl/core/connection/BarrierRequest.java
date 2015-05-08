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

final class BarrierRequest extends AbstractRequest {
    private final BarrierBatch batch;

    private BarrierRequest(final BarrierBatch batch, final ByteBuf message) {
        super(message);
        this.batch = Preconditions.checkNotNull(batch);
    }

    @Override
    protected void onFailure(final Throwable cause) {
        batch.onFailure(cause);
    }

    @Override
    protected void onSuccess() {
        batch.onSuccess();

    }
}
