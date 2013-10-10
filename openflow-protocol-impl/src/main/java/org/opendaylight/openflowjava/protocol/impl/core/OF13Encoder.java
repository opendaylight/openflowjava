/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Transforms OpenFlow Protocol messages to POJOs
 * 
 * @author michal.polkorab
 */
public class OF13Encoder extends MessageToByteEncoder<OfHeader> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OF13Encoder.class);
    
    /** Constructor of class */
    public OF13Encoder() {
        LOGGER.info("Creating OF13Encoder");
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, OfHeader msg, ByteBuf out)
            throws Exception {
        LOGGER.debug("Encoding");
        SerializationFactory.messageToBuffer(msg.getVersion(), out, msg);
        if (out.readableBytes() > 0) {
            out.retain();
            ctx.writeAndFlush(out);
        } else {
            LOGGER.warn("Translated buffer is empty");
        }
    }

}
