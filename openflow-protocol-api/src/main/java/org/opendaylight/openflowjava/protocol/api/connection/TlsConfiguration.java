/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.KeystoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.PathType;


/**
 * @author michal.polkorab
 *
 */
public interface TlsConfiguration {

    /**
     * @return keystore location
     */
    public String getTlsKeystore();
    
    /**
     * @return keystore type
     */
    public KeystoreType getTlsKeystoreType();
    
    /**
     * @return truststore location
     */
    public String getTlsTruststore();
    
    /**
     * @return truststore type
     */
    public KeystoreType getTlsTruststoreType();

    /**
     * @return keystore path type (CLASSPATH or PATH)
     */
    public PathType getTlsKeystorePathType();

    /**
     * @return truststore path type (CLASSPATH or PATH)
     */
    public PathType getTlsTruststorePathType();

    /**
     * @return password protecting specified keystore
     */
    public String getKeystorePassword();

    /**
     * @return password protecting certificate
     */
    public String getCertificatePassword();

    /**
     * @return password protecting specified truststore
     */
    public String getTruststorePassword();
}
