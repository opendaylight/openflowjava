/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.opendaylight.openflowjava.protocol.impl.connection.MessageConsumer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEventBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class DelegatingInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingInboundHandler.class);
    
    protected MessageConsumer consumer;
    private boolean inactiveMessageSent = false;
    
    /** 
     * Constructs class + creates and sets MessageConsumer
     * @param connectionAdapter reference for adapter communicating with upper layers outside library
     */
    public DelegatingInboundHandler(MessageConsumer connectionAdapter) {
        LOGGER.debug("Creating DelegatingInboundHandler");
        consumer = connectionAdapter;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg)
            throws Exception {
        LOGGER.debug("Reading");
        consumer.consume((DataObject) msg);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Channel inactive");
        if (!inactiveMessageSent) {
            DisconnectEventBuilder builder = new DisconnectEventBuilder();
            builder.setInfo("Channel inactive");
            consumer.consume(builder.build());
            inactiveMessageSent = true;
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Channel unregistered");
        if (!inactiveMessageSent) {
            DisconnectEventBuilder builder = new DisconnectEventBuilder();
            builder.setInfo("Channel unregistered");
            consumer.consume(builder.build());
            inactiveMessageSent = true;
        }
    }
    
}
