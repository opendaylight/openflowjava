/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;

/**
 * @author michal.polkorab
 *
 */
public class MatchEntryDeserializerRegistryHelper {

    private short version;
    private DeserializerRegistry registry;

    /**
     * @param version wire protocol version
     * @param deserializerRegistry registry to be filled with message deserializers
     */
    public MatchEntryDeserializerRegistryHelper(short version, DeserializerRegistry deserializerRegistry) {
        this.version = version;
        this.registry = deserializerRegistry;
    }

    /**
     * Registers match entry deserializer under provided oxmfield ()
     * @param oxmField oxm_field value/code
     * @param deserializer deserializer instance
     */
    public void register(int oxmField, OFGeneralDeserializer deserializer) {
        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(
                version, OxmMatchConstants.OPENFLOW_BASIC_CLASS, oxmField);
        key.setExperimenterId(null);
        registry.registerDeserializer(key, deserializer);
    }

    public void registerExperimenter(int oxmField, long expId, OFGeneralDeserializer deserializer) {
        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(
                version, OxmMatchConstants.EXPERIMENTER_CLASS, oxmField);
        key.setExperimenterId(expId);
        registry.registerDeserializer(key, deserializer);
    }
}
