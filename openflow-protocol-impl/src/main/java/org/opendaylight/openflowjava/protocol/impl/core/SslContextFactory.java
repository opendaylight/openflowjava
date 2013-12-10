/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

/**
 * Class for setting up TLS connection.
 *
 * @author michal.polkorab
 */
public final class SslContextFactory {

    
    // "TLS" - supports some version of TLS
    // Use "TLSv1", "TLSv1.1", "TLSv1.2" for specific TLS version
    private static final String PROTOCOL = "TLS";
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        SSLContext clientContext;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(SslKeyStore.asInputStream(),
                    SslKeyStore.getKeyStorePassword());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, SslKeyStore.getCertificatePassword());

            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (RuntimeException e) {
            throw new Error(
                    "Failed to initialize the server-side SSLContext", e);
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the server-side SSLContext", e);
        }
        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, SslTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
        CLIENT_CONTEXT = clientContext;
    }

    /**
     * @return servercontext
     */
    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    /**
     * @return cliencontext
     */
    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }
}