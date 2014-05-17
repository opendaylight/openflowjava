/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.sal.common.util.RpcErrors;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionReadyListener;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Notification;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Handles messages (notifications + rpcs) and connections
 * @author mirehak
 * @author michal.polkorab
 */
public class ConnectionAdapterImpl implements ConnectionFacade {
    /** after this time, RPC future response objects will be thrown away (in minutes) */
    public static final int RPC_RESPONSE_EXPIRATION = 1;

    /**
     * Default depth of write queue, e.g. we allow these many messages
     * to be queued up before blocking producers.
     */
    public static final int DEFAULT_QUEUE_DEPTH = 1024;

    private static final Logger LOG = LoggerFactory
            .getLogger(ConnectionAdapterImpl.class);
    private static final Exception QUEUE_FULL_EXCEPTION =
            new RejectedExecutionException("Output queue is full");

    private static final String APPLICATION_TAG = "OPENFLOW_LIBRARY";
    private static final String TAG = "OPENFLOW";
    private static final RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>> REMOVAL_LISTENER =
            new RemovalListener<RpcResponseKey, ResponseExpectedRpcListener<?>>() {
        @Override
        public void onRemoval(
                final RemovalNotification<RpcResponseKey, ResponseExpectedRpcListener<?>> notification) {
            notification.getValue().discard();
        }
    };

    /** expiring cache for future rpcResponses */
    private final Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> responseCache;

    private final ChannelOutboundQueue output;
    private final Channel channel;

    private ConnectionReadyListener connectionReadyListener;
    private OpenflowProtocolListener messageListener;
    private SystemNotificationsListener systemListener;
    private boolean disconnectOccured = false;

    /**
     * default ctor
     * @param channel the channel to be set - used for communication
     */
    public ConnectionAdapterImpl(final SocketChannel channel) {
        responseCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterWrite(RPC_RESPONSE_EXPIRATION, TimeUnit.MINUTES)
                .removalListener(REMOVAL_LISTENER).build();
        this.channel = Preconditions.checkNotNull(channel);
        this.output = new ChannelOutboundQueue(channel, DEFAULT_QUEUE_DEPTH);
        channel.pipeline().addLast(output);
        LOG.debug("ConnectionAdapter created");
    }

    @Override
    public Future<RpcResult<BarrierOutput>> barrier(final BarrierInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, BarrierOutput.class, "barrier-input sending failed");
    }

    @Override
    public Future<RpcResult<EchoOutput>> echo(final EchoInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, EchoOutput.class, "echo-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> echoReply(final EchoReplyInput input) {
        return sendToSwitchFuture(input, "echo-reply sending failed");
    }

    @Override
    public Future<RpcResult<Void>> experimenter(final ExperimenterInput input) {
        return sendToSwitchFuture(input, "experimenter sending failed");
    }

    @Override
    public Future<RpcResult<Void>> flowMod(final FlowModInput input) {
        return sendToSwitchFuture(input, "flow-mod sending failed");
    }

    @Override
    public Future<RpcResult<GetConfigOutput>> getConfig(final GetConfigInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetConfigOutput.class, "get-config-input sending failed");
    }

    @Override
    public Future<RpcResult<GetFeaturesOutput>> getFeatures(
            final GetFeaturesInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetFeaturesOutput.class, "get-features-input sending failed");
    }

    @Override
    public Future<RpcResult<GetQueueConfigOutput>> getQueueConfig(
            final GetQueueConfigInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetQueueConfigOutput.class, "get-queue-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> groupMod(final GroupModInput input) {
        return sendToSwitchFuture(input, "group-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> hello(final HelloInput input) {
        return sendToSwitchFuture(input, "hello-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> meterMod(final MeterModInput input) {
        return sendToSwitchFuture(input, "meter-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> packetOut(final PacketOutInput input) {
        return sendToSwitchFuture(input, "packet-out-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> multipartRequest(final MultipartRequestInput input) {
        return sendToSwitchFuture(input, "multi-part-request sending failed");
    }

    @Override
    public Future<RpcResult<Void>> portMod(final PortModInput input) {
        return sendToSwitchFuture(input, "port-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<RoleRequestOutput>> roleRequest(
            final RoleRequestInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, RoleRequestOutput.class, "role-request-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> setConfig(final SetConfigInput input) {
        return sendToSwitchFuture(input, "set-config-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> tableMod(final TableModInput input) {
        return sendToSwitchFuture(input, "table-mod-input sending failed");
    }

    @Override
    public Future<RpcResult<GetAsyncOutput>> getAsync(final GetAsyncInput input) {
        return sendToSwitchExpectRpcResultFuture(
                input, GetAsyncOutput.class, "get-async-input sending failed");
    }

    @Override
    public Future<RpcResult<Void>> setAsync(final SetAsyncInput input) {
        return sendToSwitchFuture(input, "set-async-input sending failed");
    }

    @Override
    public Future<Boolean> disconnect() {
        ChannelFuture disconnectResult = channel.disconnect();
        responseCache.invalidateAll();
        disconnectOccured = true;

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
    public void setMessageListener(final OpenflowProtocolListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void consume(final DataObject message) {
        LOG.debug("ConsumeIntern msg");
        if (disconnectOccured ) {
            return;
        }
        if (message instanceof Notification) {
            // System events
            if (message instanceof DisconnectEvent) {
                systemListener.onDisconnectEvent((DisconnectEvent) message);
                responseCache.invalidateAll();
                disconnectOccured = true;
            } else if (message instanceof SwitchIdleEvent) {
                systemListener.onSwitchIdleEvent((SwitchIdleEvent) message);
            }
            // OpenFlow messages
              else if (message instanceof EchoRequestMessage) {
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
            } else if (message instanceof PacketInMessage) {
                messageListener.onPacketInMessage((PacketInMessage) message);
            } else if (message instanceof PortStatusMessage) {
                messageListener.onPortStatusMessage((PortStatusMessage) message);
            } else {
                LOG.warn("message listening not supported for type: {}", message.getClass());
            }
        } else {
            if (message instanceof OfHeader) {
                LOG.debug("OFheader msg received");
                RpcResponseKey key = createRpcResponseKey((OfHeader) message);
                final ResponseExpectedRpcListener<?> listener = findRpcResponse(key);
                if (listener != null) {
                    LOG.debug("corresponding rpcFuture found");
                    listener.completed((OfHeader)message);
                    LOG.debug("after setting rpcFuture");
                    responseCache.invalidate(key);
                } else {
                    LOG.warn("received unexpected rpc response: {}", key);
                }
            } else {
                LOG.warn("message listening not supported for type: {}", message.getClass());
            }
        }
    }

    private <T> ListenableFuture<RpcResult<T>> enqueueMessage(final AbstractRpcListener<T> promise) {
        LOG.debug("Submitting promise {}", promise);

        if (!output.enqueue(promise)) {
            LOG.debug("Message queue is full, rejecting execution");
            promise.failedRpc(QUEUE_FULL_EXCEPTION);
        } else {
            LOG.debug("Promise enqueued successfully");
        }

        return promise.getResult();
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
    private ListenableFuture<RpcResult<Void>> sendToSwitchFuture(
            final DataObject input, final String failureInfo) {
        return enqueueMessage(new SimpleRpcListener(input, failureInfo));
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
    private <IN extends OfHeader, OUT extends OfHeader> ListenableFuture<RpcResult<OUT>> sendToSwitchExpectRpcResultFuture(
            final IN input, final Class<OUT> responseClazz, final String failureInfo) {
        final RpcResponseKey key = new RpcResponseKey(input.getXid(), responseClazz.getName());
        final ResponseExpectedRpcListener<OUT> listener =
                new ResponseExpectedRpcListener<>(input, failureInfo, responseCache, key);
        return enqueueMessage(listener);
    }

    /**
     * @param resultFuture
     * @param failureInfo
     * @param errorSeverity
     * @param message
     * @return
     */
    private static SettableFuture<Boolean> handleTransportChannelFuture(
            final ChannelFuture resultFuture, final String failureInfo,
            final ErrorSeverity errorSeverity, final String message) {

        final SettableFuture<Boolean> transportResult = SettableFuture.create();

        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {

            @Override
            public void operationComplete(
                    final io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                transportResult.set(future.isSuccess());
                if (!future.isSuccess()) {
                    transportResult.setException(future.cause());
                }
            }
        });
        return transportResult;
    }

    /**
     * @param cause
     * @return
     */
    static RpcError buildRpcError(final String info, final ErrorSeverity severity, final String message,
            final Throwable cause) {
        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message,
                ErrorType.RPC, cause);
        return error;
    }

    /**
     * @param cause
     * @return
     */
    protected static RpcError buildTransportError(final String info, final ErrorSeverity severity, final String message,
            final Throwable cause) {
        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message,
                ErrorType.TRANSPORT, cause);
        return error;
    }

    /**
     * @param message
     * @return
     */
    private static RpcResponseKey createRpcResponseKey(final OfHeader message) {
        return new RpcResponseKey(message.getXid(), message.getImplementedInterface().getName());
    }

    /**
     * @return
     */
    private ResponseExpectedRpcListener<?> findRpcResponse(final RpcResponseKey key) {
        return responseCache.getIfPresent(key);
    }

    @Override
    public void setSystemListener(final SystemNotificationsListener systemListener) {
        this.systemListener = systemListener;
    }

    @Override
    public void checkListeners() {
        final StringBuilder buffer =  new StringBuilder();
        if (systemListener == null) {
            buffer.append("SystemListener ");
        }
        if (messageListener == null) {
            buffer.append("MessageListener ");
        }
        if (connectionReadyListener == null) {
            buffer.append("ConnectionReadyListener ");
        }

        Preconditions.checkState(buffer.length() == 0, "Missing listeners: %s", buffer.toString());
    }

    @Override
    public void fireConnectionReadyNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectionReadyListener.onConnectionReady();
            }
        }).start();
    }


    @Override
    public void setConnectionReadyListener(
            final ConnectionReadyListener connectionReadyListener) {
        this.connectionReadyListener = connectionReadyListener;
    }
}
