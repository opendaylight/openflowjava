/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing sending message event
 * 
 * @author michal.polkorab
 */
public class SendEvent implements ClientEvent {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SendEvent.class);
    protected byte[] msgToSend;
    protected ChannelHandlerContext ctx;

    /**
     * @param msgToSend message to be sent
     */
    public SendEvent(byte[] msgToSend) {
        this.msgToSend = msgToSend;
    }

    @Override
    public boolean eventExecuted() {
        LOGGER.debug("sending message");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("start of run");
                ByteBuf buffer = ctx.alloc().buffer();
                buffer.writeBytes(msgToSend);
                ctx.writeAndFlush(buffer);
                LOGGER.debug(">> " + ByteBufUtils.bytesToHexString(msgToSend));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.debug("message sent");
        return true;
    }

    /**
     * @param ctx context which will be used for sending messages (SendEvents)
     */
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

}
