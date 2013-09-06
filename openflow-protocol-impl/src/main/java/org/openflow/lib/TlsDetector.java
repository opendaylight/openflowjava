/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslHandler;

import java.util.List;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openflow.lib.TcpHandler.COMPONENT_NAMES;
import org.openflow.util.ByteBufUtils;

/**
 * Class for detecting TLS encrypted connection. If TLS encrypted connection is detected,
 * TLSDetector engages SSLHandler and OFFrameDecoder into pipeline else it engages only
 * OFFrameDecoder.
 *
 * @author michal.polkorab
 */
public class TlsDetector extends ByteToMessageDecoder {

    private boolean detectSsl;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TlsDetector.class);

    /**
     * Constructor of class
     */
    public TlsDetector() {
        LOGGER.info("Creating TLS Detector");
        detectSsl = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("Unexpected exception from downstream.",
                cause);
        ctx.close();
    }

    private boolean isSsl(ByteBuf bb) {
        if (detectSsl) {
            LOGGER.info("Testing connection for TLS");
            return SslHandler.isEncrypted(bb);
        }
        return false;
    }

    private static void enableSsl(ChannelHandlerContext ctx) {
        if (ctx.pipeline().get(COMPONENT_NAMES.SSL_HANDLER.name()) == null) {
            LOGGER.info("Engaging TLS handler");
            ChannelPipeline p = ctx.channel().pipeline();
            SSLEngine engine = SslContextFactory.getServerContext()
                    .createSSLEngine();
            engine.setUseClientMode(false);
            p.addLast(COMPONENT_NAMES.SSL_HANDLER.name(),
                    new SslHandler(engine));
        }
    }

    private static void enableOFFrameDecoder(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.channel().pipeline();
        if (p.get(COMPONENT_NAMES.OF_FRAME_DECODER.name()) == null) {
            LOGGER.debug("Engaging OFFrameDecoder");
            p.addLast(COMPONENT_NAMES.OF_FRAME_DECODER.name(), new OfFrameDecoder());
        } else {
            LOGGER.debug("OFFD already in pipeline");
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bb,
            List<Object> list) throws Exception {
        if (bb.readableBytes() < 5) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        if (isSsl(bb)) {
            LOGGER.info("Connection is encrypted");
            enableSsl(ctx);
        } else {
            LOGGER.info("Connection is not encrypted");
        }
        enableOFFrameDecoder(ctx);
        ctx.pipeline().remove(COMPONENT_NAMES.TLS_DETECTOR.name());
    }
}
