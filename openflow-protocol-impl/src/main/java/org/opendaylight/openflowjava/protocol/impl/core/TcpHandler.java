/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.connection.ServerFacade;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Class implementing server over TCP for handling incoming connections.
 *
 * @author michal.polkorab
 */
public class TcpHandler implements ServerFacade {
    /*
     * High/low write watermarks, in KiB.
     */
    private static final int DEFAULT_WRITE_HIGH_WATERMARK = 64;
    private static final int DEFAULT_WRITE_LOW_WATERMARK = 32;
    /*
     * Write spin count. This tells netty to immediately retry a non-blocking
     * write this many times before moving on to selecting.
     */
    private static final int DEFAULT_WRITE_SPIN_COUNT = 16;

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpHandler.class);

    private final int port;
    private String address;
    private final InetAddress startupAddress;
    private NioEventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;
    private final SettableFuture<Boolean> isOnlineFuture;

    private final PublishingChannelInitializer channelInitializer;

    /**
     * Enum used for storing names of used components (in pipeline).
     */
    public static enum COMPONENT_NAMES {

        /**
         * Detects switch idle state
         */
        IDLE_HANDLER,
        /**
         * Detects TLS connections
         */
        TLS_DETECTOR,
        /**
         * Component for handling TLS frames
         */
        SSL_HANDLER,
        /**
         * Decodes incoming messages into message frames
         */
        OF_FRAME_DECODER,
        /**
         * Detects version of incoming OpenFlow Protocol message
         */
        OF_VERSION_DETECTOR,
        /**
         * Transforms OpenFlow Protocol byte messages into POJOs
         */
        OF_DECODER,
        /**
         * Transforms POJOs into OpenFlow Protocol byte messages
         */
        OF_ENCODER,
        /**
         * Delegates translated POJOs into MessageConsumer
         */
        DELEGATING_INBOUND_HANDLER,
    }


    /**
     * Constructor of TCPHandler that listens on selected port.
     *
     * @param port listening port of TCPHandler server
     */
    public TcpHandler(final int port) {
        this(null, port);
    }

    /**
     * Constructor of TCPHandler that listens on selected address and port.
     * @param address listening address of TCPHandler server
     * @param port listening port of TCPHandler server
     */
    public TcpHandler(final InetAddress address, final int port) {
        this.port = port;
        this.startupAddress = address;
        channelInitializer = new PublishingChannelInitializer();
        isOnlineFuture = SettableFuture.create();
    }

    /**
     * Starts server on selected port.
     */
    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        /*
         * We generally do not perform IO-unrelated tasks, so we want to have
         * all outstanding tasks completed before the executing thread goes
         * back into select.
         *
         * Any other setting means netty will measure the time it spent selecting
         * and spend roughly proportional time executing tasks.
         */
        workerGroup.setIoRatio(100);

        final ChannelFuture f;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, DEFAULT_WRITE_HIGH_WATERMARK * 1024)
                    .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, DEFAULT_WRITE_LOW_WATERMARK * 1024)
                    .childOption(ChannelOption.WRITE_SPIN_COUNT, DEFAULT_WRITE_SPIN_COUNT);

            if (startupAddress != null) {
                f = b.bind(startupAddress.getHostAddress(), port).sync();
            } else {
                f = b.bind(port).sync();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while binding port {}", port, e);
            return;
        }

        try {
            InetSocketAddress isa = (InetSocketAddress) f.channel().localAddress();
            address = isa.getHostString();
            LOGGER.debug("address from tcphandler: {}", address);
            isOnlineFuture.set(true);
            LOGGER.info("Switch listener started and ready to accept incoming connections on port: {}", isa.getPort());
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for port {} shutdown", port, e);
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down {@link TcpHandler}}
     */
    @Override
    public ListenableFuture<Boolean> shutdown() {
        final SettableFuture<Boolean> result = SettableFuture.create();
        workerGroup.shutdownGracefully();
        // boss will shutdown as soon, as worker is down
        bossGroup.shutdownGracefully().addListener(new GenericFutureListener<io.netty.util.concurrent.Future<Object>>() {

            @Override
            public void operationComplete(
                    final io.netty.util.concurrent.Future<Object> downResult) throws Exception {
                result.set(downResult.isSuccess());
                if (downResult.cause() != null) {
                    result.setException(downResult.cause());
                }
            }

        });
        return result;
    }

    /**
     *
     * @return number of connected clients / channels
     */
    public int getNumberOfConnections() {
        return channelInitializer.size();
    }

    /**
     * @return channelInitializer providing channels
     */
    public PublishingChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    public ListenableFuture<Boolean> getIsOnlineFuture() {
        return isOnlineFuture;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param switchConnectionHandler
     */
    public void setSwitchConnectionHandler(
            final SwitchConnectionHandler switchConnectionHandler) {
        channelInitializer.setSwitchConnectionHandler(switchConnectionHandler);
    }

    /**
     * @param switchIdleTimeout in milliseconds
     */
    public void setSwitchIdleTimeout(final long switchIdleTimeout) {
        channelInitializer.setSwitchIdleTimeout(switchIdleTimeout);
    }

    /**
     * @param tlsSupported
     */
    public void setEncryption(final boolean tlsSupported) {
        channelInitializer.setEncryption(tlsSupported);
    }

    /**
     * @param sf serialization factory
     */
    public void setSerializationFactory(final SerializationFactory sf) {
        channelInitializer.setSerializationFactory(sf);
    }

    /**
     * @param factory
     */
    public void setDeserializationFactory(final DeserializationFactory factory) {
        channelInitializer.setDeserializationFactory(factory);
    }

}
