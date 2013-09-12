/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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
