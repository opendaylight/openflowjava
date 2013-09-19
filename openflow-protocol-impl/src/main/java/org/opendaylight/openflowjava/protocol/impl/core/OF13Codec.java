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

    private static final int MESSAGE_TYPES = 29;
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
        
        bb.markReaderIndex();
        short type = bb.readUnsignedByte();
        int length = bb.readUnsignedShort();
        long xid = bb.readUnsignedInt();
        bb.resetReaderIndex();

        // TODO - consider removing this control -> extended types may be discarded
        if (!checkOFHeader(type, length)) {
            bb.discardReadBytes();
            LOGGER.info("Non-OF Protocol message received (discarding)");
            return;
        }

        // TODO - change hardcoded version to constant, enum, ...
        DataObject dataObject = DeserializationFactory.bufferToMessage(bb, (short) 0x04);
        out.add(dataObject);
        System.out.println(dataObject.getClass().getName());
        bb.skipBytes(bb.readableBytes());
    }
    
    private static boolean checkOFHeader(short type, int length) {
        return !((type > MESSAGE_TYPES) || (length < OFFrameDecoder.LENGTH_OF_HEADER));
    }
}
