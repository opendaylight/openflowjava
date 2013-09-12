/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
 *
 */
public class SwitchConnectionProviderImpl implements SwitchConnectionProvider {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(SwitchConnectionProviderImpl.class);
    private SwitchConnectionHandler switchConnectionHandler;
    private Set<ServerFacade> serverLot;

    @Override
    public void configure(Collection<ConnectionConfiguration> connConfigs) {
        LOG.debug("configurating ..");

        //TODO - add and configure servers according to configuration
        serverLot = new HashSet<>();
        serverLot.add(new TcpHandler(6633));
    }

    @Override
    public void setSwitchConnectionListener(SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }

    @Override
    public Future<Boolean> shutdown() {
        LOG.debug("shutdown summoned");
        for (ServerFacade server : serverLot) {
            server.shutdown();
        }
        return null;
    }

    @Override
    public Future<List<Boolean>> startup() {
        LOG.debug("startup summoned");
        ListenableFuture<List<Boolean>> result = SettableFuture.create();
        try {
            //TODO - check config, status of servers
            if (switchConnectionHandler == null) {
                throw new IllegalStateException("switchConnectionHandler is not set");
            }
            
            // starting
            List<ListenableFuture<Boolean>> starterChain = new ArrayList<>();
            for (ServerFacade server : serverLot) {
                new Thread(server).start();
                ListenableFuture<Boolean> isOnlineFuture = server.getIsOnlineFuture();
                starterChain.add(isOnlineFuture);
            }
            
            if (!starterChain.isEmpty()) {
                result = Futures.allAsList(starterChain);
            } else {
                throw new IllegalStateException("no servers configured");
            }
        } catch (Exception e) {
            SettableFuture<List<Boolean>> exFuture = SettableFuture.create();
            exFuture.setException(e);
            result = exFuture;
        }
        return result;
    }

}
