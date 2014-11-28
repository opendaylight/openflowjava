/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class MeterInstructionDeserializer extends AbstractInstructionDeserializer {

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Meter.class);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        MeterIdInstructionBuilder meterBuilder = new MeterIdInstructionBuilder();
        meterBuilder.setMeterId(input.readUnsignedInt());
        builder.addAugmentation(MeterIdInstruction.class, meterBuilder.build());
        return builder.build();
    }

}
