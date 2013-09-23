/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.sal.common.util.RpcErrors;
import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Notification;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author mirehak
 * @author michal.polkorab
 */
public class ConnectionAdapterImpl implements ConnectionFacade {
    
    /** after this time, rpc future response objects will be thrown away (in minutes) */
    public static final int RPC_RESPONSE_EXPIRATION = 1;

    protected static final Logger LOG = LoggerFactory
            .getLogger(ConnectionAdapterImpl.class);
    
    private static final String APPLICATION_TAG = "OPENFLOW_LIBRARY";
    private static final String TAG = "OPENFLOW";
    private Channel channel;
    private OpenflowProtocolListener messageListener;
    /** expiring cache for future rpcResponses */
    protected Cache<RpcResponseKey, SettableFuture<?>> responseCache;
    
    
    /**
     * default ctor 
     */
    public ConnectionAdapterImpl() {
        responseCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterWrite(RPC_RESPONSE_EXPIRATION, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<RpcResponseKey, SettableFuture<?>>() {

                    @Override
                    public void onRemoval(
                            RemovalNotification<RpcResponseKey, SettableFuture<?>> notification) {
                        LOG.warn("rpc response discarded: "+notification.getKey());
                        notification.getValue().cancel(true);
                    }
                }).build();
        LOG.info("ConnectionAdapter created");
    }
    
    /**
     * @param channel the channel to be set - used for communication
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Future<RpcResult<BarrierOutput>> barrier(BarrierInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, BarrierOutput.class, "barrier-input sending failed");
    }

    @Override
    public Future<RpcResult<EchoOutput>> echo(EchoInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, EchoOutput.class, "echo-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> echoReply(EchoReplyInput input) {
        return sendToSwitchFuture(input, "echo-reply sending failed");
    }

    @Override
    public Future<RpcResult<Void>> experimenter(ExperimenterInput input) {
        return sendToSwitchFuture(input, "experimenter sending failed");
    }

    @Override
    public Future<RpcResult<Void>> flowMod(FlowModInput input) {
        return sendToSwitchFuture(input, "flow-mod sending failed");
    }

    @Override
    public Future<RpcResult<GetConfigOutput>> getConfig(GetConfigInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetConfigOutput.class, "get-config-input sending failed");
    }

    @Override
    public Future<RpcResult<GetFeaturesOutput>> getFeatures(
            GetFeaturesInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetFeaturesOutput.class, "get-features-input sending failed");
    }

    @Override
    public Future<RpcResult<GetQueueConfigOutput>> getQueueConfig(
            GetQueueConfigInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetQueueConfigOutput.class, "get-queue-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> groupMod(GroupModInput input) {
        return sendToSwitchFuture(input, "group-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> hello(HelloInput input) {
        return sendToSwitchFuture(input, "hello-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> meterMod(MeterModInput input) {
        return sendToSwitchFuture(input, "meter-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> packetOut(PacketOutInput input) {
        return sendToSwitchFuture(input, "packet-out-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> portMod(PortModInput input) {
        return sendToSwitchFuture(input, "port-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<RoleRequestOutput>> roleRequest(
            RoleRequestInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, RoleRequestOutput.class, "role-request-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> setConfig(SetConfigInput input) {
        return sendToSwitchFuture(input, "set-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> tableMod(TableModInput input) {
        return sendToSwitchFuture(input, "table-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<GetAsyncOutput>> getAsync(GetAsyncInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetAsyncOutput.class, "get-async-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> setAsync(SetAsyncInput input) {
        return sendToSwitchFuture(input, "set-async-input sending failed");
    }

    @Override
    public Future<Boolean> disconnect() {
        ChannelFuture disconnectResult = channel.disconnect();
        
        String failureInfo = "switch disconnecting failed";
        ErrorSeverity errorSeverity = ErrorSeverity.ERROR;
        String message = "Check the switch connection";
        return handleTransportChannelFuture(disconnectResult, failureInfo, errorSeverity, message);
    }

    @Override
    public boolean isAlive() {
        return channel.isOpen();
    }

    @Override
    public void setMessageListener(OpenflowProtocolListener messageListener) {
        this.messageListener = messageListener;
    }
    
    @Override
    public void consume(DataObject message) {
        if (message instanceof Notification) {
            if (message instanceof EchoRequestMessage) {
                messageListener.onEchoRequestMessage((EchoRequestMessage) message);
            } else if (message instanceof ErrorMessage) {
                messageListener.onErrorMessage((ErrorMessage) message);
            } else if (message instanceof ExperimenterMessage) {
                messageListener.onExperimenterMessage((ExperimenterMessage) message);
            } else if (message instanceof FlowRemovedMessage) {
                messageListener.onFlowRemovedMessage((FlowRemovedMessage) message);
            } else if (message instanceof HelloMessage) {
                LOG.info("Hello received / branch");
                messageListener.onHelloMessage((HelloMessage) message);
            } else if (message instanceof MultipartReplyMessage) {
                messageListener.onMultipartReplyMessage((MultipartReplyMessage) message);
            } else if (message instanceof MultipartRequestMessage) {
                messageListener.onMultipartRequestMessage((MultipartRequestMessage) message);
            } else if (message instanceof PacketInMessage) {
                messageListener.onPacketInMessage((PacketInMessage) message);
            } else if (message instanceof PortStatusMessage) {
                messageListener.onPortStatusMessage((PortStatusMessage) message);
            } else {
                LOG.warn("message listening not supported for type: "+message.getClass());
            }
        } else {
            if (message instanceof OfHeader) {
                RpcResponseKey key = createRpcResponseKey((OfHeader) message);
                SettableFuture<RpcResult<?>> rpcFuture = findRpcResponse(key);
                if (rpcFuture != null) {
                    rpcFuture.set(Rpcs.getRpcResult(true, message, null));
                    responseCache.invalidate(key);
                } else {
                    LOG.warn("received unexpected rpc response: "+key);
                }
                
            } else {
                LOG.warn("message listening not supported for type: "+message.getClass());
            }
        }
    }

    /**
     * sends given message to switch, sending result will be reported via return value
     * @param input message to send
     * @param failureInfo describes, what type of message caused failure by sending 
     * @return future object, <ul>
     *  <li>if send successful, {@link RpcResult} without errors and successful 
     *  status will be returned, </li>
     *  <li>else {@link RpcResult} will contain errors and failed status</li>
     *  </ul>    
     */
    private SettableFuture<RpcResult<Void>> sendToSwitchFuture(
            DataObject input, final String failureInfo) {
        ChannelFuture resultFuture = channel.writeAndFlush(input);
        
        ErrorSeverity errorSeverity = ErrorSeverity.ERROR;
        String errorMessage = "check switch connection";
        return handleRpcChannelFuture(resultFuture, failureInfo, errorSeverity, errorMessage);
    }
    
    /**
     * sends given message to switch, sending result or switch response will be reported via return value
     * @param input message to send
     * @param responseClazz type of response
     * @param failureInfo describes, what type of message caused failure by sending 
     * @return future object, <ul>
     *  <li>if send fails, {@link RpcResult} will contain errors and failed status </li>
     *  <li>else {@link RpcResult} will be stored in responseCache and wait for particular timeout 
     *  ({@link ConnectionAdapterImpl#RPC_RESPONSE_EXPIRATION}), 
     *  <ul><li>either switch will manage to answer
     *  and then corresponding response message will be set into returned future</li>
     *  <li>or response in cache will expire and returned future will be cancelled</li></ul>
     *  </li>
     *  </ul>     
     */
    private <IN extends OfHeader, OUT extends OfHeader> SettableFuture<RpcResult<OUT>> sendToSwitchExpectRpcResultFuture(
            IN input, Class<OUT> responseClazz, final String failureInfo) {
        ChannelFuture resultFuture = channel.writeAndFlush(input);
        
        ErrorSeverity errorSeverity = ErrorSeverity.ERROR;
        String errorMessage = "check switch connection";
        return handleRpcChannelFutureWithResponse(resultFuture, failureInfo, errorSeverity, 
                errorMessage, input, responseClazz);
    }

    /**
     * @param resultFuture
     * @param failureInfo
     * @return
     */
    private SettableFuture<RpcResult<Void>> handleRpcChannelFuture(
            ChannelFuture resultFuture, final String failureInfo, 
            final ErrorSeverity errorSeverity, final String errorMessage) {
        
        final SettableFuture<RpcResult<Void>> rpcResult = SettableFuture.create();
        
        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            
            @Override
            public void operationComplete(
                    io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                Collection<RpcError> errors = null;
                
                if (future.cause() != null) {
                    RpcError rpcError = buildRpcError(failureInfo, 
                            errorSeverity, errorMessage, future.cause());
                    errors = Lists.newArrayList(rpcError);
                }
                
                rpcResult.set(Rpcs.getRpcResult(
                        future.isSuccess(), 
                        (Void) null, 
                        errors)
                );
            }
        });
        return rpcResult;
    }
    
    /**
     * @param input
     * @param responseClazz
     * @param resultFuture
     * @param failureInfo
     * @param errorSeverity
     * @param errorMessage
     * @return
     */
    private <IN extends OfHeader, OUT extends OfHeader> SettableFuture<RpcResult<OUT>> handleRpcChannelFutureWithResponse(
            ChannelFuture resultFuture, final String failureInfo,
            final ErrorSeverity errorSeverity, final String errorMessage,
            final IN input, Class<OUT> responseClazz) {
        final SettableFuture<RpcResult<OUT>> rpcResult = SettableFuture.create();
        
        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            
            @Override
            public void operationComplete(
                    io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                
                if (future.cause() != null) {
                    Collection<RpcError> errors = null;
                    RpcError rpcError = buildRpcError(failureInfo, 
                            errorSeverity, errorMessage, future.cause());
                    errors = Lists.newArrayList(rpcError);
                    rpcResult.set(Rpcs.getRpcResult(
                            future.isSuccess(), 
                            (OUT) null, 
                            errors)
                            );
                } else {
                    RpcResponseKey key = new RpcResponseKey(input.getXid(), input.getClass().toString());
                    if (responseCache.getIfPresent(key) != null) {
                        responseCache.invalidate(key);
                    }
                    responseCache.put(key, rpcResult);
                }
            }
        });
        return rpcResult;
    }

    /**
     * @param resultFuture
     * @param failureInfo
     * @param errorSeverity 
     * @param message 
     * @return
     */
    private static SettableFuture<Boolean> handleTransportChannelFuture(
            ChannelFuture resultFuture, final String failureInfo, 
            final ErrorSeverity errorSeverity, final String message) {
        
        final SettableFuture<Boolean> transportResult = SettableFuture.create();
        
        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            
            @Override
            public void operationComplete(
                    io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                transportResult.set(future.isSuccess());
                transportResult.setException(future.cause());
            }
        });
        return transportResult;
    }

    /**
     * @param cause
     * @return
     */
    protected RpcError buildRpcError(String info, ErrorSeverity severity, String message, 
            Throwable cause) {
        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message, 
                ErrorType.RPC, cause);
        return error;
    }
    
    /**
     * @param cause
     * @return
     */
    protected RpcError buildTransportError(String info, ErrorSeverity severity, String message, 
            Throwable cause) {
        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message, 
                ErrorType.TRANSPORT, cause);
        return error;
    }

    /**
     * @param message
     * @return
     */
    private static RpcResponseKey createRpcResponseKey(OfHeader message) {
        return new RpcResponseKey(message.getXid(), message.getClass().toString());
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private SettableFuture<RpcResult<?>> findRpcResponse(RpcResponseKey key) {
        return (SettableFuture<RpcResult<?>>) responseCache.getIfPresent(key);
    }

}
