/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration.FEATURE_SUPPORT;
import org.opendaylight.openflowjava.protocol.impl.clients.SimpleClient;
import org.opendaylight.openflowjava.protocol.impl.connection.SwitchConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class IntegrationTest {

    /** Name of file in which OpenFLow protocol messages are stored in binary format */
    public static final String OF_BINARY_MESSAGE_INPUT_TXT = "OFBinaryMessageInput.txt";
    private static final int DEFAULT_PORT = 6633;
    private static final FEATURE_SUPPORT DEFAULT_TLS_SUPPORT = FEATURE_SUPPORT.NOT_SUPPORTED;
    
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(IntegrationTest.class);

    private static final long CONNECTION_TIMEOUT = 2000;

    private InetAddress startupAddress;
    private MockPlugin mockPlugin;

    /**
     * @throws UnknownHostException
     */
    @Before
    public void setUp() throws UnknownHostException {
        startupAddress = InetAddress.getLocalHost();
    }
    
    /**
     * Library integration and communication test
     * @throws Exception 
     */
    @Test
    public void testCommunication() throws Exception {
        mockPlugin = new MockPlugin();
        SwitchConnectionProviderImpl scpimpl = new SwitchConnectionProviderImpl();
        scpimpl.setSwitchConnectionHandler(mockPlugin);
        List<ConnectionConfiguration> configs = new ArrayList<>();
        configs.add(new TestingConnConfigImpl(startupAddress, DEFAULT_PORT, DEFAULT_TLS_SUPPORT));
        scpimpl.configure(configs);
        scpimpl.startup().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        
        int amountOfCLients = 1;
        List<SimpleClient> clients = createAndStartClient(amountOfCLients);
        SimpleClient firstClient = clients.get(0);
        firstClient.getAutomatedPartDone().get();
        mockPlugin.getFinishedFuture().get();
    }
    
    /**
     * Library integration and communication test (with virtual machine)
     * @throws Exception 
     */
    //@Test
    public void testCommunicationWithVM() throws Exception {
        mockPlugin = new MockPlugin();
        SwitchConnectionProviderImpl scpimpl = new SwitchConnectionProviderImpl();
        scpimpl.setSwitchConnectionHandler(mockPlugin);
        List<ConnectionConfiguration> configs = new ArrayList<>();
        configs.add(new TestingConnConfigImpl(startupAddress, DEFAULT_PORT, DEFAULT_TLS_SUPPORT));
        scpimpl.configure(configs);
        scpimpl.startup().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        mockPlugin.getFinishedFuture().get();
    }
    
    /**
     * @param amountOfCLients 
     * @param dataLimit TODO
     * @return new clients up and running
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<SimpleClient> createAndStartClient(int amountOfCLients)
            throws InterruptedException, ExecutionException {
        List<SimpleClient> clientsHorde = new ArrayList<>();
        for (int i = 0; i < amountOfCLients; i++) {
            LOGGER.debug("startup address in createclient: " + startupAddress.getHostAddress());
            SimpleClient sc = new SimpleClient(startupAddress.getHostAddress(), DEFAULT_PORT,
                    getClass().getResourceAsStream(OF_BINARY_MESSAGE_INPUT_TXT));
            sc.setSecuredClient(false);
            clientsHorde.add(sc);
            sc.start();
        }
        for (SimpleClient sc : clientsHorde) {
            try {
                sc.getIsOnlineFuture().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new ExecutionException(e);
            }
        }
        return clientsHorde;
    }
    
    /**
     * @param clients
     * @throws InterruptedException 
     */
    private static void disconnectClients(List<SimpleClient> clients) throws InterruptedException {
        List<io.netty.util.concurrent.Future<?>> disconnectFutureBag = new ArrayList<>();
        for (SimpleClient simpleClient : clients) {
            disconnectFutureBag.add(simpleClient.disconnect());
        }
        for (io.netty.util.concurrent.Future<?> toBeDisconnected : disconnectFutureBag) {
            toBeDisconnected.sync();
        }
    }

}
