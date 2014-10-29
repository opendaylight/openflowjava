/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;


/**
 * Provides methods for serialization part of extensibility.
 * In case of handling multiple structures of same type (actions,
 * instructions, match entries, ... ) which are differentiated by
 * vendor / experimenter subtype, vendor has to switch / choose between
 * these subtypes. <br />
 * 
 * This has to be done in this way because of unknown augmentations
 * - that's why vendor has to handle it in his own implementations.
 * @author michal.polkorab
 */
public interface SerializerExtensionProvider {

    /**
     * Unregisters custom serializer
     * @param key used for serializer lookup
     * @return true if serializer was removed,
     *  false if no serializer was found under specified key
     */
    boolean unregisterSerializer(ExperimenterSerializerKey key);

    /**
     * Registers action serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerActionSerializer(ExperimenterActionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers instruction serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerInstructionSerializer(ExperimenterInstructionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers match entry serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    <OXMCLASS extends OxmClassBase, OXMTYPE extends MatchField> void registerMatchEntrySerializer(
            MatchEntrySerializerKey<OXMCLASS, OXMTYPE> key,OFGeneralSerializer serializer);

    /**
     * Registers experimenter (vendor) message serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerExperimenterMessageSerializer(ExperimenterIdSerializerKey<ExperimenterInput> key,
            OFSerializer<ExperimenterInput> serializer);

    /**
     * Registers multipart-request (stats-request) serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerMultipartRequestSerializer(ExperimenterIdSerializerKey<MultipartRequestExperimenterCase> key,
            OFSerializer<MultipartRequestExperimenterCase> serializer);

    /**
     * Registers multipart-request table-features serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerMultipartRequestTFSerializer(ExperimenterIdSerializerKey<TableFeatureProperties> key,
            OFGeneralSerializer serializer);

    /**
     * Registers meter band serializer (used in meter-mod messages)
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    void registerMeterBandSerializer(ExperimenterIdSerializerKey<MeterBandExperimenterCase> key,
            OFSerializer<MeterBandExperimenterCase> serializer);
}