/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.junit.Assert.assertNotNull;

import javax.net.ssl.SSLContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfigurationImpl;

/**
 *
 * @author jameshall
 */
public class SslContextFactoryTest {

	SslContextFactory sslContextFactory ;
	TlsConfiguration tlsConfiguration ;
    public SslContextFactoryTest() {
        MockitoAnnotations.initMocks(this);
    }
    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {

    	tlsConfiguration = new TlsConfigurationImpl("JKS", "src/main/resources/ctlTrustStore", true, "JKS", "src/main/resources/ctlKeystore" ) ;
    	sslContextFactory = new SslContextFactory(tlsConfiguration);
    }

    /**
     * 
     */
    @Test
    public void testGetServerContext() throws Exception {
        SSLContext context  = sslContextFactory.getServerContext() ;

        assertNotNull( context );
    }

}

