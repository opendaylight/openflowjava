/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;

class RpcChannelPromise extends AbstractRpcChannelPromise<Void> {

    public RpcChannelPromise(String failureInfo, Channel channel) {
        super(failureInfo, channel);
    }

    @Override
    public RpcChannelPromise setSuccess() {
        successfulRpc(null);
        return this;
    }

    @Override
    public RpcChannelPromise setFailure(Throwable cause) {
        failedRpc(cause);
        return this;
    }
}
