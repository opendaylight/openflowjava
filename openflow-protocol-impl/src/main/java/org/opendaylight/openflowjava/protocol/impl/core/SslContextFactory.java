/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.KeystoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.PathType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for setting up TLS connection.
 * 
 * @author michal.polkorab
 */
public class SslContextFactory {

    // "TLS" - supports some version of TLS
    // Use "TLSv1", "TLSv1.1", "TLSv1.2" for specific TLS version
    private static final String PROTOCOL = "TLS";
    private String keystore;
    private KeystoreType keystoreType;
    private String truststore;
    private KeystoreType truststoreType;
    private PathType keystorePathType;
    private PathType truststorePathType;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SslContextFactory.class);

    /**
     * @param tlsConfig
     *            TLS configuration object, contains keystore locations +
     *            keystore types
     */
    public SslContextFactory(TlsConfiguration tlsConfig) {
        keystore = tlsConfig.getTlsKeystore();
        keystoreType = tlsConfig.getTlsKeystoreType();
        keystorePathType = tlsConfig.getTlsKeystorePathType();
        truststore = tlsConfig.getTlsTruststore();
        truststoreType = tlsConfig.getTlsTruststoreType();
        truststorePathType = tlsConfig.getTlsTruststorePathType();
    }

    /**
     * @return servercontext
     */
    public SSLContext getServerContext() {
        String algorithm = Security
                .getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        SSLContext serverContext = null;
        try {
            KeyStore ks = KeyStore.getInstance(keystoreType.name());
            ks.load(SslKeyStore.asInputStream(keystore, keystorePathType),
                    SslKeyStore.getKeyStorePassword());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, SslKeyStore.getCertificatePassword());

            KeyStore ts = KeyStore.getInstance(truststoreType.name());
            ts.load(SslKeyStore.asInputStream(truststore, truststorePathType),
                    SslKeyStore.getKeyStorePassword());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(ts);

            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (IOException e) {
            LOGGER.warn("IOException - Failed to load keystore / truststore."
                    + " Failed to initialize the server-side SSLContext", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("NoSuchAlgorithmException - Unsupported algorithm."
                    + " Failed to initialize the server-side SSLContext", e);
        } catch (CertificateException e) {
            LOGGER.warn("CertificateException - Unable to access certificate (check password)."
                    + " Failed to initialize the server-side SSLContext", e);
        } catch (Exception e) {
            LOGGER.warn("Exception - Failed to initialize the server-side SSLContext", e);
        }
        return serverContext;
    }
}
