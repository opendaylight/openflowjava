/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.connection;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration.FEATURE_SUPPORT;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Exposed class for server handling
 * @author mirehak
 * @author michal.polkorab
 */
public class SwitchConnectionProviderImpl implements SwitchConnectionProvider {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SwitchConnectionProviderImpl.class);
    private SwitchConnectionHandler switchConnectionHandler;
    private Set<ServerFacade> serverLot;

    @Override
    public void configure(Collection<ConnectionConfiguration> connConfigs) {
        LOGGER.debug("Configuring ..");

        //TODO - configure servers according to configuration
        serverLot = new HashSet<>();
        for (ConnectionConfiguration connConfig : connConfigs) {
            TcpHandler server = new TcpHandler(connConfig.getAddress(), connConfig.getPort());
            server.setSwitchConnectionHandler(switchConnectionHandler);
            server.setSwitchIdleTimeout(connConfig.getSwitchIdleTimeout());
            boolean tlsSupported = FEATURE_SUPPORT.REQUIRED.equals(connConfig.getTlsSupport());
            server.setEncryption(tlsSupported);
            serverLot.add(server);
        }
    }

    @Override
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        LOGGER.debug("setSwitchConnectionHandler");
        this.switchConnectionHandler = switchConnectionHandler;
    }

    @Override
    public Future<List<Boolean>> shutdown() {
        LOGGER.debug("Shutdown summoned");
        ListenableFuture<List<Boolean>> result = SettableFuture.create();
        try {
            List<ListenableFuture<Boolean>> shutdownChain = new ArrayList<>();
            for (ServerFacade server : serverLot) {
                ListenableFuture<Boolean> shutdownFuture = server.shutdown();
                shutdownChain.add(shutdownFuture);
            }
            if (!shutdownChain.isEmpty()) {
                result = Futures.allAsList(shutdownChain);
            } else {
                throw new IllegalStateException("No servers configured");
            }
        } catch (Exception e) {
            SettableFuture<List<Boolean>> exFuture = SettableFuture.create();
            exFuture.setException(e);
            result = exFuture;
        }
        return result;
    }

    @Override
    public Future<List<Boolean>> startup() {
        LOGGER.debug("startup summoned");
        ListenableFuture<List<Boolean>> result = SettableFuture.create();
        try {
            if (serverLot.isEmpty()) {
                throw new IllegalStateException("No servers configured");
            }
            for (ServerFacade server : serverLot) {
                if (server.getIsOnlineFuture().isDone()) {
                    throw new IllegalStateException("Servers already running");
                }
            }
            if (switchConnectionHandler == null) {
                throw new IllegalStateException("switchConnectionHandler is not set");
            }
            List<ListenableFuture<Boolean>> starterChain = new ArrayList<>();
            for (ServerFacade server : serverLot) {
                new Thread(server).start();
                ListenableFuture<Boolean> isOnlineFuture = server.getIsOnlineFuture();
                starterChain.add(isOnlineFuture);
            }
            if (!starterChain.isEmpty()) {
                result = Futures.allAsList(starterChain);
            } else {
                throw new IllegalStateException("No servers configured");
            }
        } catch (Exception e) {
            SettableFuture<List<Boolean>> exFuture = SettableFuture.create();
            exFuture.setException(e);
            result = exFuture;
        }
        return result;
    }

    /**
     * @return servers
     */
    public Set<ServerFacade> getServerLot() {
        return serverLot;
    }

  @Override
  public void close() throws Exception {
       shutdown();
  }
}
