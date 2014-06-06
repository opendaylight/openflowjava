/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfigurationImpl;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfigurationImpl;
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
import org.opendaylight.openflowjava.protocol.impl.integration.MockPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class IntegrationTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(IntegrationTest.class);
    
    private static int port = 6633;
    private TlsConfiguration tlsConfiguration ;
    private static final int SWITCH_IDLE_TIMEOUT = 2000;
    private static final long CONNECTION_TIMEOUT = 2000;
    private InetAddress startupAddress;
    private org.opendaylight.openflowjava.protocol.impl.integration.MockPlugin mockPlugin;
    private SwitchConnectionProviderImpl switchConnectionProvider;
    private ConnectionConfigurationImpl connConfig;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        LOGGER.debug("\n starting test -------------------------------");
        
        startupAddress = InetAddress.getLocalHost();
    	tlsConfiguration = new TlsConfigurationImpl("JKS", "/selfSignedSwitch", true, "JKS", "/selfSignedController" ) ;
        connConfig = new ConnectionConfigurationImpl(startupAddress, port, tlsConfiguration, SWITCH_IDLE_TIMEOUT);
        mockPlugin = new MockPlugin();
        
        switchConnectionProvider = new SwitchConnectionProviderImpl();
        switchConnectionProvider.setSwitchConnectionHandler(mockPlugin);
        switchConnectionProvider.setConfiguration(connConfig);
        switchConnectionProvider.startup().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);        
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
//        mockPlugin.shutdown();
//        mockPlugin.getFinishedFuture().get();
        switchConnectionProvider.close();
        LOGGER.debug("\n ending test -------------------------------");

    }

    /**
     * Library integration and communication test with handshake
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
        Thread.sleep(1000);
        LOGGER.debug("testHandshake() Finished") ;
    }

    /**
     * Library integration and communication test with handshake + echo exchange
     * @throws Exception 
     */
    @Test
    public void testHandshakeAndEcho() throws Exception {
        int amountOfCLients = 1;
        Stack<ClientEvent> scenario = ScenarioFactory.createHandshakeScenario();
        scenario.add(0, new SleepEvent(1000));
        scenario.add(0, new SendEvent(ByteBufUtils.hexStringToBytes("04 02 00 08 00 00 00 04")));
        scenario.add(0, new SleepEvent(1000));
        scenario.add(0, new WaitForMessageEvent(ByteBufUtils.hexStringToBytes("04 03 00 08 00 00 00 04")));
        ScenarioHandler handler = new ScenarioHandler(scenario);
        List<SimpleClient> clients = createAndStartClient(amountOfCLients, handler);
        SimpleClient firstClient = clients.get(0);
        firstClient.getScenarioDone().get();

        LOGGER.debug("testHandshakeAndEcho() Finished") ;
    }
    
    /**
     * @param amountOfCLients 
     * @return new clients up and running
     * @throws ExecutionException if some client could not start
     */
    private List<SimpleClient> createAndStartClient(int amountOfCLients, ScenarioHandler scenarioHandler)
            throws ExecutionException {
        List<SimpleClient> clientsHorde = new ArrayList<>();
        for (int i = 0; i < amountOfCLients; i++) {
            LOGGER.debug("startup address in createclient: " + startupAddress.getHostAddress());
            SimpleClient sc = new SimpleClient(startupAddress.getHostAddress(), port);//startupAddress.getHostAddress(), port);
            sc.setSecuredClient(true);
            sc.setScenarioHandler(scenarioHandler);
            clientsHorde.add(sc);
            sc.start();
        }
        for (SimpleClient sc : clientsHorde) {
            try {
                sc.getIsOnlineFuture().get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOGGER.error("createAndStartClient: Something borked ... ", e.getMessage(), e);
                throw new ExecutionException(e);
            }
        }
        return clientsHorde;
    }

}
