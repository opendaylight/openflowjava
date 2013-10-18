package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendEvent implements ClientEvent {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SendEvent.class);
    protected byte[] msgToSend;
    protected ChannelHandlerContext ctx;

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

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

}
