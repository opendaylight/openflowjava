/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;

/**
 * @author michal.polkorab
 * @param <T> OXM class
 */
public class EnhancedKeyRegistryHelper<T> {

    private short version;
    private Class<T> generalClass;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version Openflow wire version
     * @param generalClass
     * @param serializerRegistry
     */
    public EnhancedKeyRegistryHelper(short version, Class<T> generalClass,
            SerializerRegistry serializerRegistry) {
        this.version = version;
        this.generalClass = generalClass;
        this.serializerRegistry = serializerRegistry;
    }

    /**
     * Registers given serializer
     * @param specificClass
     * @param serializer
     */
    public void registerSerializer(Class<?> specificClass, OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey<>(version,
                generalClass, specificClass), serializer);
    }
}
