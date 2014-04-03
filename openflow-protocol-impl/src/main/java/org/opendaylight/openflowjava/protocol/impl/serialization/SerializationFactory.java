/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class SerializationFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SerializationFactory.class);
    private SerializerRegistry registry;
    
    /**
     * Transforms POJO message into ByteBuf
     * @param version version used for encoding received message
     * @param out ByteBuf for storing and sending transformed message
     * @param message POJO message
     */
    public <E extends DataObject> void messageToBuffer(short version, ByteBuf out, E message) {
        @SuppressWarnings("unchecked")
        MessageTypeKey<E> msgTypeKey = new MessageTypeKey<>(version, (Class<E>) message.getClass());
        OFSerializer<E> serializer = registry.getSerializer(msgTypeKey);
        if (serializer != null) {
            serializer.serialize(message, out);
        } else {
            LOGGER.warn("No correct encoder found in EncoderTable for arguments: " + msgTypeKey.toString());
        }
    }

    /**
     * @param serializerRegistry registry with serializers
     */
    public void setSerializerTable(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
    }

}
