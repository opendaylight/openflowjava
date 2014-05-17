/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;

import java.util.concurrent.TimeoutException;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;

class ExpectedFailureRpcChannelPromise<T extends OfHeader> extends AbstractRpcChannelPromise<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ExpectedFailureRpcChannelPromise.class);
    private final Cache<RpcResponseKey, ExpectedFailureRpcChannelPromise<?>> cache;
    private final RpcResponseKey key;

    ExpectedFailureRpcChannelPromise(final Channel channel, final Object message, final String failureInfo,
            final Cache<RpcResponseKey, ExpectedFailureRpcChannelPromise<?>> cache, final RpcResponseKey key) {
        super(channel, message, failureInfo);
        this.cache = Preconditions.checkNotNull(cache);
        this.key = Preconditions.checkNotNull(key);
    }

    @Override
    public ExpectedFailureRpcChannelPromise<?> setSuccess() {
        LOG.debug("Request for {} sent successfully", key);
        cache.put(key, this);
        return this;
    }

    @Override
    public ExpectedFailureRpcChannelPromise<?> setFailure(final Throwable cause) {
        LOG.debug("Request for {} failed", key, cause);
        failedRpc(cause);
        return this;
    }

    public void discard() {
        LOG.warn("Request for {} did not receive a response", key);
        failedRpc(new TimeoutException("Request timed out"));
    }

    @SuppressWarnings("unchecked")
    public void completed(final OfHeader message) {
        successfulRpc((T)message);
    }
}
