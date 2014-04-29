/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.ApplyActionsInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.ClearActionsInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.GoToTableInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.MeterInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.WriteActionsInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.instruction.WriteMetadataInstructionDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.SimpleDeserializerRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class InstructionDeserializerInitializer {

    /**
     * Registers instruction deserializers
     * @param registry registry to be filled with deserializers
     */
    public static void registerDeserializers(DeserializerRegistry registry) {
        // register OF v1.3 instruction deserializers
        SimpleDeserializerRegistryHelper helper =
                new SimpleDeserializerRegistryHelper(EncodeConstants.OF13_VERSION_ID, registry);
        helper.registerDeserializer(1, Instruction.class, new GoToTableInstructionDeserializer());
        helper.registerDeserializer(2, Instruction.class, new WriteMetadataInstructionDeserializer());
        helper.registerDeserializer(3, Instruction.class, new WriteActionsInstructionDeserializer());
        helper.registerDeserializer(4, Instruction.class, new ApplyActionsInstructionDeserializer());
        helper.registerDeserializer(5, Instruction.class, new ClearActionsInstructionDeserializer());
        helper.registerDeserializer(6, Instruction.class, new MeterInstructionDeserializer());
    }
}
