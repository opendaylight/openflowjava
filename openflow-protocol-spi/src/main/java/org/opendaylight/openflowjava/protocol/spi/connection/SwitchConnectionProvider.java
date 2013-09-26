/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.spi.connection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;

/**
 * @author mirehak
 * @author michal.polkorab
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
    public Future<List<Boolean>> shutdown();
    
    /**
     * @param switchConHandler instance being informed when new switch connects
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConHandler);
    
}
