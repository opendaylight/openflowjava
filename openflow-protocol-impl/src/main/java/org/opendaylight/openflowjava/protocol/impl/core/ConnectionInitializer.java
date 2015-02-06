package org.opendaylight.openflowjava.protocol.impl.core;

/**
 * @author martin.uhlir
 *
 */
public interface ConnectionInitializer {

    /**
     * Initiates connection towards device
     * @param host - host IP
     * @param port - port number
     */
    void initiateConnection(String host, int port);

}
