/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;

import java.util.Collections;

import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

abstract class AbstractRpcChannelPromise<T> extends DefaultChannelPromise {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRpcChannelPromise.class);
    private final SettableFuture<RpcResult<T>> result = SettableFuture.create();
    private final String failureInfo;

    AbstractRpcChannelPromise(final String failureInfo, Channel channel) {
        super(channel);
        this.failureInfo = failureInfo;
    }

    protected final void failedRpc(final Throwable cause) {
        LOG.debug("operation failed");
        final RpcError rpcError = ConnectionAdapterImpl.buildRpcError(failureInfo, ErrorSeverity.ERROR, "check switch connection", cause);

        result.set(Rpcs.getRpcResult(
                false,
                (T)null,
                Collections.singletonList(rpcError)));
    }

    protected final void successfulRpc(final T value) {
        LOG.debug("operation complete");

        result.set(Rpcs.getRpcResult(
                true,
                value,
                Collections.<RpcError>emptyList()));
    }

    final ListenableFuture<RpcResult<T>> getResult() {
        return result;
    }

}
