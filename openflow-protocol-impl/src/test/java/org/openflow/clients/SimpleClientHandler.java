/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.openflow.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 *
 * @author michal.polkorab
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClientHandler.class);
    private SettableFuture<Boolean> sf;

    /**
     * @param sf future notifier of connected channel
     */
    public SimpleClientHandler(SettableFuture<Boolean> sf) {
        this.sf = sf;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler - start of read");
        if (sf != null) {
            sf.set(true);
            sf = null;
        }
        ByteBuf bb = (ByteBuf) msg;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        LOGGER.info(msg.toString());
        LOGGER.info("SimpleClientHandler - end of read");
    }
    
}
