/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;

/**
 * @author michal.polkorab
 * @param <OXM_CLASS> OXM class
 */
public class MatchEntrySerializerRegistryHelper<OXM_CLASS extends OxmClassBase> {

    private short version;
    private Class<OXM_CLASS> generalClass;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version Openflow wire version
     * @param generalClass
     * @param serializerRegistry
     */
    public MatchEntrySerializerRegistryHelper(short version, Class<OXM_CLASS> generalClass,
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
    public <OXM_FIELD extends MatchField> void registerSerializer(
            Class<OXM_FIELD> specificClass, OFGeneralSerializer serializer) {
        MatchEntrySerializerKey<?, ?> key = new MatchEntrySerializerKey<>(version, generalClass, specificClass);
        key.setExperimenterId(null);
        serializerRegistry.registerSerializer(key, serializer);
    }
}
