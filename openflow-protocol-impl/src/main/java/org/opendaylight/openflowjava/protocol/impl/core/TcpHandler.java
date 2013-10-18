/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
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

    private int port;
    private String address;
    private InetAddress startupAddress;
    private NioEventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpHandler.class);
    private SettableFuture<Boolean> isOnlineFuture;
    
    
    private PublishingChannelInitializer channelInitializer;

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
    public TcpHandler(int port) {
        this(null, port);
    }
    
    /**
     * Constructor of TCPHandler that listens on selected address and port.
     * @param address listening address of TCPHandler server
     * @param port listening port of TCPHandler server
     */
    public TcpHandler(InetAddress address, int port) {
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
        LOGGER.info("Switch ");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f;
            if (startupAddress != null) {
                f = b.bind(startupAddress.getHostAddress(), port).sync();
            } else {
                f = b.bind(port).sync();
            }
            
            InetSocketAddress isa = (InetSocketAddress) f.channel().localAddress();
            address = isa.getHostString();
            LOGGER.debug("address from tcphandler: " + address);
            port = isa.getPort();
            isOnlineFuture.set(true);
            LOGGER.info("Switch listener started and ready to accept incoming connections on port: " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
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
                    io.netty.util.concurrent.Future<Object> downResult) throws Exception {
                result.set(downResult.isSuccess());
                result.setException(downResult.cause());
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
            SwitchConnectionHandler switchConnectionHandler) {
        channelInitializer.setSwitchConnectionHandler(switchConnectionHandler);
    }
    
    public void setSwitchIdleTimeout(long switchIdleTimeout) {
        channelInitializer.setSwitchIdleTimeout(switchIdleTimeout);
    }
    
}
