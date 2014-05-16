/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

abstract class AbstractRpcChannelPromise<T> extends AbstractFuture<Void> implements ChannelPromise {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRpcChannelPromise.class);
    private final SettableFuture<RpcResult<T>> result = SettableFuture.create();
    private final String failureInfo;

    AbstractRpcChannelPromise(final String failureInfo) {
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

    @Override
    public final ChannelPromise addListener(
            GenericFutureListener<? extends Future<? super Void>> listener) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise addListeners(
            @SuppressWarnings("unchecked") GenericFutureListener<? extends Future<? super Void>>... listeners) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise removeListener(
            GenericFutureListener<? extends Future<? super Void>> listener) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise removeListeners(
            @SuppressWarnings("unchecked") GenericFutureListener<? extends Future<? super Void>>... listeners) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise sync() throws InterruptedException {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise syncUninterruptibly() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise await() throws InterruptedException {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise awaitUninterruptibly() {
        throw new UnsupportedOperationException("This should never happen");
    }


    @Override
    public final boolean setUncancellable() {
        return true;
    }

    @Override
    public final Channel channel() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean isSuccess() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean isCancellable() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final Throwable cause() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean await(long timeoutMillis) throws InterruptedException {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean awaitUninterruptibly(long timeoutMillis) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final Void getNow() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean isCancelled() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final boolean isDone() {
        throw new UnsupportedOperationException("This should never happen");
    }

    @Override
    public final ChannelPromise setSuccess(Void result) {
        return setSuccess();
    }

    @Override
    public final boolean trySuccess(Void result) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final boolean tryFailure(Throwable cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final boolean trySuccess() {
        // TODO Auto-generated method stub
        return false;
    }
}
