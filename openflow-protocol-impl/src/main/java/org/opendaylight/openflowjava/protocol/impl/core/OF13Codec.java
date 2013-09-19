/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to POJOs and reverse
 *
 * @author michal.polkorab
 */
public class OF13Codec extends ByteToMessageCodec<DataObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OF13Codec.class);

    /**
     * Constructor of class
     */
    public OF13Codec() {
        LOGGER.info("Creating OF 1.3 Codec");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DataObject msg, ByteBuf out)
            throws Exception {
        SerializationFactory.messageToBuffer((short) 0x04, out, msg);
        ctx.writeAndFlush(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bb,
            List<Object> out) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        
        // TODO - change hardcoded version to constant, enum, ...
        DataObject dataObject = DeserializationFactory.bufferToMessage(bb, (short) 0x04);
        out.add(dataObject);
        bb.skipBytes(bb.readableBytes());
    }
}
