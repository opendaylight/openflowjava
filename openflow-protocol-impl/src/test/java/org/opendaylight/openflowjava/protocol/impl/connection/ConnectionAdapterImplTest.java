/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 
 * @author madamjak
 *
 */

public class ConnectionAdapterImplTest {

    @Mock ChannelPipeline channnelPipe;
    @Mock Channel channel; 
    @Mock ChannelFuture channelFuture;
    @Before
    public void startUp(){
        MockitoAnnotations.initMocks(this);
        when(channel.pipeline()).thenReturn(channnelPipe);
        when(channel.disconnect()).thenReturn(channelFuture);
    }

    /**
     * Test IsAlive method
     */
    @Test
    public void testIsAlive(){

        byte[] byteaddr = {10,11,12,13};
        int port = 9876;
        InetAddress iaddr = null;
        try {
            iaddr = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException e) {
            Assert.fail("Can not create inet address - FIX test");
        }

        InetSocketAddress inetSockAddr = new InetSocketAddress(iaddr,port);
        ConnectionAdapterImpl connAddapter = new ConnectionAdapterImpl(channel,inetSockAddr);
        
        Assert.assertEquals("Wrong - diffrence between channel.isOpen() and ConnectionAdapterImpl.isAlive()", channel.isOpen(), connAddapter.isAlive());

        connAddapter.disconnect();
        Assert.assertFalse("Wrong - ConnectionAdapterImpl can not be aliva after disconnet.", connAddapter.isAlive());

    }

    /**
     * Test throw exception if no listener are present
     */
    @Test(expected = java.lang.IllegalStateException.class)
    public void testMissingListeners(){

        byte[] byteaddr = {10,11,12,13};
        int port = 9876;
        InetAddress iaddr = null;
        try {
            iaddr = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException e) {
            Assert.fail("Can not create inet address - FIX test");
        }

        InetSocketAddress inetSockAddr = new InetSocketAddress(iaddr,port);
        ConnectionAdapterImpl connAddapter = new ConnectionAdapterImpl(channel,inetSockAddr);
        connAddapter.setSystemListener(null);
        connAddapter.setMessageListener(null);
        connAddapter.setConnectionReadyListener(null);
        connAddapter.checkListeners();

    }

}
