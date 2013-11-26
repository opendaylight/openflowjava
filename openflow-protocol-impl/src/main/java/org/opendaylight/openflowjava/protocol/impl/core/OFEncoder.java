/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to POJOs
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OFEncoder extends MessageToByteEncoder<OfHeader> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OFEncoder.class);
    
    /** Constructor of class */
    public OFEncoder() {
        LOGGER.debug("Creating OF13Encoder");
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, OfHeader msg, ByteBuf out)
            throws Exception {
        LOGGER.debug("Encoding");
        try {
            SerializationFactory.messageToBuffer(msg.getVersion(), out, msg);
        } catch(Exception e) {
            LOGGER.error("Message serialization failed");
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (out.readableBytes() > 0) {
            out.retain();
            ctx.writeAndFlush(out);
        } else {
            LOGGER.warn("Translated buffer is empty");
        }
    }

}
