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

import org.opendaylight.controller.sal.common.util.Rpcs;
import org.opendaylight.openflowjava.protocol.api.connection.ConnectionAdapter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author mirehak
 *
 */
public class ConnectionAdapterImpl implements ConnectionAdapter {
    
    private static final String APPLICATION_TAG = "OPENFLOW_LIBRARY";
    private static final String TAG = "OPENFLOW";
    private Channel channel;
    private OpenflowProtocolListener messageListener;
    
    /**
     * @param channel the channel to set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Future<RpcResult<BarrierOutput>> barrier(BarrierInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<EchoOutput>> echo(EchoInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> echoReply(EchoReplyInput input) {
        return sendToSwitchFuture(input, "echo reply sending failed");
    }

    @Override
    public Future<RpcResult<Void>> experimenter(ExperimenterInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> flowMod(FlowModInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<GetConfigOutput>> getConfig(GetConfigInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<GetFeaturesOutput>> getFeatures(
            GetFeaturesInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<GetQueueConfigOutput>> getQueueConfig(
            GetQueueConfigInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> groupMod(GroupModInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> hello(HelloInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> meterMod(MeterModInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> packetOut(PacketOutInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> portMod(PortModInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<RoleRequestOutput>> roleRequest(
            RoleRequestInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> setConfig(SetConfigInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<RpcResult<Void>> tableMod(TableModInput input) {
        // TODO Auto-generated method stub
        return null;
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

    /**
     * @param input
     * @return
     */
    private SettableFuture<RpcResult<Void>> sendToSwitchFuture(
            EchoReplyInput input, final String failureInfo) {
        ChannelFuture resultFuture = channel.writeAndFlush(input);
        
        ErrorSeverity errorSeverity = ErrorSeverity.ERROR;
        String message = "check switch connection";
        return handleRpcChannelFuture(resultFuture, failureInfo, errorSeverity, message);
    }

    /**
     * @param resultFuture
     * @param failureInfo
     * @return
     */
    private SettableFuture<RpcResult<Void>> handleRpcChannelFuture(
            ChannelFuture resultFuture, final String failureInfo, 
            final ErrorSeverity errorSeverity, final String message) {
        
        final SettableFuture<RpcResult<Void>> rpcResult = SettableFuture.create();
        
        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            
            @Override
            public void operationComplete(
                    io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                Collection<RpcError> errors = null;
                
                if (future.cause() != null) {
                    RpcError rpcError = buildRpcError(failureInfo, 
                            errorSeverity, message, future.cause());
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
     * @param resultFuture
     * @param failureInfo
     * @return
     */
    private SettableFuture<Boolean> handleTransportChannelFuture(
            ChannelFuture resultFuture, final String failureInfo, 
            final ErrorSeverity errorSeverity, final String message) {
        
        final SettableFuture<Boolean> transportResult = SettableFuture.create();
        
        resultFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            
            @Override
            public void operationComplete(
                    io.netty.util.concurrent.Future<? super Void> future)
                    throws Exception {
                transportResult.set(future.isSuccess());
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
        // TODO - uncomment, when sal-common-util merged
//        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message, 
//                ErrorType.RPC, cause);
//        return error;
        
        return null;
    }
    
    /**
     * @param cause
     * @return
     */
    protected RpcError buildTransportError(String info, ErrorSeverity severity, String message, 
            Throwable cause) {
        // TODO - uncomment, when sal-common-util merged
//        RpcError error = RpcErrors.getRpcError(APPLICATION_TAG, TAG, info, severity, message, 
//                ErrorType.TRANSPORT, cause);
//        return error;
        
        return null;
    }

}
