/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author michal.polkorab
 *
 */
public class MessageListenerWrapper {

    private OfHeader msg;
    private GenericFutureListener<Future<Void>> listener;

    /**
     * @param takeMessage
     * @param l
     */
    public MessageListenerWrapper(Object msg, GenericFutureListener<Future<Void>> listener) {
        this.msg = (OfHeader) msg;
        this.listener = listener;
    }
    public OfHeader getMsg() {
        return msg;
    }
    public GenericFutureListener<Future<Void>> getListener() {
        return listener;
    }
}