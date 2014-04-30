/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterInstructionDeserializer implements OFDeserializer<Instruction>,
        HeaderDeserializer<Instruction> {

    @Override
    public Instruction deserializeHeader(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        return builder.build();
    }

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        if (input.readableBytes() > 0) {
            byte[] data = new byte[input.readableBytes()];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        return builder.build();
    }

}
