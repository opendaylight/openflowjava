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
public class TlsConfigurationImpl implements TlsConfiguration {

    private KeystoreType trustStoreType;
    private String trustStore;
    private KeystoreType keyStoreType;
    private String keyStore;
    private PathType keystorePathType;
    private PathType truststorePathType;

    /**
     * Default constructor
     * @param trustStoreType JKS or PKCS12
     * @param trustStore path to trustStore file
     * @param trustStorePathType truststore path type (classpath or path)
     * @param keyStoreType JKS or PKCS12
     * @param keyStore path to keyStore file
     * @param keyStorePathType keystore path type (classpath or path)
     */
    public TlsConfigurationImpl(KeystoreType trustStoreType, String trustStore,
            PathType trustStorePathType, KeystoreType keyStoreType,
            String keyStore, PathType keyStorePathType) {
        this.trustStoreType = trustStoreType;
        this.trustStore = trustStore;
        this.truststorePathType = trustStorePathType;
        this.keyStoreType = keyStoreType;
        this.keyStore = keyStore;
        this.keystorePathType = keyStorePathType;
    }

    @Override
    public KeystoreType getTlsTruststoreType() {
        return trustStoreType;
    }

    @Override
    public String getTlsTruststore() {
        return trustStore;
    }

    @Override
    public KeystoreType getTlsKeystoreType() {
        return keyStoreType;
    }

    @Override
    public String getTlsKeystore() {
        return keyStore;
    }

    @Override
    public PathType getTlsKeystorePathType() {
        return keystorePathType;
    }

    @Override
    public PathType getTlsTruststorePathType() {
        return truststorePathType;
    }
}
