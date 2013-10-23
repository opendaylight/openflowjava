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
    
    private SettableFuture<Boolean> isOnlineFuture;
    private boolean secured;
    private ScenarioHandler scenarioHandler;

    /**
     * @param isOnlineFuture future notifier of connected channel
     * @param secured true if {@link SimpleClient} should use encrypted communication
     */
    public SimpleClientInitializer(SettableFuture<Boolean> isOnlineFuture, boolean secured) {
        this.isOnlineFuture = isOnlineFuture;
        this.secured = secured;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (secured) {
            SSLEngine engine = SslContextFactory.getClientContext()
                    .createSSLEngine();
            engine.setUseClientMode(true);
            pipeline.addLast("ssl", new SslHandler(engine));
        }
        SimpleClientHandler simpleClientHandler = new SimpleClientHandler(isOnlineFuture, scenarioHandler);
        simpleClientHandler.setScenario(scenarioHandler);
        pipeline.addLast("handler", simpleClientHandler);
        isOnlineFuture = null;

    }

    /**
     * @param scenarioHandler handler of scenario events
     */
    public void setScenario(ScenarioHandler scenarioHandler) {
        this.scenarioHandler = scenarioHandler;
    }
}