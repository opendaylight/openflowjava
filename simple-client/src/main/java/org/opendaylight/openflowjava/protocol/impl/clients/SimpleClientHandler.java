/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 *
 * @author michal.polkorab
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SimpleClientHandler.class);
    private SettableFuture<Boolean> isOnlineFuture;
    protected ScenarioHandler scenarioHandler;

    /**
     * @param isOnlineFuture future notifier of connected channel
     * @param scenarioHandler handler of scenario events
     */
    public SimpleClientHandler(SettableFuture<Boolean> isOnlineFuture, ScenarioHandler scenarioHandler) {
        this.isOnlineFuture = isOnlineFuture;
        this.scenarioHandler = scenarioHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler - start of read");
        ByteBuf bb = (ByteBuf) msg;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<< " + ByteBufUtils.byteBufToHexString(bb));
        }
        byte[] message = new byte[8];
        bb.readBytes(message);
        scenarioHandler.addOfMsg(message);
        skipMsg(bb);
        LOGGER.info("end of read");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Client is active");
        if (isOnlineFuture != null) {
            isOnlineFuture.set(true);
            isOnlineFuture = null;
        }
        scenarioHandler.setCtx(ctx);
        scenarioHandler.start();
        
    }

    private static void skipMsg(ByteBuf bb) {
        if (bb.readableBytes() > 0) {
            bb.skipBytes(bb.getShort(2));
        }
    }

    /**
     * @param scenarioHandler handler of scenario events
     */
    public void setScenario(ScenarioHandler scenarioHandler) {
        this.scenarioHandler = scenarioHandler;
    }

}
