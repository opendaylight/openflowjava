/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openflow.clients.SimpleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author michal.polkorab
 */
public class TCPHandlerTest {

    /**
     * 
     */
    private static final String OF_BINARY_MESSAGE_INPUT_TXT = "OFBinaryMessageInput.txt";

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(TCPHandlerTest.class);

    protected int port;
    protected String address;
    protected TCPHandler tcphandler;

    /**
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Before
    public void setUp() throws InterruptedException, ExecutionException {
        getFreePortAndAddress();
        tcphandler = new TCPHandler(port);
        tcphandler.start();
        tcphandler.getIsOnlineFuture().get();
    }
    
    /**
     * stop {@link TCPHandler}
     */
    @After
    public void tearDown() {
        tcphandler.shutdown();
    }

    /**
     * Test of connections in {@link TCPHandler} - accepting connection of 1
     * client
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    @Test
    public void testConnectOneClient() throws InterruptedException, ExecutionException {
        int amountOfCLients = 1;
        createAndStartClient(amountOfCLients);
        int actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(amountOfCLients, actualConnections);
        PublishingChannelInitializer channelInitializer = tcphandler.getChannelInitializer();
        for (Iterator<Channel> iterator = channelInitializer.getConnectionIterator(); iterator.hasNext();) {
            Channel channel =  iterator.next();
            try {
                channel.close().sync();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(0, actualConnections);
    }
    
    /**
     * Test of connections in {@link TCPHandler} - accepting connection of 10
     * clients
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    @Test
    public void testConnectTenClients() throws InterruptedException, ExecutionException {
        int amountOfCLients = 10;
        createAndStartClient(amountOfCLients);
        int actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(amountOfCLients, actualConnections);
        PublishingChannelInitializer channelInitializer = tcphandler.getChannelInitializer();
        for (Iterator<Channel> iterator = channelInitializer.getConnectionIterator(); iterator.hasNext();) {
            Channel channel =  iterator.next();
            try {
                channel.close().sync();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(0, actualConnections);
    }
    
    /**
     * Test of disconnecting in {@link TCPHandler} - shutting down connection of 10
     * clients
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    @Test
    public void testDisconnectTenClients() throws InterruptedException, ExecutionException {
        int amountOfCLients = 10;
        List<SimpleClient> clients = createAndStartClient(amountOfCLients);
        int actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(amountOfCLients, actualConnections);
        
        disconnectClients(clients);

        actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(0, actualConnections);
    }

    /**
     * @param amountOfCLients 
     * @return new clients up and running
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<SimpleClient> createAndStartClient(int amountOfCLients)
            throws InterruptedException, ExecutionException {
        List<SimpleClient> clientsHorde = new ArrayList<>();
        for (int i = 0; i < amountOfCLients; i++) {
            SimpleClient sc = new SimpleClient(address, port, getClass().getResourceAsStream(
                    OF_BINARY_MESSAGE_INPUT_TXT));
            sc.setSecuredClient(true);
            clientsHorde.add(sc);
            sc.start();
        }
        for (SimpleClient sc : clientsHorde) {
            sc.getIsOnlineFuture().get();
        }
        return clientsHorde;
    }
    
    /**
     * Test of disconnecting in {@link TCPHandler} - shutting down connection of 1
     * client
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    @Test
    public void testDisconnectOneClient() throws InterruptedException, ExecutionException {
        int amountOfCLients = 1;
        List<SimpleClient> clients = createAndStartClient(amountOfCLients);
        int actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(amountOfCLients, actualConnections);
        disconnectClients(clients);
        actualConnections = tcphandler.getNumberOfConnections();
        Assert.assertEquals(0, actualConnections);
    }

    /**
     * @param clients
     * @throws InterruptedException 
     */
    private static void disconnectClients(List<SimpleClient> clients) throws InterruptedException {
        List<Future<?>> disconnectFutureBag = new ArrayList<>();
        for (SimpleClient simpleClient : clients) {
            disconnectFutureBag.add(simpleClient.disconnect());
        }
        for (Future<?> toBeDisconnected : disconnectFutureBag) {
            toBeDisconnected.sync();
        }
    }

    /**
     * Finds free port and determines host ip address
     */
    private void getFreePortAndAddress() {
        try {
            ServerSocket ss = new ServerSocket(0);
            port = ss.getLocalPort();
            InetAddress ia = InetAddress.getLocalHost();
            InetAddress[] all = InetAddress.getAllByName(ia.getHostName());
            address = all[0].getHostAddress();
            ss.close();
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
    }
}
