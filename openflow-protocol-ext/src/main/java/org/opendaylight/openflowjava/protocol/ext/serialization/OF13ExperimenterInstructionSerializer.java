/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * 
 * @author michal.polkorab
 */
public class OF13ExperimenterInstructionSerializer implements OFSerializer<Instruction>,
        HeaderSerializer<Instruction>{

    @Override
    public void serializeHeader(Instruction instruction, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_IDS_LENGTH);
        outBuffer.writeInt(instruction
                .getAugmentation(ExperimenterInstruction.class).getExperimenter().intValue());
    }

    @Override
    public void serialize(Instruction instruction, ByteBuf outBuffer) {
        int startIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        int lengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        ExperimenterInstruction experimenter =
                instruction.getAugmentation(ExperimenterInstruction.class);
        outBuffer.writeInt(experimenter.getExperimenter().intValue());
        if (experimenter.getData() != null) {
            outBuffer.writeBytes(experimenter.getData());
        }
        outBuffer.setShort(lengthIndex, outBuffer.writerIndex() - startIndex);
    }
}