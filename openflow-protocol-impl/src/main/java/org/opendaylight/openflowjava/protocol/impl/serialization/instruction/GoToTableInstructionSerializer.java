/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.instruction;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class GoToTableInstructionSerializer extends AbstractInstructionSerializer {

    @Override
    public void serialize(Instruction instruction, ByteBuf outBuffer) {
        outBuffer.writeShort(getType());
        outBuffer.writeShort(InstructionConstants.STANDARD_INSTRUCTION_LENGTH);
        outBuffer.writeByte(instruction.getAugmentation(TableIdInstruction.class).getTableId());
        ByteBufUtils.padBuffer(InstructionConstants.PADDING_IN_GOTO_TABLE, outBuffer);
    }

    @Override
    protected int getType() {
        return InstructionConstants.GOTO_TABLE_TYPE;
    }

}
