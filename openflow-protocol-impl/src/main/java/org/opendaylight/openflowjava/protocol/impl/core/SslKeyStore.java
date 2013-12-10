/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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