/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionReadyListener;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandler;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandlerRegistration;
import org.opendaylight.openflowjava.protocol.impl.core.OFVersionDetector;
import org.opendaylight.openflowjava.protocol.impl.core.PipelineHandlers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Notification;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles messages (notifications + rpcs) and connections
 * @author mirehak
 * @author michal.polkorab
 */
public class ConnectionAdapterImpl extends AbstractConnectionAdapterStatistics implements ConnectionFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionAdapterImpl.class);

    private static final Exception QUEUE_FULL_EXCEPTION =
            new RejectedExecutionException("Output queue is full");

    private final ChannelOutboundQueue output;

    private ConnectionReadyListener connectionReadyListener;
    private OpenflowProtocolListener messageListener;
    private SystemNotificationsListener systemListener;
    private OutboundQueueManager<?> outputManager;
    private boolean disconnectOccured = false;
    private OFVersionDetector versionDetector;

    private final boolean useBarrier;

    /**
     * default ctor
     * 
     * @param channel the channel to be set - used for communication
     * @param address client address (used only in case of UDP communication,
     *            as there is no need to store address over tcp (stable channel))
     * @param useBarrier value is configurable by configSubsytem
     */
    public ConnectionAdapterImpl(final Channel channel, final InetSocketAddress address, final boolean useBarrier) {
        super(channel, address);
        this.output = new ChannelOutboundQueue(channel, DEFAULT_QUEUE_DEPTH, address);

        this.useBarrier = useBarrier;
        channel.pipeline().addLast(output);

        LOG.debug("ConnectionAdapter created");
    }

    @Override
    public Future<Boolean> disconnect() {
        final ChannelFuture disconnectResult = channel.disconnect();
        responseCache.invalidateAll();
        disconnectOccured = true;

        return handleTransportChannelFuture(disconnectResult);
    }

    @Override
    public void setMessageListener(final OpenflowProtocolListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void consumeDeviceMessage(final DataObject message) {
        LOG.debug("ConsumeIntern msg on {}", channel);
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
                // OpenFlow messages
            } else if (message instanceof EchoRequestMessage) {
                if (outputManager != null) {
                    outputManager.onEchoRequest((EchoRequestMessage) message);
                } else {
                    messageListener.onEchoRequestMessage((EchoRequestMessage) message);
                }
            } else if (message instanceof ErrorMessage) {
                // Send only unmatched errors
                if (outputManager == null || !outputManager.onMessage((OfHeader) message)) {
                    messageListener.onErrorMessage((ErrorMessage) message);
                }
            } else if (message instanceof ExperimenterMessage) {
                if (outputManager != null) {
                    outputManager.onMessage((OfHeader) message);
                }
                messageListener.onExperimenterMessage((ExperimenterMessage) message);
            } else if (message instanceof FlowRemovedMessage) {
                messageListener.onFlowRemovedMessage((FlowRemovedMessage) message);
            } else if (message instanceof HelloMessage) {
                LOG.info("Hello received / branch");
                messageListener.onHelloMessage((HelloMessage) message);
            } else if (message instanceof MultipartReplyMessage) {
                if (outputManager != null) {
                    outputManager.onMessage((OfHeader) message);
                }
                messageListener.onMultipartReplyMessage((MultipartReplyMessage) message);
            } else if (message instanceof PacketInMessage) {
                messageListener.onPacketInMessage((PacketInMessage) message);
            } else if (message instanceof PortStatusMessage) {
                messageListener.onPortStatusMessage((PortStatusMessage) message);
            } else {
                LOG.warn("message listening not supported for type: {}", message.getClass());
            }
        } else if (message instanceof OfHeader) {
            LOG.debug("OFheader msg received");

            if (outputManager == null || !outputManager.onMessage((OfHeader) message)) {
                final RpcResponseKey key = createRpcResponseKey((OfHeader) message);
                final ResponseExpectedRpcListener<?> listener = findRpcResponse(key);
                if (listener != null) {
                    LOG.debug("corresponding rpcFuture found");
                    listener.completed((OfHeader)message);
                    LOG.debug("after setting rpcFuture");
                    responseCache.invalidate(key);
                } else {
                    LOG.warn("received unexpected rpc response: {}", key);
                }
            }
        } else {
            LOG.warn("message listening not supported for type: {}", message.getClass());
        }
    }

    @Override
    protected <T> ListenableFuture<RpcResult<T>> enqueueMessage(final AbstractRpcListener<T> promise) {
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
     * @param resultFuture
     * @param failureInfo
     * @param errorSeverity
     * @param message
     * @return
     */
    private static SettableFuture<Boolean> handleTransportChannelFuture(
            final ChannelFuture resultFuture) {

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
        versionDetector = (OFVersionDetector) channel.pipeline().get(PipelineHandlers.OF_VERSION_DETECTOR.name());
        Preconditions.checkState(versionDetector != null);

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

    /**
     * Used only for testing purposes
     * @param cache
     */
    public void setResponseCache(final Cache<RpcResponseKey, ResponseExpectedRpcListener<?>> cache) {
        this.responseCache = cache;
    }

    @Override
    public <T extends OutboundQueueHandler> OutboundQueueHandlerRegistration<T> registerOutboundQueueHandler(
            final T handler, final int maxQueueDepth, final long maxBarrierNanos) {
        Preconditions.checkState(outputManager == null, "Manager %s already registered", outputManager);

        if (useBarrier) {

        }

        final OutboundQueueManager<T> ret = new OutboundQueueManager<>(this, address, handler, maxQueueDepth, maxBarrierNanos);
        outputManager = ret;
        channel.pipeline().addLast(outputManager);

        return new OutboundQueueHandlerRegistrationImpl<T>(handler) {
            @Override
            protected void removeRegistration() {
                outputManager.close();
                channel.pipeline().remove(outputManager);
                outputManager = null;
            }
        };
    }

    Channel getChannel() {
        return channel;
    }

    @Override
    public void setPacketInFiltering(final boolean enabled) {
        versionDetector.setFilterPacketIns(enabled);
        LOG.debug("PacketIn filtering {}abled", enabled ? "en" : "dis");
    }
}
