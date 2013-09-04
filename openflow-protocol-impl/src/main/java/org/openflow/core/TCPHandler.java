/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Class implementing server over TCP for handling incoming connections.
 *
 * @author michal.polkorab
 */
public class TCPHandler extends Thread {

    private int port;
    private NioEventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPHandler.class);
    private SettableFuture<Boolean> isOnlineFuture;
    
    
    private PublishingChannelInitializer channelInitializer;

    /**
     * Enum used for storing names of used components (in pipeline).
     */
    public static enum COMPONENT_NAMES {

        /**
         * First component in pipeline - detecting TLS connections
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
         * Transforms OpenFlow Protocol messages
         */
        OF_CODEC,
        /**
         * Communicates with upper layers (outside OF Library)
         */
        OF_FACADE
    }
    

    /**
     * Constructor of TCPHandler that listens on selected port.
     *
     * @param port listening port of TCPHandler server
     */
    public TCPHandler(int port) {
        this.port = port;
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
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            isOnlineFuture.set(true);
            LOGGER.info("Switch listener started and ready to accept incoming connections on port: " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(TCPHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down {@link TCPHandler}}
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
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
    
    /**
     * Sets and starts TCPHandler.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 6633;
        }
        new TCPHandler(port).start();
    }
    
    /**
     * @return the isOnlineFuture
     */
    public SettableFuture<Boolean> getIsOnlineFuture() {
        return isOnlineFuture;
    }
    
}
