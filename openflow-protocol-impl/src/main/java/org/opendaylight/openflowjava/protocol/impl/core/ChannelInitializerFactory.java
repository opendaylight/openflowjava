/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.connection.TlsConfiguration;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;

/**
 * @author michal.polkorab
 *
 */
public class ChannelInitializerFactory {

    private long switchIdleTimeOut;
    private DeserializationFactory deserializationFactory;
    private SerializationFactory serializationFactory;
    private TlsConfiguration tlsConfig;
    private SwitchConnectionHandler switchConnectionHandler;
    
    /**
     * @return PublishingChannelInitializer that initializes new channels
     */
    public PublishingChannelInitializer createPublishingChannelInitializer() {
        PublishingChannelInitializer initializer = new PublishingChannelInitializer();
        initializer.setSwitchIdleTimeout(switchIdleTimeOut);
        initializer.setDeserializationFactory(deserializationFactory);
        initializer.setSerializationFactory(serializationFactory);
        initializer.setTlsConfiguration(tlsConfig);
        initializer.setSwitchConnectionHandler(switchConnectionHandler);
        return initializer;
    }

    /**
     * @return PublishingChannelInitializer that initializes new channels
     */
    public UdpChannelInitializer createUdpChannelInitializer() {
        UdpChannelInitializer initializer = new UdpChannelInitializer();
        initializer.setSwitchIdleTimeout(switchIdleTimeOut);
        initializer.setDeserializationFactory(deserializationFactory);
        initializer.setSerializationFactory(serializationFactory);
        initializer.setSwitchConnectionHandler(switchConnectionHandler);
        return initializer;
    }

    /**
     * @param switchIdleTimeOut
     */
    public void setSwitchIdleTimeout(long switchIdleTimeOut) {
        this.switchIdleTimeOut = switchIdleTimeOut;
    }

    /**
     * @param deserializationFactory
     */
    public void setDeserializationFactory(DeserializationFactory deserializationFactory) {
        this.deserializationFactory = deserializationFactory;
    }

    /**
     * @param serializationFactory
     */
    public void setSerializationFactory(SerializationFactory serializationFactory) {
        this.serializationFactory = serializationFactory;
    }

    /**
     * @param tlsConfig
     */
    public void setTlsConfig(TlsConfiguration tlsConfig) {
        this.tlsConfig = tlsConfig;
    }

    /**
     * @param switchConnectionHandler
     */
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        this.switchConnectionHandler = switchConnectionHandler;
    }
}