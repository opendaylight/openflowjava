/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;

/**
 * @author michal.polkorab
 *
 */
public class TestingConnConfigImpl implements ConnectionConfiguration {

    private InetAddress address;
    private int port;
    private Object transferProtocol;
    private TlsConfiguration tlsSupport;
    private long switchIdleTimeout;

    /**
     * Creates {@link TestingConnConfigImpl}
     * @param address 
     * @param port
     * @param tlsSupport
     * @param switchIdleTimeout
     */
    public TestingConnConfigImpl(InetAddress address, int port, boolean tlsSupport, long switchIdleTimeout) {
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
        // TODO Auto-generated method stub
        return null;
    }

}
