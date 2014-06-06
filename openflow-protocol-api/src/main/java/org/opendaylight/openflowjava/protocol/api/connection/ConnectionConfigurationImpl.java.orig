/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;

import java.net.InetAddress;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;

/**
 * @author michal.polkorab
 *
 */
public class ConnectionConfigurationImpl implements ConnectionConfiguration {

    private InetAddress address;
    private int port;
    private Object transferProtocol;
    private TlsConfiguration tlsConfig;
    private long switchIdleTimeout;

    /**
     * Creates {@link ConnectionConfigurationImpl}
     * @param address 
     * @param port
     * @param tlsSupport
     * @param switchIdleTimeout
     */
    public ConnectionConfigurationImpl(InetAddress address, int port, TlsConfiguration tlsConfig, long switchIdleTimeout) {
        this.address = address;
        this.port = port;
        this.tlsConfig = tlsConfig;
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
    public long getSwitchIdleTimeout() {
        return switchIdleTimeout;
    }

    @Override
    public Object getSslContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TlsConfiguration getTlsConfiguration() {
        return tlsConfig;
    }

}
