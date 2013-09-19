/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.opendaylight.openflowjava.protocol.impl.connection.CommunicationFacade;
import org.opendaylight.openflowjava.protocol.impl.connection.ConnectionAdapterImpl;
import org.opendaylight.openflowjava.protocol.impl.connection.MessageConsumer;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class DelegatingInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingInboundHandler.class);
    
    private MessageConsumer consumer;
    
    
    /** 
     * Constructs class + creates and sets MessageConsumer
     * @param connectionAdapter reference for adapter communicating with upper layers outside library
     */
    public DelegatingInboundHandler(CommunicationFacade connectionAdapter) {
        LOGGER.info("Creating DelegatingInboundHandler");
        if (connectionAdapter == null) {
            consumer = new ConnectionAdapterImpl();
        } else {
            consumer = connectionAdapter;
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        consumer.consume((DataObject) msg);
    }
}
