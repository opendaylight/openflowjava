/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.Iterator;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public boolean ready = false;
    
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
    }

    /**
     * Starts server on selected port.
     *
     * @throws Exception on connection failure
     */
    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(COMPONENT_NAMES.TLS_DETECTOR.name(), new TLSDetector());
                }
            })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            ready = true;
            LOGGER.info("Switch listener started and ready to accept incoming connections on port: " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(TCPHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
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
    
    public int getNumberOfConnections() {
        int numberOfConnections = 0;
        Iterator wgi = workerGroup.iterator();
        while (wgi.hasNext()){
            numberOfConnections++;
            NioEventLoop eventLoop = (NioEventLoop) wgi.next();
            if (eventLoop != null){
                System.out.println("eventLoop je rozne od null");
            } else {
                System.out.println("eventLoop je null");
            }
        }
        
        LOGGER.debug("number of connections is: " + numberOfConnections);
        LOGGER.debug("Executor count: " + workerGroup.executorCount());
        return numberOfConnections;
    }
    
}
