/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.spi.connection;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author mirehak
 * @author michal.polkorab
 *
 */
public interface SwitchConnectionProvider extends AutoCloseable {

    /**
     * @param configuration [protocol, port, address and supported features]
     */
    void setConfiguration(ConnectionConfiguration configuration);
    
    /**
     * start listening to switches, but please don't forget to do
     * {@link #setSwitchConnectionHandler(SwitchConnectionHandler)} first
     * @return future, triggered to true, when listening channel is up and running
     */
    ListenableFuture<Boolean> startup();
    
    /**
     * stop listening to switches
     * @return future, triggered to true, when all listening channels are down
     */
    ListenableFuture<Boolean> shutdown();
    
    /**
     * @param switchConHandler instance being informed when new switch connects
     */
    void setSwitchConnectionHandler(SwitchConnectionHandler switchConHandler);

    /**
     * Registers custom serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public  <KEY_TYPE> void registerSerializer(MessageTypeKey<KEY_TYPE> key,
            OFGeneralSerializer serializer);

    /**
     * Registers custom deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerDeserializer(MessageCodeKey key, OFGeneralDeserializer deserializer);

    /**
     * Unregisters custom serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     * @return true if serializer was removed,
     *  false if no serializer was found under specified key
     */
    public  <KEY_TYPE> boolean unregisterSerializer(MessageTypeKey<KEY_TYPE> key);

    /**
     * Unregisters custom deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     * @return true if deserializer was removed,
     *  false if no deserializer was found under specified key
     */
    public boolean unregisterDeserializer(MessageCodeKey key);
}
