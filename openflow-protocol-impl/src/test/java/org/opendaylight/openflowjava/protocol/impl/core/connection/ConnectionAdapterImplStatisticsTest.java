/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionReadyListener;
import org.opendaylight.openflowjava.statistics.CounterEventTypes;
import org.opendaylight.openflowjava.statistics.StatisticsCounters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEventBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEventBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @author madamjak
 *
 */
public class ConnectionAdapterImplStatisticsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounters.class);
    private static final int RPC_RESPONSE_EXPIRATION = 1;
    private static final RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>> REMOVAL_LISTENER =
            new RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>>() {
        @Override
        public void onRemoval(
                final RemovalNotification<RpcResponseKey, ResponseExpectedRpcListener<?>> notification) {
            notification.getValue().discard();
        }
    };
    
    @Mock SystemNotificationsListener systemListener;
    @Mock ConnectionReadyListener readyListener;
    //@Mock Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> mockCache;
    @Mock ChannelFuture channelFuture;
    @Mock OpenflowProtocolListener messageListener;
    @Mock SocketChannel channel;
    @Mock ChannelPipeline pipeline;
    @Mock EchoInput echoInput;
    @Mock BarrierInput barrierInput;
    @Mock EchoReplyInput echoReplyInput;
    @Mock ExperimenterInput experimenterInput;
    @Mock FlowModInput flowModInput;
    @Mock GetConfigInput getConfigInput;
    @Mock GetFeaturesInput getFeaturesInput;
    @Mock GetQueueConfigInput getQueueConfigInput;
    @Mock GroupModInput groupModInput;
    @Mock HelloInput helloInput;
    @Mock MeterModInput meterModInput;
    @Mock PacketOutInput packetOutInput;
    @Mock MultipartRequestInput multipartRequestInput;
    @Mock PortModInput portModInput;
    @Mock RoleRequestInput roleRequestInput;
    @Mock SetConfigInput setConfigInput;
    @Mock TableModInput tableModInput;
    @Mock GetAsyncInput getAsyncInput;
    @Mock SetAsyncInput setAsyncInput;
    
    private ConnectionAdapterImpl adapter;
    private Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> cache;
    private StatisticsCounters statCounters;
    /**
     * Initialize mocks
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statCounters = StatisticsCounters.getInstance();
    }
    /**
     * Disconnect adapter
     */
    @After
    public void tierDown(){
        if (adapter != null && adapter.isAlive()) {
            adapter.disconnect();
        }
        statCounters.resetCounters();
    }
    /**
     * Test statistic counter for all rpc calls 
     * @throws InterruptedException 
     */
    @Test
    public void testEnterOFJavaCounter() throws InterruptedException {
        EmbeddedChannel embChannel = new EmbeddedChannel(new EmbededChannelHandler());
        adapter = new ConnectionAdapterImpl(embChannel,InetSocketAddress.createUnresolved("localhost", 9876));
        cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(RPC_RESPONSE_EXPIRATION, TimeUnit.MINUTES)
                .removalListener(REMOVAL_LISTENER).build();
        adapter.setResponseCache(cache);
        // -- barrier
        adapter.barrier(barrierInput);
        embChannel.runPendingTasks();
        // -- echo
        adapter.echo(echoInput);
        embChannel.runPendingTasks();
        // -- echoReply
        adapter.echoReply(echoReplyInput);
        embChannel.runPendingTasks();
        // -- experimenter
        adapter.experimenter(experimenterInput);
        embChannel.runPendingTasks();
        // -- flowMod
        adapter.flowMod(flowModInput);
        embChannel.runPendingTasks();
        // -- getConfig
        adapter.getConfig(getConfigInput);
        embChannel.runPendingTasks();
        // -- getFeatures
        adapter.getFeatures(getFeaturesInput);
        embChannel.runPendingTasks();
        // -- getQueueConfig
        adapter.getQueueConfig(getQueueConfigInput);
        embChannel.runPendingTasks();
        // -- groupMod
        adapter.groupMod(groupModInput);
        embChannel.runPendingTasks();
        // -- hello
        adapter.hello(helloInput);
        embChannel.runPendingTasks();
        // -- meterMod
        adapter.meterMod(meterModInput);
        embChannel.runPendingTasks();
        // -- packetOut
        adapter.packetOut(packetOutInput);
        embChannel.runPendingTasks();
        // -- multipartRequest
        adapter.multipartRequest(multipartRequestInput);
        embChannel.runPendingTasks();
        // -- portMod
        adapter.portMod(portModInput);
        embChannel.runPendingTasks();
        // -- roleRequest
        adapter.roleRequest(roleRequestInput);
        embChannel.runPendingTasks();
        // -- setConfig
        adapter.setConfig(setConfigInput);
        embChannel.runPendingTasks();
        // -- tableMod
        adapter.tableMod(tableModInput);
        embChannel.runPendingTasks();
        // -- getAsync
        adapter.getAsync(getAsyncInput);
        embChannel.runPendingTasks();
        // -- setAsync
        adapter.setAsync(setAsyncInput);
        embChannel.runPendingTasks();
        LOGGER.debug("Waiting to Event Queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY + 100);
        Assert.assertEquals("Wrong - bad counter value for ConnectionAdapterImpl rpc methods", 19, statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue());
        adapter.disconnect();
    }

    @Test
    public void testMessagePassCounter() throws InterruptedException {
        when(channel.pipeline()).thenReturn(pipeline);
        adapter = new ConnectionAdapterImpl(channel, InetSocketAddress.createUnresolved("10.0.0.1", 6653));
        adapter.setMessageListener(messageListener);
        adapter.setSystemListener(systemListener);
        adapter.setConnectionReadyListener(readyListener);
        cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(RPC_RESPONSE_EXPIRATION, TimeUnit.MINUTES)
                .removalListener(REMOVAL_LISTENER).build();
        adapter.setResponseCache(cache);
        when(channel.disconnect()).thenReturn(channelFuture);
        DataObject message = new EchoRequestMessageBuilder().build();
        adapter.consume(message);
        message = new ErrorMessageBuilder().build();
        adapter.consume(message);
        message = new ExperimenterMessageBuilder().build();
        adapter.consume(message);
        message = new FlowRemovedMessageBuilder().build();
        adapter.consume(message);
        message = new HelloMessageBuilder().build();
        adapter.consume(message);
        message = new MultipartReplyMessageBuilder().build();
        adapter.consume(message);
        message = new PacketInMessageBuilder().build();
        adapter.consume(message);
        message = new PortStatusMessageBuilder().build();
        adapter.consume(message);
        message = new EchoRequestMessageBuilder().build();
        adapter.consume(message);
        LOGGER.debug("Waiting to Event Queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY + 100);
        Assert.assertEquals("Wrong - bad counter value for ConnectionAdapterImpl consume method", 9, statCounters.getCounter(CounterEventTypes.US_MESSAGE_PASS).getCounterValue());
        adapter.disconnect();
    }
    
    /**
     * Channel Handler for testing
     * @author madamjak
     *
     */
    private class EmbededChannelHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg,
                ChannelPromise promise) throws Exception {
            OfHeader responseOfCall = null;
            if(msg instanceof MessageListenerWrapper){
                MessageListenerWrapper listener = (MessageListenerWrapper) msg;
                OfHeader ofHeader = listener.getMsg();
                responseOfCall = ofHeader;
            }
        }
    }
}
