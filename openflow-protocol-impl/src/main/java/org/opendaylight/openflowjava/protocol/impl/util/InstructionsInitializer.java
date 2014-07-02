/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.ApplyActionsInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.ClearActionsInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.GoToTableInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.MeterInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.WriteActionsInstructionSerializer;
import org.opendaylight.openflowjava.protocol.impl.serialization.instruction.WriteMetadataInstructionSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class InstructionsInitializer {

    /**
     * Registers instruction serializers into provided registry
     * @param serializerRegistry registry to be initialized with instruction serializers
     */
    public static void registerInstructionSerializers(SerializerRegistry serializerRegistry) {
        Class<Instruction> instructionClass = Instruction.class;
        // register OF v1.3 instruction serializers
        EnhancedKeyRegistryHelper<Instruction> helper =
                new EnhancedKeyRegistryHelper<>(EncodeConstants.OF13_VERSION_ID,
                        instructionClass, serializerRegistry);
        helper.registerSerializer(GotoTable.class, new GoToTableInstructionSerializer());
        helper.registerSerializer(WriteMetadata.class, new WriteMetadataInstructionSerializer());
        helper.registerSerializer(WriteActions.class, new WriteActionsInstructionSerializer());
        helper.registerSerializer(ApplyActions.class, new ApplyActionsInstructionSerializer());
        helper.registerSerializer(ClearActions.class, new ClearActionsInstructionSerializer());
        helper.registerSerializer(Meter.class, new MeterInstructionSerializer());
    }
}
