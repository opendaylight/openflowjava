/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslHandler;

import java.util.List;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler.COMPONENT_NAMES;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;

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
    
    private ConnectionFacade connectionFacade;

    /**
     * Constructor of class
     */
    public TlsDetector() {
        LOGGER.trace("Creating TLS Detector");
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
            LOGGER.trace("Testing connection for TLS");
            return SslHandler.isEncrypted(bb);
        }
        return false;
    }

    private static void enableSsl(ChannelHandlerContext ctx) {
        if (ctx.pipeline().get(COMPONENT_NAMES.SSL_HANDLER.name()) == null) {
            LOGGER.trace("Engaging TLS handler");
            ChannelPipeline p = ctx.channel().pipeline();
            SSLEngine engine = SslContextFactory.getServerContext()
                    .createSSLEngine();
            engine.setUseClientMode(false);
            p.addAfter(COMPONENT_NAMES.TLS_DETECTOR.name(), COMPONENT_NAMES.SSL_HANDLER.name(),
                    new SslHandler(engine));
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
            LOGGER.debug("Connection is encrypted");
            enableSsl(ctx);
        } else {
            LOGGER.debug("Connection is not encrypted");
        }
        
        if (connectionFacade != null) {
            LOGGER.trace("Firing onConnectionReady notification");
            connectionFacade.fireConnectionReadyNotification();
        }
        
        ctx.pipeline().remove(COMPONENT_NAMES.TLS_DETECTOR.name());
    }
    
    /**
     * @param connectionFacade the connectionFacade to set
     */
    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }
}
