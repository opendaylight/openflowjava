/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collections;

import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

abstract class AbstractRpcListener<T> implements GenericFutureListener<Future<? super Void>> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRpcListener.class);
    private final SettableFuture<RpcResult<T>> result = SettableFuture.create();
    private final String failureInfo;

    AbstractRpcListener(final String failureInfo) {
        this.failureInfo = failureInfo;
    }

    public final ListenableFuture<RpcResult<T>> getResult() {
        return result;
    }

    @Override
    public final void operationComplete(final Future<? super Void> future) {
        if (!future.isSuccess()) {
            LOG.debug("operation failed");
            failedRpc(future.cause());
        } else {
            LOG.debug("operation complete");
            operationSuccessful();
        }
    }

    abstract protected void operationSuccessful();

    protected final void failedRpc(final Throwable cause) {
        final RpcError rpcError = ConnectionAdapterImpl.buildRpcError(
                failureInfo, ErrorSeverity.ERROR, "check switch connection", cause);

        result.set(Rpcs.getRpcResult(
                false,
                (T)null,
                Collections.singletonList(rpcError)));
    }

    protected final void successfulRpc(final T value) {

        result.set(Rpcs.getRpcResult(
                true,
                value,
                Collections.<RpcError>emptyList()));
    }

}
