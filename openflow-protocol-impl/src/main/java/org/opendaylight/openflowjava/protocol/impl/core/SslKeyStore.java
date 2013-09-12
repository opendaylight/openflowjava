/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import java.io.InputStream;

/**
 * Class for storing keys
 *
 * @author michal.polkorab
 */
public final class SslKeyStore {

    private static final String filename = "/key.bin";

    /**
     * InputStream instance of key
     *
     * @return key as InputStream
     */
    public static InputStream asInputStream() {
        InputStream in = SslKeyStore.class.getResourceAsStream(filename);
        if (in == null) {
            throw new IllegalStateException("KeyStore file not found: " + filename);
        }
        return in;
    }

    /**
     * @return certificate password as char[]
     */
    public static char[] getCertificatePassword() {
        return "secret".toCharArray();
    }

    /**
     * @return KeyStore password as char[]
     */
    public static char[] getKeyStorePassword() {
        return "secret".toCharArray();
    }
}