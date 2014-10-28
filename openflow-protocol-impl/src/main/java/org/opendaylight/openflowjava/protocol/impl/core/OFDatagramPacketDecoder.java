/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.opendaylight.openflowjava.protocol.impl.connection.MessageConsumer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class OFDatagramPacketDecoder extends SimpleChannelInboundHandler<VersionMessageUdpWrapper>{

    private static final Logger LOGGER = LoggerFactory.getLogger(OFDatagramPacketDecoder.class);
    private DeserializationFactory deserializationFactory;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, VersionMessageUdpWrapper msg)
            throws Exception {
        if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("UdpVersionMessageWrapper received");
                LOGGER.debug("<< " + ByteBufUtils.byteBufToHexString(msg.getMessageBuffer()));
        }
        DataObject dataObject = null;
        try {
            dataObject = deserializationFactory.deserialize(msg.getMessageBuffer(),msg.getVersion());
            if (dataObject == null) {
                LOGGER.warn("Translated POJO is null");
            } else {
                MessageConsumer consumer = UdpConnectionMap.getMessageConsumer(msg.getAddress());
                consumer.consume(dataObject);
            }
        } catch(Exception e) {
            LOGGER.error("Message deserialization failed");
            LOGGER.error(e.getMessage(), e);
            // TODO: delegate exception to allow easier deserialization
            // debugging / deserialization problem awareness
        } finally {
            msg.getMessageBuffer().release();
        }
    }

    /**
     * @param deserializationFactory
     */
    public void setDeserializationFactory(DeserializationFactory deserializationFactory) {
        this.deserializationFactory = deserializationFactory;
    }
}