/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public class OF13Codec extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(OFFrameDecoder.class);
    
    public OF13Codec(){
        logger.info("OF13C - Creating OF 1.3 Codec");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("OF13C - reading frame");
        ByteBuf bb = (ByteBuf) msg;
        if(logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bb.capacity(); i++) {
                byte b = bb.getByte(i);
                sb.append(b + " ");
            }
            logger.debug(sb.toString());
        }
        byte type = bb.readByte();
        if (type == 0){
            logger.info("OF13C - HELLO message received");
        } else {
            logger.info("OF13C - Received message type: " + type);
        }
        short length = bb.readShort();
        int xid = bb.readInt();
        int bytes_remaining = bb.readableBytes();
        logger.info("OF13C - skipping unread bytes");
        bb.skipBytes(bytes_remaining);
        logger.info("OF13C - discarding read bytes");
        bb.discardReadBytes();
        
    }
    
    
    
    
    
    
}
