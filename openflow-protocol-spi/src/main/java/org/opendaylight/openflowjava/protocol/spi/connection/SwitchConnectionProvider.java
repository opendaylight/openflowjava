/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.spi.connection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;

/**
 * @author mirehak
 *
 */
public interface SwitchConnectionProvider {

    /**
     * @param configurations list of [protocol, port, address and supported features]
     */
    public void configure(Collection<ConnectionConfiguration> configurations);
    
    /**
     * start listening to switches, but please don't forget to do
     * {@link #setSwitchConnectionHandler(SwitchConnectionHandler)} first
     * @return future, triggered to true, when all listening channels are up and running
     */
    public Future<List<Boolean>> startup();
    
    /**
     * stop listening to switches
     * @return future, triggered to true, when all listening channels are down
     */
    public Future<Boolean> shutdown();
    
    /**
     * @param switchConHandler instance being informed when new switch connects
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConHandler);
    
}
