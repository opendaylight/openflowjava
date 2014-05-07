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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 * @param <T> OXM class
 */
public class OF13MatchEntriesRegistryHelper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OF13MatchEntriesRegistryHelper.class);
    private short version;
    private Class<T> oxmClass;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version Openflow wire version
     * @param oxmClass
     * @param serializerRegistry
     */
    public OF13MatchEntriesRegistryHelper(short version, Class<T> oxmClass, SerializerRegistry serializerRegistry) {

        LOGGER.info(" **NXM** OF13InstructionsSerializer(2nd ApplyActions.class) => {} => {}",
            oxmClass, version, serializerRegistry);

        this.version = version;
        this.oxmClass = oxmClass;
        this.serializerRegistry = serializerRegistry;
    }

    /**
     * Registers given serializer
     * @param oxmField
     * @param serializer
     */
    public void registerSerializer(Class<?> oxmField, OFGeneralSerializer serializer) {
        LOGGER.info(" **NXM** OF13InstructionsSerializer(2nd ApplyActions.class) => {} => {}", oxmField, serializer);

        serializerRegistry.registerSerializer(new EnhancedMessageTypeKey<>(version, oxmClass, oxmField),
            serializer);
    }
}
