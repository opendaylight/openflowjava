/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.opendaylight.openflowjava.protocol.api.connection.ThreadConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Class implementing server over TCP / TLS for handling incoming connections.
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

    private int port;
    private String address;
    private final InetAddress startupAddress;
    private NioEventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;
    private final SettableFuture<Boolean> isOnlineFuture;
    private ThreadConfiguration threadConfig;

    private TcpChannelInitializer channelInitializer;

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
        isOnlineFuture = SettableFuture.create();
    }

    /**
     * Starts server on selected port.
     */
    @Override
    public void run() {
        if (threadConfig != null) {
            bossGroup = new NioEventLoopGroup(threadConfig.getBossThreadCount());
        } else {
            bossGroup = new NioEventLoopGroup();
        }

        if (workerGroup == null) {
            this.initiateWorkerGroup();
        }
        
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
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
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

            // Update port, as it may have been specified as 0
            this.port = isa.getPort();

            LOGGER.debug("address from tcphandler: {}", address);
            isOnlineFuture.set(true);
            LOGGER.info("Switch listener started and ready to accept incoming tcp/tls connections on port: {}", port);
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
     * @param channelInitializer
     */
    public void setChannelInitializer(TcpChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void setThreadConfig(ThreadConfiguration threadConfig) {
        this.threadConfig = threadConfig;
    }
    
    /**
     * @return worker group
     */
    public NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
    
    /**
     * Initiate worker group
     */
    public void initiateWorkerGroup() {
        if (threadConfig != null) {
            workerGroup = new NioEventLoopGroup(threadConfig.getWorkerThreadCount());
        } else {
            workerGroup = new NioEventLoopGroup();
        }
    }

}
