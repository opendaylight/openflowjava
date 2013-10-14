/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

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
     * connection functionality support types
     */
    public enum FEATURE_SUPPORT {
        /** feature is not supported at all */
        NOT_SUPPORTED,
        /** feature is supported */
        SUPPORTED,
        /** feature is supported and has to be used by clients */
        REQUIRED
    }
    
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
     * @return encryption feature support
     */
    public FEATURE_SUPPORT getTlsSupport();
    
    /**
     * @return silence time (in milliseconds) - after this time {@link SwitchIdleEvent} message is sent upstream 
     */
    public long getSwitchIdleTimeout();
    
    /**
     * @return seed for {@link SSLEngine}
     */
    public Object getSslContext();

}
