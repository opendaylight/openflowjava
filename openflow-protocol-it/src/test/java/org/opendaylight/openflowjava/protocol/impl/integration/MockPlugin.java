/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionAdapter;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEvent;
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
    private int idleCounter = 0;

    /** Creates MockPlugin */
    public MockPlugin() {
        LOGGER.info("Creating MockPlugin");
        finishedFuture = SettableFuture.create();
        LOGGER.info("mockPlugin: "+System.identityHashCode(this));
    }
    
    @Override
    public void onSwitchConnected(ConnectionAdapter connection) {
        LOGGER.info("onSwitchConnected: " + connection);
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
        replyBuilder.setVersion((short) 4);
        replyBuilder.setXid(notification.getXid());
        EchoReplyInput echoReplyInput = replyBuilder.build();
        LOGGER.debug("EchoReplyInput built");
        LOGGER.debug("Going to send EchoReplyInput");
        adapter.echoReply(echoReplyInput);
        LOGGER.debug("EchoReplyInput sent");
        LOGGER.debug("adapter: "+adapter);
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
        LOGGER.debug("adapter: "+adapter);
        LOGGER.debug("Hello message received");
        HelloInputBuilder hib = new HelloInputBuilder();
        hib.setVersion((short) 4);
        hib.setXid(2L);
        HelloInput hi = hib.build();
        adapter.hello(hi);
        LOGGER.debug("hello msg sent");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendHelloMessage();
            }
        }).start();
        LOGGER.debug("adapter: "+adapter);
    }
    
    protected void sendHelloMessage() {
        GetFeaturesInputBuilder featuresBuilder = new GetFeaturesInputBuilder();
        featuresBuilder.setVersion((short) 4);
        featuresBuilder.setXid(3L);
        GetFeaturesInput featuresInput = featuresBuilder.build();
        try {
            LOGGER.debug("Going to send featuresRequest");
            RpcResult<GetFeaturesOutput> rpcResult = adapter.getFeatures(
                    featuresInput).get(2500, TimeUnit.MILLISECONDS);
            if (rpcResult.isSuccessful()) {
                byte[] byteArray = rpcResult.getResult().getDatapathId()
                        .toByteArray();
                LOGGER.info("DatapathId: " + Arrays.toString(byteArray));
            } else {
                RpcError rpcError = rpcResult.getErrors().iterator().next();
                LOGGER.warn("rpcResult failed: "
                        + rpcError.getCause().getMessage(), rpcError.getCause());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("After FeaturesReply message");
    }

    protected void shutdown() {
        LOGGER.debug("adapter: "+adapter);
        try {
            LOGGER.info("mockPlugin: "+System.identityHashCode(this));
            Thread.sleep(500);
            if (adapter != null) {
                Future<Boolean> disconnect = adapter.disconnect();
                disconnect.get();
                LOGGER.info("Disconnected");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
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
        LOGGER.debug("adapter: "+adapter);
    }

    /**
     * @return finishedFuture object
     */
    public SettableFuture<Void> getFinishedFuture() {
        return finishedFuture;
    }

    @Override
    public void onSwitchIdleEvent(SwitchIdleEvent notification) {
        LOGGER.debug("switch status: "+notification.getInfo());
        idleCounter ++;
    }

    /**
     * @return number of occured idleEvents
     */
    public int getIdleCounter() {
        return idleCounter;
    }


}
