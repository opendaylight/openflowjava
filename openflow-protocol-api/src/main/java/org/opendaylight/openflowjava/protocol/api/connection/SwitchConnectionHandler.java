/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.api.connection;

import java.net.InetAddress;

/**
 * @author mirehak
 * @author michal.polkorab
 *
 */
public interface SwitchConnectionHandler {
    
    /**
     * @param connection to switch proving message sending/receiving, connection management
     */
    public void onSwitchConnected(ConnectionAdapter connection);
    
    /**
     * @param switchAddress
     * @return true, if connection from switch having given address shell be accepted; false otherwise
     */
    public boolean accept(InetAddress switchAddress);

}
