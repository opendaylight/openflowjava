/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionAdapterFactoryImpl;
import org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes TCP / TLS channel
 * @author michal.polkorab
 */
public class TcpChannelInitializer extends ProtocolChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TcpChannelInitializer.class);
    private final DefaultChannelGroup allChannels;
    private ConnectionAdapterFactory connectionAdapterFactory;

    /**
     * default ctor
     */
    public TcpChannelInitializer() {
        this( new DefaultChannelGroup("netty-receiver", null), new ConnectionAdapterFactoryImpl() );
    }

    /**
     * Testing Constructor
     *
     */
    protected TcpChannelInitializer( DefaultChannelGroup channelGroup, ConnectionAdapterFactory connAdaptorFactory ) {
    	allChannels = channelGroup ;
    	connectionAdapterFactory = connAdaptorFactory ;
    }

    @Override
    protected void initChannel(final SocketChannel ch) {
        InetAddress switchAddress = ch.remoteAddress().getAddress();
        int port = ch.localAddress().getPort();
        int remotePort = ch.remoteAddress().getPort();
        LOGGER.debug("Incoming connection from (remote address): " + switchAddress.toString()
                + ":" + remotePort + " --> :" + port);
        
        if (ch.remoteAddress() != null) {
            if (!getSwitchConnectionHandler().accept(switchAddress)) {
                ch.disconnect();
                LOGGER.debug("Incoming connection rejected");
                return;
            }
        }
        LOGGER.debug("Incoming connection accepted - building pipeline");
        allChannels.add(ch);
        ConnectionFacade connectionFacade = null;
        connectionFacade = connectionAdapterFactory.createConnectionFacade(ch, null);
        try {
            LOGGER.debug("calling plugin: " + getSwitchConnectionHandler());
            getSwitchConnectionHandler().onSwitchConnected(connectionFacade);
            connectionFacade.checkListeners();
            ch.pipeline().addLast(PipelineHandlers.IDLE_HANDLER.name(), new IdleHandler(getSwitchIdleTimeout(), TimeUnit.MILLISECONDS));
            boolean tlsPresent = false;

            // If this channel is configured to support SSL it will only support SSL
            if (getTlsConfiguration() != null) {
                tlsPresent = true;
                SslContextFactory sslFactory = new SslContextFactory(getTlsConfiguration());
                SSLEngine engine = sslFactory.getServerContext().createSSLEngine();
                engine.setNeedClientAuth(true);
                engine.setUseClientMode(false);
                ch.pipeline().addLast(PipelineHandlers.SSL_HANDLER.name(), new SslHandler(engine));
            }
            ch.pipeline().addLast(PipelineHandlers.OF_FRAME_DECODER.name(),
                    new OFFrameDecoder(connectionFacade, tlsPresent));
            ch.pipeline().addLast(PipelineHandlers.OF_VERSION_DETECTOR.name(), new OFVersionDetector());
            OFDecoder ofDecoder = new OFDecoder();
            ofDecoder.setDeserializationFactory(getDeserializationFactory());
            ch.pipeline().addLast(PipelineHandlers.OF_DECODER.name(), ofDecoder);
            OFEncoder ofEncoder = new OFEncoder();
            ofEncoder.setSerializationFactory(getSerializationFactory());
            ch.pipeline().addLast(PipelineHandlers.OF_ENCODER.name(), ofEncoder);
            ch.pipeline().addLast(PipelineHandlers.DELEGATING_INBOUND_HANDLER.name(), new DelegatingInboundHandler(connectionFacade));
            if (!tlsPresent) {
                connectionFacade.fireConnectionReadyNotification();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize channel", e);
            ch.close();
        }
    }

    /**
     * @return iterator through active connections
     */
    public Iterator<Channel> getConnectionIterator() {
        return allChannels.iterator();
    }

    /**
     * @return amount of active channels
     */
    public int size() {
        return allChannels.size();
    }
}