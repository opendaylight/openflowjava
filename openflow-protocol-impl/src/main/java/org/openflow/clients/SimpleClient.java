/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.clients;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client for testing purposes
 *
 * @author michal.polkorab
 */
public class SimpleClient extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    private final String host;
    private final int port;
    private String filename;
    private boolean securedClient = true;

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
        this.filename = filename;
    }

    /**
     * Starting class of {@link SimpleClient}
     *
     * @throws Exception connection exception
     */
    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            if (securedClient) {
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleClientInitializer());
            } else {
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleClientHandler());
            }

            Channel ch = b.connect(host, port).sync().channel();

            byte[] bytearray = new byte[64];
            ByteBuf buffy = ch.alloc().buffer(128);

            if (filename != null) {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(filename);
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
     * Sets up {@link SimpleClient} and fires run()
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String host;
        int port;
        String filenamearg;
        if (args.length != 3) {
            LOGGER.error("Usage: " + SimpleClient.class.getSimpleName()
                    + " <host> <port> <filename>");
            LOGGER.error("Trying to use default setting.");
            InetAddress ia = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(ia.getHostName());
            host = all[0].getHostAddress();
            port = 6633;
            filenamearg = null;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            filenamearg = args[2];
        }
        new SimpleClient(host, port, filenamearg).start();
    }
}