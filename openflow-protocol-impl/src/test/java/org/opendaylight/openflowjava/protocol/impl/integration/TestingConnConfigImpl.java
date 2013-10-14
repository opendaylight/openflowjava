/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;

/**
 * @author michal.polkorab
 *
 */
public class TestingConnConfigImpl implements ConnectionConfiguration {

    private InetAddress address;
    private int port;
    private Object transferProtocol;
    private FEATURE_SUPPORT tlsSupport;
    private long switchIdleTimeout;
    
    // TODO - implement transferProtocol
    public TestingConnConfigImpl(InetAddress address, int port, FEATURE_SUPPORT tlsSupport, long switchIdleTimeout) {
        this.address = address;
        this.port = port;
        this.tlsSupport = tlsSupport;
        this.switchIdleTimeout = switchIdleTimeout;
    }
    
    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Object getTransferProtocol() {
        return transferProtocol;
    }

    @Override
    public FEATURE_SUPPORT getTlsSupport() {
        return tlsSupport;
    }

    @Override
    public long getSwitchIdleTimeout() {
        return switchIdleTimeout;
    }

    @Override
    public Object getSslContext() {
        // TODO Auto-generated method stub
        return null;
    }

}
