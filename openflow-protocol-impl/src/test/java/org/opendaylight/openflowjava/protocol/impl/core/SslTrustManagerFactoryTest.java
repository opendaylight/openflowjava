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

import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author jameshall
 */
public class SslTrustManagerFactoryTest {

    public SslTrustManagerFactoryTest() {
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
    public void testGetTrustManagers() throws Exception {
        TrustManager[] tmArray  = SslTrustManagerFactory.getTrustManagers() ;

        assertNotNull( tmArray );
        for ( TrustManager tm : tmArray ) {
            if ( tm.getClass() == X509TrustManager.class ) {
                X509Certificate[] certsArray = ((X509TrustManager)tm).getAcceptedIssuers() ;
                assertTrue( certsArray.length > 0 ) ;

                //                Boolean caught = false;
                //                try {
                //                    ((X509TrustManager)tm).checkClientTrusted( certsArray, TrustManagerFactory.getDefaultAlgorithm()) ;
                //                } catch (CertificateException ce) {
                //                    caught = true ;
                //                }
                //                assertTrue( caught ) ;
                //
                //                caught = false;
                //                try {
                //                    ((X509TrustManager)tm).checkServerTrusted( certsArray, TrustManagerFactory.getDefaultAlgorithm()) ;
                //                } catch (CertificateException ce) {
                //                    caught = true ;
                //                }
                //                assertTrue( caught ) ;
                //            }
            }
        }
    }
}

