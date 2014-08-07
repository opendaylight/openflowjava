/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;

/**
 * @author michal.polkorab
 *
 */
public class UdpChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

    private long switchIdleTimeout;
    private SerializationFactory serializationFactory;
    private DeserializationFactory deserializationFactory;
    private SwitchConnectionHandler switchConnectionHandler;

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ch.pipeline().addLast(PIPELINE_HANDLERS.OF_DATAGRAMPACKET_HANDLER.name(),
                new OFDatagramPacketHandler(switchConnectionHandler));
        OFDatagramPacketDecoder ofDatagramPacketDecoder = new OFDatagramPacketDecoder();
        ofDatagramPacketDecoder.setDeserializationFactory(deserializationFactory);
        ch.pipeline().addLast(PIPELINE_HANDLERS.OF_DATAGRAMPACKET_DECODER.name(),
                ofDatagramPacketDecoder);
        OFDatagramPacketEncoder ofDatagramPacketEncoder = new OFDatagramPacketEncoder();
        ofDatagramPacketEncoder.setSerializationFactory(serializationFactory);
        ch.pipeline().addLast(PIPELINE_HANDLERS.OF_ENCODER.name(), ofDatagramPacketEncoder);
//        connectionFacade.fireConnectionReadyNotification();
    }

    /**
     * @param serializationFactory
     */
    public void setSerializationFactory(SerializationFactory serializationFactory) {
        this.serializationFactory = serializationFactory;
    }

    /**
     * @param deserializationFactory
     */
    public void setDeserializationFactory(DeserializationFactory deserializationFactory) {
        this.deserializationFactory = deserializationFactory;
    }

    /**
     * @param switchConnectionHandler the switchConnectionHandler to set
     */
    public void setSwitchConnectionHandler(final SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }

    /**
     * @param switchIdleTimeout the switchIdleTimeout to set
     */
    public void setSwitchIdleTimeout(final long switchIdleTimeout) {
        this.switchIdleTimeout = switchIdleTimeout;
    }
}