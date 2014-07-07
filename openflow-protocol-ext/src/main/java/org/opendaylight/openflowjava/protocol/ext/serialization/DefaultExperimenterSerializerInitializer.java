/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * @author michal.polkorab
 *
 */
public class DefaultExperimenterSerializerInitializer {

    /**
     * Registers message serializers into provided registry
     * @param provider provider to be initialized with message serializers
     */
    public static void registerSerializers(SwitchConnectionProvider provider) {
        // register OF v1.0 default experimenter serializers
        short version = EncodeConstants.OF10_VERSION_ID;
        // - default vendor (experimenter) message serializer
        provider.registerSerializer(new MessageTypeKey<>(version, ExperimenterInput.class),
                new OF10VendorInputMessageFactory());
        // - default vendor (experimenter) action serializer
        provider.registerSerializer(new EnhancedMessageTypeKey<>(version, Action.class,
                Experimenter.class), new OF10VendorActionSerializer());
        // - default vendor stats (experimenter multipart) serializer
        provider.registerSerializer(new MessageTypeKey<>(version, MultipartRequestExperimenter.class),
                new OF10StatsRequestVendorSerializer());

        // register OF v1.3 default experimenter serializers
        version = EncodeConstants.OF13_VERSION_ID;
        // - default experimenter message serializer
        provider.registerSerializer(new MessageTypeKey<>(version, ExperimenterInput.class),
                new OF13ExperimenterInputMessageFactory());
        // - default experimenter action serializer
        provider.registerSerializer(new EnhancedMessageTypeKey<>(version, Action.class,
                Experimenter.class), new OF13ExperimenterActionSerializer());
        // - default vendor stats (experimenter multipart) serializer
        provider.registerSerializer(new MessageTypeKey<>(version, MultipartRequestExperimenter.class),
                new OF13MultipartExperimenterSerializer());
        // - default experimenter instruction serializer
        provider.registerSerializer(new EnhancedMessageTypeKey<>(version, Instruction.class,
                org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common
                .instruction.rev130731.Experimenter.class), new OF13ExperimenterInstructionSerializer());
        // - default experimenter message serializer
        provider.registerSerializer(new MessageTypeKey<>(version, TableFeatureProperties.class),
                new OF13TableFeatExpSerializer());
        // - default experimenter message serializer
        provider.registerSerializer(new MessageTypeKey<>(version, MeterBandExperimenter.class),
                new OF13MeterBandExperimenterSerializer());
    }
}