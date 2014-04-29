/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class SimpleDeserializerRegistryHelper {

    private short version;
    private DeserializerRegistry registry;

    /**
     * @param version wire protocol version
     * @param deserializerRegistry registry to be filled with message deserializers
     */
    public SimpleDeserializerRegistryHelper(short version, DeserializerRegistry deserializerRegistry) {
        this.version = version;
        this.registry = deserializerRegistry;
    }

    /**
     * @param code code / value to distinguish between deserializers
     * @param deserializedObjectClass class of object that will be deserialized
     *  by given deserializer
     * @param deserializer deserializer instance
     */
    public void registerDeserializer(int code,
            Class<?> deserializedObjectClass, OFGeneralDeserializer deserializer) {
        registry.registerDeserializer(new MessageCodeKey(version, code,
                deserializedObjectClass), deserializer);
    }
}
