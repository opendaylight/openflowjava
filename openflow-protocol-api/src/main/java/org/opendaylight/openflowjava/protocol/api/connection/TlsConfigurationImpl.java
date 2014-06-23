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
public class TlsConfigurationImpl implements TlsConfiguration {

    private String trustStoreType;
    private String trustStore;
    private String keyStoreType;
    private String keyStore;
    Boolean isTlsSupported;
    private String keystorePathType;
    private String truststorePathType;

    /**
     * Default constructor
     * @param trustStoreType JKS or PKCS12
     * @param trustStore path to trustStore file
     * @param trustStorePathType truststore path type (classpath or path)
     * @param isTlsSupported true if communication over TLS is desired, false otherwise
     * @param keyStoreType JKS or PKCS12
     * @param keyStore path to keyStore file
     * @param keyStorePathType keystore path type (classpath or path)
     */
    public TlsConfigurationImpl(String trustStoreType, String trustStore,
            String trustStorePathType, Boolean isTlsSupported, String keyStoreType,
            String keyStore, String keyStorePathType) {
        this.trustStoreType = trustStoreType;
        this.trustStore = trustStore;
        this.truststorePathType = trustStorePathType;
        this.isTlsSupported = isTlsSupported;
        this.keyStoreType = keyStoreType;
        this.keyStore = keyStore;
        this.keystorePathType = keyStorePathType;
    }

    @Override
    public String getTlsTruststoreType() {
        return trustStoreType;
    }

    @Override
    public String getTlsTruststore() {
        return trustStore;
    }

    @Override
    public boolean isTlsSupported() {
        return isTlsSupported;
    }

    @Override
    public String getTlsKeystoreType() {
        return keyStoreType;
    }

    @Override
    public String getTlsKeystore() {
        return keyStore;
    }

    @Override
    public String getTlsKeystorePathType() {
        return keystorePathType;
    }

    @Override
    public String getTlsTruststorePathType() {
        return truststorePathType;
    }
}
