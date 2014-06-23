/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfigurationImpl;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterFactory;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionFacade;
import org.opendaylight.openflowjava.protocol.impl.core.PublishingChannelInitializer.COMPONENT_NAMES;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow._switch.connection.provider.impl.rev140328.Tls;

/**
 *
 * @author james.hall
 */
public class PublishingChannelInitializerTest {

    @Mock SocketChannel mockSocketCh ;
    @Mock ChannelPipeline mockChPipeline ;
    @Mock SwitchConnectionHandler mockSwConnHandler ;
    @Mock ConnectionAdapterFactory mockConnAdaptorFactory;
    @Mock DefaultChannelGroup mockChGrp ;
    @Mock ConnectionFacade mockConnFacade ;
    @Mock Tls mockTls ;
    SSLEngine sslEngine ;

    @Mock SerializationFactory mockSerializationFactory ;
    @Mock DeserializationFactory mockDeserializationFactory ;


    TlsConfiguration tlsConfiguration ;
    InetSocketAddress inetSockAddr;

    PublishingChannelInitializer pubChInitializer  ;

    public PublishingChannelInitializerTest() {
        MockitoAnnotations.initMocks(this);
    }
    /**
     * Sets up test environment
     * 
     */
    @Before
    public void setUp() throws Exception {
      
        pubChInitializer= new PublishingChannelInitializer(mockChGrp, mockConnAdaptorFactory) ;
        pubChInitializer.setSerializationFactory(mockSerializationFactory);
        pubChInitializer.setDeserializationFactory(mockDeserializationFactory);
        pubChInitializer.setSwitchIdleTimeout(1) ;
        pubChInitializer.getConnectionIterator() ;

        when( mockChGrp.size()).thenReturn(1) ;
        pubChInitializer.setSwitchConnectionHandler( mockSwConnHandler ) ;

        inetSockAddr = new InetSocketAddress(InetAddress.getLocalHost(), 8675 ) ;

        when(mockConnAdaptorFactory.createConnectionFacade(mockSocketCh))
        .thenReturn(mockConnFacade);
        when(mockSocketCh.remoteAddress()).thenReturn(inetSockAddr) ;
        when(mockSocketCh.localAddress()).thenReturn(inetSockAddr) ;
        when(mockSocketCh.remoteAddress()).thenReturn(inetSockAddr) ;
        when(mockSwConnHandler.accept(eq(InetAddress.getLocalHost()))).thenReturn(true) ;
        when(mockSocketCh.pipeline()).thenReturn(mockChPipeline) ;
        
        tlsConfiguration = new TlsConfigurationImpl("JKS", "src/main/resources/selfSignedSwitch", true, "JKS", "src/main/resources/selfSignedController");
    }


    @Test
    public void testinitChannelEncryptionSet()  {
    	pubChInitializer.setTlsConfiguration(tlsConfiguration);
        pubChInitializer.initChannel(mockSocketCh) ;

        verifyCommonHandlers();
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.SSL_HANDLER.name()),any(SslHandler.class)) ;

        // To explain why there shouldn't be an SSL_HANDLER in the pipeline ... that will be added when the TLS Detector runs.
    }

    @Test
    public void testinitChannelEncryptionSetNullTls()  {
    	pubChInitializer.setTlsConfiguration(null);
        pubChInitializer.initChannel(mockSocketCh) ;

        verifyCommonHandlers();
        verify(mockChPipeline, times(0)).addLast(eq(COMPONENT_NAMES.SSL_HANDLER.name()),any(SslHandler.class)) ;

        // To explain why there shouldn't be an SSL_HANDLER in the pipeline ... that will be added when the TLS Detector runs.
    }

    @Test
    public void testinitChannelEncryptionNotSet()  {

        // Without encryption, only the common
        pubChInitializer.initChannel(mockSocketCh) ;

        verifyCommonHandlers();
    }

    @Test
    public void testinitChannelNoEncryptionAcceptFails() throws UnknownHostException  {

        when(mockSwConnHandler.accept(eq(InetAddress.getLocalHost()))).thenReturn(false) ;
        pubChInitializer.initChannel(mockSocketCh) ;

        verify(mockSocketCh, times(1)).disconnect();
        verify(mockChPipeline, times(0))
        .addLast( any(String.class), any(ChannelHandler.class) ) ;
    }

    @Test
    public void testExceptionThrown() {

        doThrow(new IllegalArgumentException()).when(mockSocketCh).pipeline() ;
        pubChInitializer.initChannel(mockSocketCh);

        verify( mockSocketCh, times(1)).close() ;
    }
    
    /**
     * All paths should install these six handlers:
     */
    private void verifyCommonHandlers() {
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.IDLE_HANDLER.name()),any(IdleHandler.class)) ;
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.OF_DECODER.name()),any(OFDecoder.class)) ;
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.OF_ENCODER.name()),any(OFEncoder.class)) ;
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.OF_FRAME_DECODER.name()),any(OFFrameDecoder.class)) ;
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.OF_VERSION_DETECTOR.name()),any(OFVersionDetector.class)) ;
        verify(mockChPipeline, times(1)).addLast(eq(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER.name()),any(DelegatingInboundHandler.class));
        assertEquals(1, pubChInitializer.size()) ;

    }
}
