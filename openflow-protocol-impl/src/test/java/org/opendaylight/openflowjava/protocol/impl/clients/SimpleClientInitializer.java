/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import org.opendaylight.openflowjava.protocol.impl.core.SslContextFactory;

import com.google.common.util.concurrent.SettableFuture;

/** Initializes secured {@link SimpleClient} pipeline
 * 
 * @author michal.polkorab
 */
public class SimpleClientInitializer extends ChannelInitializer<SocketChannel> {
    
    private SettableFuture<Boolean> sf;
    private boolean secured;

    /**
     * @param sf future notifier of connected channel
     */
    public SimpleClientInitializer(SettableFuture<Boolean> sf, boolean secured) {
        this.sf = sf;
        this.secured = secured;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (secured) {
        	SSLEngine engine =
        			SslContextFactory.getClientContext().createSSLEngine();
        	engine.setUseClientMode(true);
        	pipeline.addLast("ssl", new SslHandler(engine));
        }
        pipeline.addLast("handler", new SimpleClientHandler(sf));
        sf = null;
    }
}