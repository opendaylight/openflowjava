/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author mirehak
 * @author michal.polkorab
 */
public class SwitchConnectionProviderImpl implements SwitchConnectionProvider {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(SwitchConnectionProviderImpl.class);
    private SwitchConnectionHandler switchConnectionHandler;
    private Set<ServerFacade> serverLot;

    @Override
    public void configure(Collection<ConnectionConfiguration> connConfigs) {
        LOG.debug("Configurating ..");

        //TODO - configure servers according to configuration
        serverLot = new HashSet<>();
        for (Iterator<ConnectionConfiguration> iterator = connConfigs.iterator(); iterator.hasNext();) {
            ConnectionConfiguration connConfig = iterator.next();
            serverLot.add(new TcpHandler(connConfig.getAddress(), connConfig.getPort()));
        }
    }

    @Override
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        LOG.debug("setSwitchConnectionHanler");
        this.switchConnectionHandler = switchConnectionHandler;
    }

    @Override
    public Future<List<Boolean>> shutdown() {
        LOG.debug("Shutdown summoned");
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
        LOG.debug("startup summoned");
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

}
