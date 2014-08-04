/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.api.connection;

import java.net.InetAddress;

import javax.net.ssl.SSLEngine;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEvent;

/**
 * @author mirehak
 *
 */
public interface ConnectionConfiguration {

    /**
     * @return address to bind, if null, all available interfaces will be used
     */
    public InetAddress getAddress();
    
    /**
     * @return port to bind
     */
    public int getPort();
    
    /**
     * @return transport protocol to use
     */
    public Object getTransferProtocol();
    
    /**
     * @return TLS configuration object
     */
    public TlsConfiguration getTlsConfiguration();
    
    /**
     * @return silence time (in milliseconds) - after this time {@link SwitchIdleEvent} message is sent upstream 
     */
    public long getSwitchIdleTimeout();
    
    /**
     * @return seed for {@link SSLEngine}
     */
    public Object getSslContext();
    
    /**
     * @return thread numbers for TcpHandler's eventloopGroups
     */
    public ThreadConfiguration getThreadConfiguration();
}