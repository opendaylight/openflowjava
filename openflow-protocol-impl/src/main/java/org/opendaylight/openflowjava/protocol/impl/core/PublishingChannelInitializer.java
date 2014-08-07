/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactoryImpl;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the channel
 * @author michal.polkorab
 */
public class PublishingChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublishingChannelInitializer.class);
    private final DefaultChannelGroup allChannels;
    private SwitchConnectionHandler switchConnectionHandler;
    private long switchIdleTimeout;
    private SerializationFactory serializationFactory;
    private DeserializationFactory deserializationFactory;
    private ConnectionAdapterFactory connectionAdapterFactory;
    private TlsConfiguration tlsConfiguration ;

    /**
     * default ctor
     */
    public PublishingChannelInitializer() {
        this( new DefaultChannelGroup("netty-receiver", null), new ConnectionAdapterFactoryImpl() );
    }

    /**
     * Testing Constructor
     * 
     */
    protected PublishingChannelInitializer( DefaultChannelGroup channelGroup, ConnectionAdapterFactory connAdaptorFactory ) {
    	allChannels = channelGroup ;
    	connectionAdapterFactory = connAdaptorFactory ;
    }
    
    @Override
    protected void initChannel(final SocketChannel ch) {
        InetAddress switchAddress = ch.remoteAddress().getAddress();
        int port = ch.localAddress().getPort();
        int remotePort = ch.remoteAddress().getPort();
        LOGGER.info("Incoming connection from (remote address): " + switchAddress.toString()
                + ":" + remotePort + " --> :" + port);
        if (!switchConnectionHandler.accept(switchAddress)) {
            ch.disconnect();
            LOGGER.info("Incoming connection rejected");
            return;
        }
        LOGGER.info("Incoming connection accepted - building pipeline");
        allChannels.add(ch);
        ConnectionFacade connectionFacade = null;
        connectionFacade = connectionAdapterFactory.createConnectionFacade(ch, null);
        try {
            LOGGER.debug("calling plugin: " + switchConnectionHandler);
            switchConnectionHandler.onSwitchConnected(connectionFacade);
            connectionFacade.checkListeners();
            ch.pipeline().addLast(PIPELINE_HANDLERS.IDLE_HANDLER.name(), new IdleHandler(switchIdleTimeout, TimeUnit.MILLISECONDS));
            
            // If this channel is configured to support SSL it will only support SSL
            if (tlsConfiguration != null) {
                SslContextFactory sslFactory = new SslContextFactory(tlsConfiguration);
                SSLEngine engine = sslFactory.getServerContext().createSSLEngine();
                engine.setNeedClientAuth(true);
                engine.setUseClientMode(false);
                ch.pipeline().addLast(PIPELINE_HANDLERS.SSL_HANDLER.name(), new SslHandler(engine));
            }
            ch.pipeline().addLast(PIPELINE_HANDLERS.OF_FRAME_DECODER.name(), new OFFrameDecoder(connectionFacade));
            ch.pipeline().addLast(PIPELINE_HANDLERS.OF_VERSION_DETECTOR.name(), new OFVersionDetector());
            OFDecoder ofDecoder = new OFDecoder();
            ofDecoder.setDeserializationFactory(deserializationFactory);
            ch.pipeline().addLast(PIPELINE_HANDLERS.OF_DECODER.name(), ofDecoder);
            OFEncoder ofEncoder = new OFEncoder();
            ofEncoder.setSerializationFactory(serializationFactory);
            ch.pipeline().addLast(PIPELINE_HANDLERS.OF_ENCODER.name(), ofEncoder);
            ch.pipeline().addLast(PIPELINE_HANDLERS.DELEGATING_INBOUND_HANDLER.name(), new DelegatingInboundHandler(connectionFacade));
            if (tlsConfiguration == null) {
                connectionFacade.fireConnectionReadyNotification();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize channel", e);
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

    /**
     * @param switchConnectionHandler the switchConnectionHandler to set
     */
    public void setSwitchConnectionHandler(final SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }

    /**
     * @param switchIdleTimeout the switchIdleTimeout to set
     */
    public void setSwitchIdleTimeout(final long switchIdleTimeout) {
        this.switchIdleTimeout = switchIdleTimeout;
    }

    /**
     * @param serializationFactory
     */
    public void setSerializationFactory(final SerializationFactory serializationFactory) {
        this.serializationFactory = serializationFactory;
    }

    /**
     * @param deserializationFactory
     */
    public void setDeserializationFactory(final DeserializationFactory deserializationFactory) {
        this.deserializationFactory = deserializationFactory;
    }

    /**
     * @param tlsConfiguration
     */
    public void setTlsConfiguration(TlsConfiguration tlsConfiguration) {
        this.tlsConfiguration = tlsConfiguration;
    }
}
