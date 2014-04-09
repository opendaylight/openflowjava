/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.TypeToClassMapInitializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class DeserializationFactory {

    private DeserializerRegistry registry;
    private Map<TypeToClassKey, Class<?>> messageClassMap;

    /**
     * Constructor
     */
    public DeserializationFactory() {
        messageClassMap = new HashMap<>();
        initTypeToClassMapping();
    }

    /**
     * Transforms ByteBuf into correct POJO message
     * @param rawMessage 
     * @param version version decoded from OpenFlow protocol message
     * @return correct POJO as DataObject
     */
    public DataObject deserialize(ByteBuf rawMessage, short version) {
        DataObject dataObject = null;
        short type = rawMessage.readUnsignedByte();
        rawMessage.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);

        OFDeserializer<DataObject> deserializer = registry.getDeserializer(
                new MessageCodeKey(version, type, messageClassMap.get(new TypeToClassKey(version, type))));
        if (deserializer != null) {
            dataObject = deserializer.deserialize(rawMessage, version);
        }
        return dataObject;
    }

    /**
     * @param registry
     */
    public void setRegistry(DeserializerRegistry registry) {
        this.registry = registry;
    }

    private void initTypeToClassMapping() {
        TypeToClassMapInitializer.initializeTypeToClassMap(messageClassMap);
    }
}
