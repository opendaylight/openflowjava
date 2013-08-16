/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import java.io.FileNotFoundException;
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
     * @throws FileNotFoundException
     */
    public static InputStream asInputStream() throws FileNotFoundException {
        InputStream in = SslKeyStore.class.getResourceAsStream(filename);
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