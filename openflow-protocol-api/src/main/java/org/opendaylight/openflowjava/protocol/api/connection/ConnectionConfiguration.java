/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.api.connection;

import java.net.InetAddress;

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

}
