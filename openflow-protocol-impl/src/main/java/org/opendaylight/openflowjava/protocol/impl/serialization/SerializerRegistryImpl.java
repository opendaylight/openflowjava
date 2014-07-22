/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF13MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores and handles serializers
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class SerializerRegistryImpl implements SerializerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializerRegistryImpl.class);
    private static final short OF10 = EncodeConstants.OF10_VERSION_ID;
    private static final short OF13 = EncodeConstants.OF13_VERSION_ID;
    private Map<MessageTypeKey<?>, OFGeneralSerializer> registry;


    @Override
    public void init() {
        registry = new HashMap<>();
        // Openflow message type serializers
        MessageFactoryInitializer.registerMessageSerializers(this);

        // match structure serializers
        registerSerializer(new MessageTypeKey<>(OF10, MatchV10.class), new OF10MatchSerializer());
        registerSerializer(new MessageTypeKey<>(OF13, Match.class), new OF13MatchSerializer());

        // match entry serializers
        MatchEntriesInitializer.registerMatchEntrySerializers(this);
        // action serializers
        ActionsInitializer.registerActionSerializers(this);
        // instruction serializers
        InstructionsInitializer.registerInstructionSerializers(this);
    }

    /**
     * @param msgTypeKey
     * @return encoder for current type of message (msgTypeKey)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <KEY_TYPE, SERIALIZER_TYPE extends OFGeneralSerializer> SERIALIZER_TYPE getSerializer(
            MessageTypeKey<KEY_TYPE> msgTypeKey) {
        OFGeneralSerializer serializer = registry.get(msgTypeKey);
        if (serializer == null) {
            throw new IllegalArgumentException("Serializer for key: {}" + msgTypeKey.toString()
                    + " was not found - please verify that you are using correct message"
                    + " combination (e.g. OF v1.0 message to OF v1.0 device)");
        }
        return (SERIALIZER_TYPE) serializer;
    }

    @Override
    public <KEY_TYPE> void registerSerializer(
            MessageTypeKey<KEY_TYPE> msgTypeKey, OFGeneralSerializer serializer) {
        if ((msgTypeKey == null) || (serializer == null)) {
            throw new IllegalArgumentException("MessageTypeKey or Serializer is null");
        }
        OFGeneralSerializer serInRegistry = registry.get(msgTypeKey);
        registry.put(msgTypeKey, serializer);
        if (serInRegistry != null) {
            LOGGER.warn("Serializer for key " + msgTypeKey + " overwritten. Old serializer: "
                    + serInRegistry.getClass().getName() + ", new serializer: "
                    + serializer.getClass().getName() );
        }
        if (serializer instanceof SerializerRegistryInjector) {
            ((SerializerRegistryInjector) serializer).injectSerializerRegistry(this);
        }
    }

    @Override
    public <KEY_TYPE> boolean unregisterSerializer(MessageTypeKey<KEY_TYPE> msgTypeKey) {
        if (msgTypeKey == null) {
            throw new IllegalArgumentException("MessageTypeKey is null");
        }
        OFGeneralSerializer serializer = registry.remove(msgTypeKey);
        if (serializer == null) {
            return false;
        }
        return true;
    }
}
