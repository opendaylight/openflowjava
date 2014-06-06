/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author jameshall
 */
public class SslKeyStoreTest {

    public SslKeyStoreTest() {
        MockitoAnnotations.initMocks(this);
    }
    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {

    }

    /**
     * 
     */
    @Test
    public void testAsInputStream() throws Exception {
        InputStream inputStream = SslKeyStore.asInputStream("/key.bin");
        assertNotNull( inputStream );
    }

    @Test
    public void testGetCertificatePassword() {
        char[] password = SslKeyStore.getCertificatePassword();
        assertNotNull(password);
        assertTrue (password.length>0) ;

    }

    @Test
    public void testGetKeyStorePassword() {
        char[] password = SslKeyStore.getKeyStorePassword() ;
        assertNotNull(password);
        assertTrue (password.length>0) ;
    }
}

