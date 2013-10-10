/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 *
 * @author michal.polkorab
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClientHandler.class);
    private SettableFuture<Boolean> isOnlineFuture;
    private int messagesReceived;

    /**
     * @param isOnlineFuture future notifier of connected channel
     */
    public SimpleClientHandler(SettableFuture<Boolean> isOnlineFuture) {
        this.isOnlineFuture = isOnlineFuture;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler - start of read");
        ByteBuf bb = (ByteBuf) msg;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        messagesReceived += readHeaders(bb);
        LOGGER.debug("Messages received: " + messagesReceived);
        switch (messagesReceived) {
        case 2:
            LOGGER.debug("FeaturesReply case");
            ByteBuf featuresReply = createFeaturesReplyBytebuf();
            ctx.write(featuresReply);
            LOGGER.debug("FeaturesReply sent");
            break;
        default:
            LOGGER.debug("Default case");
            break;
        }

        ctx.flush();
        LOGGER.info("end of read");
    }

    private static ByteBuf createFeaturesReplyBytebuf() {
        ByteBuf featuresReply = UnpooledByteBufAllocator.DEFAULT.buffer();
        featuresReply.writeByte(4);
        featuresReply.writeByte(6);
        featuresReply.writeShort(32);
        ByteBuf featuresReplyBody = BufferHelper
                .buildBuffer("00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00"
                        + " 01 02 03 00 01 02 03");
        featuresReply.writeBytes(featuresReplyBody);
        return featuresReply;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client is active");
        if (isOnlineFuture != null) {
            isOnlineFuture.set(true);
            isOnlineFuture = null;
        }
    }

    private static int readHeaders(ByteBuf bb) {
        int messages = 0;
        int length = 0;
        while (bb.readableBytes() > 0) {
            length = bb.getShort(2);
            bb.skipBytes(length);
            messages++;
        }
        return messages;
    }

}
