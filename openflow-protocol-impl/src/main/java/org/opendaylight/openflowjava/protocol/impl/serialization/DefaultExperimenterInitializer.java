/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF10StatsRequestVendorSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF10VendorInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13ExperimenterActionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13ExperimenterInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13ExperimenterInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13MeterBandExperimenterSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13MultipartExperimenterSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.experimenters.OF13TableFeatExpSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.CommonMessageRegistryHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.EnhancedKeyRegistryHelper;
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
public class DefaultExperimenterInitializer {

    /**
     * Registers message serializers into provided registry
     * @param serializerRegistry registry to be initialized with message serializers
     */
    public static void registerDefaultExperimenterSerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.0 default experimenter serializers
        short version = EncodeConstants.OF10_VERSION_ID;
        CommonMessageRegistryHelper helper = new CommonMessageRegistryHelper(version, serializerRegistry);
        helper.registerSerializer(ExperimenterInput.class, new OF10VendorInputMessageFactory());
        EnhancedKeyRegistryHelper<Action> actionHelper = new EnhancedKeyRegistryHelper<>(
                EncodeConstants.OF10_VERSION_ID, Action.class, serializerRegistry);
        actionHelper.registerSerializer(Experimenter.class, new OF13ExperimenterActionSerializer());
        helper.registerSerializer(MultipartRequestExperimenter.class, new OF10StatsRequestVendorSerializer());
        // register OF v1.3 default experimenter serializers
        version = EncodeConstants.OF13_VERSION_ID;
        helper = new CommonMessageRegistryHelper(version, serializerRegistry);
        helper.registerSerializer(ExperimenterInput.class, new OF13ExperimenterInputMessageFactory());
        helper.registerSerializer(MultipartRequestExperimenter.class, new OF13MultipartExperimenterSerializer());
        helper.registerSerializer(TableFeatureProperties.class, new OF13TableFeatExpSerializer());
        helper.registerSerializer(MeterBandExperimenter.class, new OF13MeterBandExperimenterSerializer());
        actionHelper = new EnhancedKeyRegistryHelper<>(
                EncodeConstants.OF13_VERSION_ID, Action.class, serializerRegistry);
        actionHelper.registerSerializer(Experimenter.class, new OF13ExperimenterActionSerializer());
        EnhancedKeyRegistryHelper<Instruction> insHelper = new EnhancedKeyRegistryHelper<>(
                EncodeConstants.OF13_VERSION_ID, Instruction.class, serializerRegistry);
        insHelper.registerSerializer(org.opendaylight.yang.gen.v1.urn.opendaylight
                .openflow.common.instruction.rev130731.Experimenter.class,
                new OF13ExperimenterInstructionSerializer());
    }
}
