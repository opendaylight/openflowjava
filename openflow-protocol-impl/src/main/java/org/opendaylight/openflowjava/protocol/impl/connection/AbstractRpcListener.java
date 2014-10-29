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

import org.opendaylight.controller.sal.common.util.RpcErrors;
import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * This class holds all the context we need for sending a single message down the tube.
 * A MessageHolder (used in queue) and the actual listener. It is not a thing of beauty,
 * but it keeps us from allocating unnecessary objects in the egress path.
 */
abstract class AbstractRpcListener<T> implements GenericFutureListener<Future<Void>>, ChannelOutboundQueue.MessageHolder<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRpcListener.class);
    private static final String APPLICATION_TAG = "OPENFLOW_LIBRARY";
    private static final String TAG = "OPENFLOW";
    private final SettableFuture<RpcResult<T>> result = SettableFuture.create();
    private final String failureInfo;
    private Object message;

    /**
     * Create RcpError object
     * @param info
     * @param severity - error severity
     * @param message
     * @param cause - details of reason
     * @return
     */
    static RpcError buildRpcError(final String info, final ErrorSeverity severity, final String message,
            final Throwable cause) {
        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message,
                ErrorType.RPC, cause);
        return error;
    }

    AbstractRpcListener(final Object message, final String failureInfo) {
        this.failureInfo = Preconditions.checkNotNull(failureInfo);
        this.message = Preconditions.checkNotNull(message);
    }

    public final ListenableFuture<RpcResult<T>> getResult() {
        return result;
    }

    @Override
    public final void operationComplete(final Future<Void> future) {
        if (!future.isSuccess()) {
            LOG.debug("operation failed");
            failedRpc(future.cause());
        } else {
            LOG.debug("operation complete");
            operationSuccessful();
        }
    }

    @Override
    public final Object takeMessage() {
        final Object ret = message;
        Preconditions.checkState(ret != null, "Message has already been taken");
        message = null;
        return ret;
    }

    @Override
    public final GenericFutureListener<Future<Void>> takeListener() {
        return this;
    }

    protected abstract void operationSuccessful();

    protected final void failedRpc(final Throwable cause) {
        final RpcError rpcError = buildRpcError(
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
