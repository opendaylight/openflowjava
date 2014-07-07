/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
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

    /** Size of Experimenter instruction header (2 * short + int) */
    private static final byte HEADER_LENGTH = 8;
    @Override
    public Instruction deserializeHeader(ByteBuf input) {
        InstructionBuilder builder = processHeader(input, false);
        return builder.build();
    }

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = processHeader(input, true);
        return builder.build();
    }

    private static InstructionBuilder processHeader(ByteBuf input, boolean readWholeAugmentation) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        int length = input.readUnsignedShort();
        addExpInstructionAugmentation(input, builder, length, readWholeAugmentation);
        return builder;
    }

    private static void addExpInstructionAugmentation(ByteBuf input,
            InstructionBuilder builder, int length, boolean readWholeAugmentation) {
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        if (readWholeAugmentation && ((length - HEADER_LENGTH) > 0)) {
            byte[] data = new byte[length - HEADER_LENGTH];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
    }
}