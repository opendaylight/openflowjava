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

/**
 *
 * @author michal.polkorab
 */
public class TLSDetector extends ByteToMessageDecoder {

    private boolean detectSsl;
    private ByteBuf buf;
    private static final Logger logger = LoggerFactory.getLogger(TLSDetector.class);

    public TLSDetector() {
        logger.info("TLS Detector - Creating TLS Detector");
        detectSsl = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("TLS Detector - Unexpected exception from downstream.", cause);
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

    private void enableSsl(ChannelHandlerContext ctx) {
        if (ctx.pipeline().get("ssl") == null) {
            logger.info("TLS Detector - Engaging TLS handler");
            ChannelPipeline p = ctx.channel().pipeline();
            SSLEngine engine =
                    SslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            p.addLast("ssl", new SslHandler(engine));
        }
    }

    private void enableOFFrameDecoder(ChannelHandlerContext ctx) {
        logger.info("TLS Detector - Engaging OFFrameDecoder");
        ChannelPipeline p = ctx.channel().pipeline();
        p.addLast("offramedecoder", new OFFrameDecoder());
        p.remove("tlsdetector");
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        if (bb.readableBytes() < 5) {
            return;
        }
        if (isSsl(bb)) {
            logger.info("TLS Detector - connection is encrypted");
            enableSsl(chc);
        } else {
            logger.info("TLS Detector - connection is not encrypted");
        }
        enableOFFrameDecoder(chc);
    }
    
}
