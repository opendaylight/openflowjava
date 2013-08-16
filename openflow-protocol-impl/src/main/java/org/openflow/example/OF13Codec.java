/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.openflow.util.ByteBufUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to Java messages / objects and takes appropriate
 * actions
 *
 * @author michal.polkorab
 */
public class OF13Codec extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(OFFrameDecoder.class);

    /**
     * Constructor of class
     */
    public OF13Codec() {
        logger.info("OF13C - Creating OF 1.3 Codec");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("OF13C - Reading frame");
        ByteBuf bb = (ByteBuf) msg;
        if (logger.isDebugEnabled()) {
            logger.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        byte type = bb.readByte();
        short length = bb.readShort();
        long xid = bb.readUnsignedInt();
        ByteBuf out = ctx.alloc().buffer();
        switch (type) {
            case 0: {
                logger.info("OF13C - OFPT_HELLO received");
                byte[] hello = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
                out.writeBytes(hello);
                break;
            }
            case 1:
                logger.info("OF13C - OFPT_ERROR received");
                break;
            case 2: {
                logger.info("OF13C - OFPT_ECHO_REQUEST received");
                byte[] echoReply = new byte[]{0x04, 0x03, 0x00, 0x08};
                out.writeBytes(echoReply);
                out.writeInt((int) xid);
                // TODO - append original data field
                break;
            }
            case 3:
                logger.info("OF13C - OFPT_ECHO_REPLY received");
                break;
            case 5:
                logger.info("OF13C - OFPT_FEATURES_REQUEST received");
                break;
            case 6:
                logger.info("OF13C - OFPT_FEATURES_REPLY received");
                break;
            default:
                logger.info("OF13C - Received message type: " + type);
                break;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(ByteBufUtils.byteBufToHexString(out));
        }

        ctx.writeAndFlush(out);
        logger.info("OF13C - Flushed");

        int bytes_remaining = bb.readableBytes();
        logger.debug("OF13C - Skipping unread bytes");
        bb.skipBytes(bytes_remaining);
        logger.debug("OF13C - Discarding read bytes");
        logger.debug("RI: " + bb.readerIndex());
        logger.debug("WI: " + bb.writerIndex());

    }
}
