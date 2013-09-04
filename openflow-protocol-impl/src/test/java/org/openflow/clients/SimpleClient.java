/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.clients;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Simple client for testing purposes
 *
 * @author michal.polkorab
 */
public class SimpleClient extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    private final String host;
    private final int port;
    private boolean securedClient = false;
    private InputStream fis;
    private EventLoopGroup group;
    private SettableFuture<Boolean> isOnlineFuture;
    
    /**
     * Constructor of class
     *
     * @param host address of host
     * @param port host listening port
     * @param filename name of input file containing binary data to be send
     */
    public SimpleClient(String host, int port, String filename) {
        this.host = host;
        this.port = port;
        if (filename != null) {
            try {
                fis = new FileInputStream(filename);
            } catch (FileNotFoundException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        init();
    }

    /**
     * @param host
     * @param port
     * @param filename
     */
    public SimpleClient(String host, int port, InputStream filename) {
        this.host = host;
        this.port = port;
        this.fis = filename;
        init();
    }

    private void init() {
        isOnlineFuture = SettableFuture.create();
    }
    /**
     * Starting class of {@link SimpleClient}
     */
    @Override
    public void run() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            if (securedClient) {
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleClientInitializer(isOnlineFuture));
            } else {
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleClientHandler(isOnlineFuture));
            }

            Channel ch = b.connect(host, port).sync().channel();
            
            byte[] bytearray = new byte[64];
            ByteBuf buffy = ch.alloc().buffer(128);

            LOGGER.debug("Before fis != null " + fis);
            if (fis != null) {
                try {
                    LOGGER.debug("Size to read (in bytes) : " + fis.available());
                    int lenght;
                    while ((lenght = fis.read(bytearray)) != -1) {
                        buffy.writeBytes(bytearray, 0, lenght);
                    }
                    ch.writeAndFlush(buffy);
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                buffy = ch.alloc().buffer(128);
                buffy.writeBytes(line.getBytes(Charset.defaultCharset()));
                ch.writeAndFlush(buffy);

                if ("bye".equals(line.toLowerCase())) {
                    LOGGER.info("Bye");
                    in.close();
                    break;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * @return close future
     */
    public Future<?> disconnect() {
        return group.shutdownGracefully();
    }

    /**
     * @param securedClient
     */
    public void setSecuredClient(boolean securedClient) {
        this.securedClient = securedClient;
    }

    /**
     * Sets up {@link SimpleClient} and fires run()
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO - add using secure switch via parameter
        String host;
        int port;
        SimpleClient sc;
        if (args.length != 3) {
            LOGGER.error("Usage: " + SimpleClient.class.getSimpleName()
                    + " <host> <port> <secured> <filename>");
            LOGGER.error("Trying to use default setting.");
            InetAddress ia = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(ia.getHostName());
            host = all[0].getHostAddress();
            port = 6633;
            InputStream filenamearg = SimpleClient.class.getResourceAsStream("/org/openflow/core/OFBinaryMessageInput.txt");
            sc = new SimpleClient(host, port, filenamearg);
            sc.setSecuredClient(true);
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            String filenamearg = args[3];
            sc = new SimpleClient(host, port, filenamearg);
            sc.setSecuredClient(Boolean.parseBoolean(args[2]));
        }
        sc.start();
        
    }
    
    /**
     * @return the isOnlineFuture
     */
    public SettableFuture<Boolean> getIsOnlineFuture() {
        return isOnlineFuture;
    }
}