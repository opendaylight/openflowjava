/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.connection;

import static org.mockito.Mockito.when;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionReadyListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @author madamjak
 * @author michal.polkorab
 */
public class ConnectionAdapterImp02lTest {
    private static final int RPC_RESPONSE_EXPIRATION = 1;
    private static final RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>> REMOVAL_LISTENER =
            new RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>>() {
        @Override
        public void onRemoval(
                final RemovalNotification<RpcResponseKey, ResponseExpectedRpcListener<?>> notification) {
            notification.getValue().discard();
        }
    };

    @Mock SocketChannel channel;
    @Mock ChannelPipeline pipeline;
    @Mock OpenflowProtocolListener messageListener;
    @Mock SystemNotificationsListener systemListener;
    @Mock ConnectionReadyListener readyListener;
    @Mock Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> mockCache;
    @Mock ChannelFuture channelFuture;
    @Mock EchoInput echoInput;
    @Mock BarrierInput barrierInput;
    private ConnectionAdapterImpl adapter;
    private Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> cache;

    /**
     * Initializes ConnectionAdapter
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(channel.pipeline()).thenReturn(pipeline);
        adapter = new ConnectionAdapterImpl(channel, InetSocketAddress.createUnresolved("10.0.0.1", 6653));
        adapter.setMessageListener(messageListener);
        adapter.setSystemListener(systemListener);
        adapter.setConnectionReadyListener(readyListener);
        cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(RPC_RESPONSE_EXPIRATION, TimeUnit.MINUTES)
                .removalListener(REMOVAL_LISTENER).build();
        adapter.setResponseCache(cache);
        when(channel.disconnect()).thenReturn(channelFuture);
    }

    /**
     * Test echo (by set cache to null)
     */
    @Test(expected = java.lang.NullPointerException.class)
    public void testEcho(){
        int port = 9876;
        String host ="localhost";
        InetSocketAddress inetSockAddr = InetSocketAddress.createUnresolved(host, port);
        ConnectionAdapterImpl connAddapter = new ConnectionAdapterImpl(channel,inetSockAddr);
        connAddapter.setResponseCache(null);
        connAddapter.echo(echoInput);
        connAddapter.disconnect();
    }

    /**
     * Test echo (by set cache to null)
     */
    @Test(expected = java.lang.NullPointerException.class)
    public void testBarier(){
        int port = 9876;
        String host ="localhost";
        InetSocketAddress inetSockAddr = InetSocketAddress.createUnresolved(host, port);
        ConnectionAdapterImpl connAddapter = new ConnectionAdapterImpl(channel,inetSockAddr);
        connAddapter.setResponseCache(null);
        connAddapter.barrier(barrierInput);
        connAddapter.disconnect();
    }
    //TODO: to create test for next method:
    //echoReply
    //experimenter
    //flowMod
    //getConfig
    //getFeatures
    //getQueueConfig
    //groupMod
    //hello
    //meterMod
    //packetOut
    //multipartRequest
    //portMod
    //roleRequest
    //setConfig
    //tableMod
    //getAsync
    //setAsync
}
