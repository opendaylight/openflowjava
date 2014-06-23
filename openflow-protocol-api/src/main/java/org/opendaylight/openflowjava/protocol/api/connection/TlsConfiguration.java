/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;


/**
 * @author michal.polkorab
 *
 */
public interface TlsConfiguration {

    /**
     * @return encryption feature support
     */
    public boolean isTlsSupported();
    
    /**
     * @return keystore location
     */
    public String getTlsKeystore();
    
    /**
     * @return keystore type
     */
    public String getTlsKeystoreType();
    
    /**
     * @return truststore location
     */
    public String getTlsTruststore();
    
    /**
     * @return truststore type
     */
    public String getTlsTruststoreType();

    /**
     * @return keystore path type (classpath or path)
     */
    public String getTlsKeystorePathType();

    /**
     * @return truststore path type (classpath or path)
     */
    public String getTlsTruststorePathType();
}
