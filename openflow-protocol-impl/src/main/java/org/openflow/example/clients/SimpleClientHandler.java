/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.example.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.openflow.util.ByteBufUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter/*ChannelOutboundHandlerAdapter*/ {

    private static final Logger logger = LoggerFactory.getLogger(SimpleClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("SimpleClientHandler - start of read");
        ByteBuf bb = (ByteBuf) msg;
        if (logger.isDebugEnabled()) {
            logger.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        logger.info(msg.toString());
        logger.info("SimpleClientHandler - end of read");
    }
    
}
