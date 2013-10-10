/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionAdapter;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * @author michal.polkorab
 *
 */
public class MockPlugin implements OpenflowProtocolListener, SwitchConnectionHandler, SystemNotificationsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockPlugin.class);
    private ConnectionAdapter adapter;
    private SettableFuture<Void> finishedFuture;
    
    public MockPlugin() {
        finishedFuture = SettableFuture.create();
    }
    
    @Override
    public void onSwitchConnected(ConnectionAdapter connection) {
        LOGGER.debug("onSwitchConnected");
        this.adapter = connection;
        connection.setMessageListener(this);
        connection.setSystemListener(this);
    }

    @Override
    public boolean accept(InetAddress switchAddress) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onEchoRequestMessage(EchoRequestMessage notification) {
        LOGGER.debug("EchoRequest message received");
        LOGGER.debug("Building EchoReplyInput");
        EchoReplyInputBuilder replyBuilder = new EchoReplyInputBuilder();
        try {
            BufferHelper.setupHeader(replyBuilder);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        replyBuilder.setXid(notification.getXid());
        EchoReplyInput echoReplyInput = replyBuilder.build();
        LOGGER.debug("EchoReplyInput built");
        LOGGER.debug("Going to send EchoReplyInput");
        adapter.echoReply(echoReplyInput);
        LOGGER.debug("EchoReplyInput sent");
    }

    @Override
    public void onErrorMessage(ErrorMessage notification) {
        LOGGER.debug("Error message received");
        
    }

    @Override
    public void onExperimenterMessage(ExperimenterMessage notification) {
        LOGGER.debug("Experimenter message received");
        
    }

    @Override
    public void onFlowRemovedMessage(FlowRemovedMessage notification) {
        LOGGER.debug("FlowRemoved message received");
        
    }

    @Override
    public void onHelloMessage(HelloMessage notification) {
        LOGGER.debug("Hello message received");
        HelloInputBuilder hib = new HelloInputBuilder();
        GetFeaturesInputBuilder featuresBuilder = new GetFeaturesInputBuilder();
        try {
            BufferHelper.setupHeader(hib);
            BufferHelper.setupHeader(featuresBuilder);
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }
        HelloInput hi = hib.build();
        adapter.hello(hi);
        LOGGER.debug("hello msg sent");
        GetFeaturesInput featuresInput = featuresBuilder.build();
        try {
            LOGGER.debug("Going to send featuresRequest");
            RpcResult<GetFeaturesOutput> rpcResult = adapter.getFeatures(
                    featuresInput).get(2500, TimeUnit.MILLISECONDS);
            if (rpcResult.isSuccessful()) {
                byte[] byteArray = rpcResult.getResult().getDatapathId()
                        .toByteArray();
                LOGGER.debug("DatapathId: " + Arrays.toString(byteArray));
            } else {
                RpcError rpcError = rpcResult.getErrors().iterator().next();
                LOGGER.warn("rpcResult failed: "
                        + rpcError.getCause().getMessage(), rpcError.getCause());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("After FeaturesReply message - disconnecting");
        adapter.disconnect();
        finishedFuture.set(null);
    }

    @Override
    public void onMultipartReplyMessage(MultipartReplyMessage notification) {
        LOGGER.debug("MultipartReply message received");
        
    }

    @Override
    public void onMultipartRequestMessage(MultipartRequestMessage notification) {
        LOGGER.debug("MultipartRequest message received");
        
    }

    @Override
    public void onPacketInMessage(PacketInMessage notification) {
        LOGGER.debug("PacketIn message received");
        
    }

    @Override
    public void onPortStatusMessage(PortStatusMessage notification) {
        LOGGER.debug("PortStatus message received");
        
    }

    @Override
    public void onDisconnectEvent(DisconnectEvent notification) {
        LOGGER.debug("disconnection ocured: "+notification.getInfo());
    }

    public SettableFuture<Void> getFinishedFuture() {
        return finishedFuture;
    }


}
