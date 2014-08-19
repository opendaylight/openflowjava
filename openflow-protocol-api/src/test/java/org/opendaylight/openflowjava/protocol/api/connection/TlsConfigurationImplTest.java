/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.KeystoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.PathType;

/**
 * @author michal.polkorab
 *
 */
public class TlsConfigurationImplTest {

    /**
     * Test correct TlsConfigurationImpl creation
     */
    @Test
    public void test() {
        TlsConfigurationImpl config = new TlsConfigurationImpl(KeystoreType.JKS,
                "user/dir", PathType.CLASSPATH, KeystoreType.PKCS12, "/var/lib", PathType.PATH);
        assertEquals("Wrong keystore location", "/var/lib", config.getTlsKeystore());
        assertEquals("Wrong truststore location", "user/dir", config.getTlsTruststore());
        assertEquals("Wrong keystore location", KeystoreType.PKCS12, config.getTlsKeystoreType());
        assertEquals("Wrong truststore location", KeystoreType.JKS, config.getTlsTruststoreType());
        assertEquals("Wrong keystore location", PathType.PATH, config.getTlsKeystorePathType());
        assertEquals("Wrong truststore location", PathType.CLASSPATH, config.getTlsTruststorePathType());
        assertEquals("Wrong keystore location", "opendaylight", config.getCertificatePassword());
        assertEquals("Wrong truststore location", "opendaylight", config.getKeystorePassword());
        assertEquals("Wrong keystore location", "opendaylight", config.getTruststorePassword());
    }
}