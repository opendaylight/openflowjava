/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.openflow.lib.deserialization.DeserializationFactory;
import org.openflow.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to Java messages / objects and takes appropriate
 * actions
 *
 * @author michal.polkorab
 */
public class Of13Codec extends ChannelInboundHandlerAdapter {

    // TODO - fix with enum in API
    private static final int MESSAGE_TYPES = 29;
    private static final Logger LOGGER = LoggerFactory.getLogger(Of13Codec.class);

    /**
     * Constructor of class
     */
    public Of13Codec() {
        LOGGER.info("Creating OF 1.3 Codec");
        
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("Reading frame");
        LOGGER.debug("Received msg is of type: " + msg.getClass().getName());
        ByteBuf bb = (ByteBuf) msg;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        
        bb.markReaderIndex();
        short type = bb.readUnsignedByte();
        int length = bb.readUnsignedShort();
        long xid = bb.readUnsignedInt();
        bb.resetReaderIndex();

        if (!checkOFHeader(type, length)) {
            bb.discardReadBytes();
            LOGGER.info("Non-OF Protocol message received (discarding)");
            return;
        }

        // TODO - change hardcoded version to constant, enum, ...
        DeserializationFactory.bufferToMessage(bb, (short) 0x04);
        
        // TODO - delete this switch case after completing deserialization factory
        ByteBuf out = ctx.alloc().buffer();
        switch (type) {
            case 0: {
                LOGGER.info("OFPT_HELLO received");
                byte[] hello = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
                out.writeBytes(hello);
                break;
            }
            case 1:
                LOGGER.info("OFPT_ERROR received");
                break;
            case 2: {
                LOGGER.info("OFPT_ECHO_REQUEST received");
                byte[] echoReply = new byte[]{0x04, 0x03, 0x00, 0x08};
                out.writeBytes(echoReply);
                out.writeInt((int) xid);
                // TODO - append original data field
                break;
            }
            case 3:
                LOGGER.info("OFPT_ECHO_REPLY received");
                break;
            case 5:
                LOGGER.info("OFPT_FEATURES_REQUEST received");
                break;
            case 6:
                LOGGER.info("OFPT_FEATURES_REPLY received");
                break;
            default:
                LOGGER.info("Received message type: " + type);
                break;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(out));
        }

        ctx.writeAndFlush(out);
        LOGGER.info("Flushed");

        int bytesRemaining = bb.readableBytes();
        LOGGER.debug("Skipping unread bytes");
        bb.skipBytes(bytesRemaining);
        LOGGER.debug("Discarding read bytes");
        LOGGER.debug("RI: " + bb.readerIndex());
        LOGGER.debug("WI: " + bb.writerIndex());

    }

    private static boolean checkOFHeader(short type, int length) {
        return !((type > MESSAGE_TYPES) || (length < OfFrameDecoder.LENGTH_OF_HEADER));
    }
}
