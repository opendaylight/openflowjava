/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to POJOs
 * @author michal.polkorab
 */
public class OF13Decoder extends MessageToMessageDecoder<VersionMessageWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OF13Decoder.class);

    /**
     * Constructor of class
     */
    public OF13Decoder() {
        LOGGER.debug("Creating OF 1.3 Decoder");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, VersionMessageWrapper msg,
            List<Object> out) throws Exception {
        LOGGER.debug("VersionMessageWrapper received");
        DataObject dataObject = DeserializationFactory.bufferToMessage(msg.getMessageBuffer(),
                msg.getVersion());
        if (dataObject == null) {
            LOGGER.warn("Translated POJO is null");
            return;
        }
        msg.getMessageBuffer().discardReadBytes();
        msg.getMessageBuffer().release();
        out.add(dataObject);
    }
}
