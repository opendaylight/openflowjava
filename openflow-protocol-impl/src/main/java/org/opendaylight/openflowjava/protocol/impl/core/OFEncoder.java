/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;

import org.opendaylight.openflowjava.protocol.impl.connection.MessageListenerWrapper;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to POJOs
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OFEncoder extends MessageToByteEncoder<MessageListenerWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OFEncoder.class);
    private SerializationFactory serializationFactory;

    /** Constructor of class */
    public OFEncoder() {
        LOGGER.trace("Creating OF13Encoder");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageListenerWrapper wrapper, ByteBuf out)
            throws Exception {
        LOGGER.trace("Encoding");
        try {
            serializationFactory.messageToBuffer(wrapper.getMsg().getVersion(), out, wrapper.getMsg());
        } catch(Exception e) {
            LOGGER.warn("Message serialization failed ", e);
            Future<Void> newFailedFuture = ctx.newFailedFuture(e);
            wrapper.getListener().operationComplete(newFailedFuture);
            out.clear();
            return;
        }
    }

    /**
     * @param serializationFactory
     */
    public void setSerializationFactory(SerializationFactory serializationFactory) {
        this.serializationFactory = serializationFactory;
    }

}
