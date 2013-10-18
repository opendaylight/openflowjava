/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration.FEATURE_SUPPORT;
import org.opendaylight.openflowjava.protocol.impl.clients.ClientEvent;
import org.opendaylight.openflowjava.protocol.impl.clients.ScenarioFactory;
import org.opendaylight.openflowjava.protocol.impl.clients.ScenarioHandler;
import org.opendaylight.openflowjava.protocol.impl.clients.SendEvent;
import org.opendaylight.openflowjava.protocol.impl.clients.SimpleClient;
import org.opendaylight.openflowjava.protocol.impl.clients.SleepEvent;
import org.opendaylight.openflowjava.protocol.impl.clients.WaitForMessageEvent;
import org.opendaylight.openflowjava.protocol.impl.connection.SwitchConnectionProviderImpl;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class IntegrationTest {

    /** Name of file in which OpenFLow protocol messages are stored in binary format */
    private static int port;
    private static final FEATURE_SUPPORT DEFAULT_TLS_SUPPORT = FEATURE_SUPPORT.NOT_SUPPORTED;
    private static final int SWITCH_IDLE_TIMEOUT = 2000;

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(IntegrationTest.class);

    private static final long CONNECTION_TIMEOUT = 2000;
    private InetAddress startupAddress;
    private MockPlugin mockPlugin;
    private SwitchConnectionProviderImpl scpimpl;
    private List<ConnectionConfiguration> configs;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        LOGGER.debug("\n\nstarting test -------------------------------");
        startupAddress = InetAddress.getLocalHost();
        mockPlugin = new MockPlugin();
        scpimpl = new SwitchConnectionProviderImpl();
        scpimpl.setSwitchConnectionHandler(mockPlugin);
        configs = new ArrayList<>();
        configs.add(new TestingConnConfigImpl(startupAddress, 0, DEFAULT_TLS_SUPPORT, SWITCH_IDLE_TIMEOUT));
        scpimpl.configure(configs);
        scpimpl.startup().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        TcpHandler server = (TcpHandler) scpimpl.getServerLot().iterator().next();
        port = server.getPort();
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(500);
    }

    /**
     * Library integration and communication test
     * @throws Exception 
     */
    @Test
    public void testHandshake() throws Exception {
        int amountOfCLients = 1;
        Stack<ClientEvent> scenario = ScenarioFactory.createHandshakeScenario();
        ScenarioHandler handler = new ScenarioHandler(scenario);
        List<SimpleClient> clients = createAndStartClient(amountOfCLients, handler);
        SimpleClient firstClient = clients.get(0);
        firstClient.getScenarioDone().get();
        mockPlugin.shutdown();
        mockPlugin.getFinishedFuture().get();
    }

    /**
     * Library integration and communication test
     * @throws Exception 
     */
    @Test
    public void testHandshakeAndEcho() throws Exception {
        int amountOfCLients = 1;
        Stack<ClientEvent> scenario = ScenarioFactory.createHandshakeScenario();
        scenario.add(0, new SleepEvent(1500));
        scenario.add(0, new SendEvent(ByteBufUtils.hexStringToBytes("04 02 00 08 00 00 00 04")));
        scenario.add(0, new SleepEvent(1500));
        scenario.add(0, new WaitForMessageEvent(ByteBufUtils.hexStringToBytes("04 03 00 08 00 00 00 04")));
        ScenarioHandler handler = new ScenarioHandler(scenario);
        List<SimpleClient> clients = createAndStartClient(amountOfCLients, handler);
        SimpleClient firstClient = clients.get(0);
        firstClient.getScenarioDone().get();
        mockPlugin.shutdown();
        mockPlugin.getFinishedFuture().get();
    }

    /**
     * Library integration and communication test (with virtual machine)
     * @throws Exception
     */
    //@Test
    public void testCommunicationWithVM() throws Exception {
        mockPlugin.getFinishedFuture().get();
    }
    
    /**
     * @param amountOfCLients 
     * @return new clients up and running
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<SimpleClient> createAndStartClient(int amountOfCLients, ScenarioHandler scenarioHandler)
            throws InterruptedException, ExecutionException {
        List<SimpleClient> clientsHorde = new ArrayList<>();
        for (int i = 0; i < amountOfCLients; i++) {
            LOGGER.debug("startup address in createclient: " + startupAddress.getHostAddress());
            SimpleClient sc = new SimpleClient(startupAddress.getHostAddress(), port);
            sc.setSecuredClient(false);
            sc.setScenarioHandler(scenarioHandler);
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

}
