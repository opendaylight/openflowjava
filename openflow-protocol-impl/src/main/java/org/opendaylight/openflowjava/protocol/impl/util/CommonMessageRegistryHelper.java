/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;

/**
 * @author michal.polkorab
 *
 */
public class CommonMessageRegistryHelper {

    private short version;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version
     * @param serializerRegistry
     */
    public CommonMessageRegistryHelper(short version, SerializerRegistry serializerRegistry) {
        this.version = version;
        this.serializerRegistry = serializerRegistry;
    }

    /**
     * @param msgType
     * @param serializer
     */
    public void registerSerializer(Class<?> msgType, OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(new MessageTypeKey<>(version, msgType), serializer);
    }
}
