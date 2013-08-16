/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslHandler;
import java.util.List;
import javax.net.ssl.SSLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openflow.example.TCPHandler.COMPONENT_NAMES;

/**
 * Class for detecting TLS encrypted connection. If TLS encrypted connection is detected,
 * TLSDetector engages SSLHandler and OFFrameDecoder into pipeline else it engages only
 * OFFrameDecoder.
 *
 * @author michal.polkorab
 */
public class TLSDetector extends ByteToMessageDecoder {

    private boolean detectSsl;
    private static final Logger logger = LoggerFactory
            .getLogger(TLSDetector.class);

    /**
     * Constructor of class
     */
    public TLSDetector() {
        logger.info("TLS Detector - Creating TLS Detector");
        detectSsl = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("TLS Detector - Unexpected exception from downstream.",
                cause);
        cause.printStackTrace();
        ctx.close();
    }

    private boolean isSsl(ByteBuf bb) {
        if (detectSsl) {
            logger.info("TLS Detector - Testing connection for TLS");
            return SslHandler.isEncrypted(bb);
        }
        return false;
    }

    private static void enableSsl(ChannelHandlerContext ctx) {
        if (ctx.pipeline().get(COMPONENT_NAMES.SSL_HANDLER.name()) == null) {
            logger.info("TLS Detector - Engaging TLS handler");
            ChannelPipeline p = ctx.channel().pipeline();
            SSLEngine engine = SslContextFactory.getServerContext()
                    .createSSLEngine();
            engine.setUseClientMode(false);
            p.addLast(COMPONENT_NAMES.SSL_HANDLER.name(),
                    new SslHandler(engine));
        }
    }

    private static void enableOFFrameDecoder(ChannelHandlerContext ctx) {
        logger.debug("TLS Detector - Engaging OFFrameDecoder");
        ChannelPipeline p = ctx.channel().pipeline();
        p.addLast(COMPONENT_NAMES.OF_FRAME_DECODER.name(), new OFFrameDecoder());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bb,
            List<Object> list) throws Exception {
        if (bb.readableBytes() < 5) {
            return;
        }
        if (isSsl(bb)) {
            logger.info("TLS Detector - Connection is encrypted");
            enableSsl(ctx);
        } else {
            logger.info("TLS Detector - Connection is not encrypted");
        }
        enableOFFrameDecoder(ctx);
        ctx.pipeline().remove(COMPONENT_NAMES.TLS_DETECTOR.name());
    }

}
