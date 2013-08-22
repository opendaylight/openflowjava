package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.openflow.util.ByteBufUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class Outbound extends ChannelOutboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(Outbound.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        logger.debug("OUTBOUND - Writing");
        ByteBuf bb = (ByteBuf) msg;
        logger.debug("Active: " + ctx.channel().isActive());
        logger.debug("Open: " + ctx.channel().isOpen());
        logger.debug("Registered: " + ctx.channel().isRegistered());
        logger.debug("Writeable: " + ctx.channel().isWritable());
        if (logger.isDebugEnabled()) {
            logger.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        ctx.writeAndFlush(bb, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        logger.debug("OUTBOUND - flushed");
        ctx.flush();
    }
    
}
