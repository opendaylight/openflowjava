/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;

import com.google.common.util.concurrent.ListenableFuture;

/**
 *
 * @author jameshall
 */
public class TcpHandlerTest {

    private static final int serverPort = 28001;
    private InetAddress serverAddress = InetAddress.getLoopbackAddress() ;
    private Socket firstBinder ;
    @Mock ChannelHandlerContext mockChHndlrCtx ;
    @Mock PublishingChannelInitializer mockChannelInitializer;
    @Mock SwitchConnectionHandler mockSwitchConnHndler ;
    @Mock SerializationFactory mockSerializationFactory ;
    @Mock DeserializationFactory mockDeserializationFactory ;

    TcpHandler tcpHandler ;

    public TcpHandlerTest() {
        MockitoAnnotations.initMocks(this);
    }
    /**
     * Sets up test environment
     * 
     */
    @Before
    public void setUp() {
        firstBinder = new Socket();
    }

    @After
    public void cleanUp() throws IOException {
        firstBinder.close() ;
    }
    @Test
    public void testRunWithNullAddress() throws IOException, InterruptedException, ExecutionException  {

        tcpHandler = new TcpHandler( mockChannelInitializer, null, serverPort ) ;

        assertEquals("failed to start server", true, startupServer()) ;
        assertEquals("failed to connect client", true, clientConnection()) ;
        shutdownServer();
    }

    @Test
    public void testRunWithAddress() throws IOException, InterruptedException, ExecutionException  {

        tcpHandler = new TcpHandler( mockChannelInitializer, serverAddress, serverPort ) ;

        assertEquals("failed to start server", true, startupServer()) ;
        assertEquals("failed to connect client", true, clientConnection()) ;
        shutdownServer();
    }

    @Test
    public void testRunWithEncryption () throws InterruptedException, IOException, ExecutionException {

        tcpHandler = new TcpHandler( mockChannelInitializer, serverAddress, serverPort ) ;

        assertEquals( "failed to start server", true, startupServer()) ;
        assertEquals( "wrong connection count", 0, tcpHandler.getNumberOfConnections() );
        assertEquals( "wrong port", serverPort, tcpHandler.getPort() );
        assertEquals( "wrong address", serverAddress.getHostAddress(), tcpHandler.getAddress()) ;

        assertEquals("failed to connect client", true, clientConnection()) ;

        shutdownServer();
    }

    @Test
    public void testSocketAlreadyInUse() throws IOException, InterruptedException, ExecutionException {

        firstBinder.bind(new InetSocketAddress(serverAddress, serverPort));
        tcpHandler = new TcpHandler( mockChannelInitializer, serverAddress, serverPort ) ;

        assertEquals( false, startupServer() ) ;
    }
    /**
     * Trigger the server shutdown and wait 2 seconds for completion
     */
    private void shutdownServer() throws InterruptedException, ExecutionException {
        ListenableFuture<Boolean> shutdownRet = tcpHandler.shutdown() ;
        while ( shutdownRet.isDone() != true )
            Thread.sleep(100) ;
        assertEquals("shutdown failed", true, shutdownRet.get());
    }

    /**
     * @throws InterruptedException
     * @throws IOException
     * @throws ExecutionException
     */
    private Boolean startupServer() throws InterruptedException, IOException, ExecutionException {
        ListenableFuture<Boolean> online = tcpHandler.getIsOnlineFuture();

        try {
            (new Thread(tcpHandler)).start();
            int retry = 0;
            while (online.isDone() != true && retry++ < 20) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            System.out.println("startup exception " + e );
        }
        return online.isDone() ;
    }
    /**
     * @throws IOException
     */
    private Boolean clientConnection() throws IOException {
        // Connect, and disconnect
        Socket socket = new Socket(InetAddress.getLoopbackAddress(), serverPort );
        Boolean result = socket.isConnected();
        socket.close() ;
        return result ;
    }
}
