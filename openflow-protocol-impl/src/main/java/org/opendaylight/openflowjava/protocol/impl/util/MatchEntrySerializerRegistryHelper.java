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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.OxmClassBase;

/**
 * @author michal.polkorab
 * @param <C> OXM class
 */
public class MatchEntrySerializerRegistryHelper<C extends OxmClassBase> {

    private short version;
    private SerializerRegistry serializerRegistry;

    /**
     * @param version Openflow wire version
     * @param serializerRegistry
     */
    public MatchEntrySerializerRegistryHelper(short version, SerializerRegistry serializerRegistry) {
        this.version = version;
        this.serializerRegistry = serializerRegistry;
    }

    /**
     * Registers OpenflowBasicClass type match serializer
     * @param specificClass
     * @param serializer
     */
    public <F extends MatchField> void registerSerializer(
            Class<F> specificClass, OFGeneralSerializer serializer) {
        MatchEntrySerializerKey<?, ?> key = new MatchEntrySerializerKey<>(version, OpenflowBasicClass.class,
                specificClass);
        key.setExperimenterId(null);
        serializerRegistry.registerSerializer(key, serializer);
    }

    /**
     * Registers ExperimenterClass type match serializer
     * @param specificClass
     * @param serializer
     */
    public <F extends MatchField> void registerExperimenterSerializer(
            Class<F> specificClass, long expId, OFGeneralSerializer serializer) {
        MatchEntrySerializerKey<?, ?> key = new MatchEntrySerializerKey<>(version, ExperimenterClass.class, specificClass);
        key.setExperimenterId(expId);
        serializerRegistry.registerSerializer(key, serializer);
    }

}
