/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractInstructionDeserializer implements OFDeserializer<Instruction>,
        HeaderDeserializer<Instruction> {

    @Override
    public Instruction deserializeHeader(ByteBuf rawMessage) {
        InstructionBuilder builder = processHeader(rawMessage);
        rawMessage.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        return builder.build();
    }

    protected InstructionBuilder processHeader(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        int type = input.readUnsignedShort();
        switch (type) {
        case 1:
            builder.setType(GotoTable.class);
            break;
        case 2:
            builder.setType(WriteMetadata.class);
            break;
        case 3:
            builder.setType(WriteActions.class);
            break;
        case 4:
            builder.setType(ApplyActions.class);
            break;
        case 5:
            builder.setType(ClearActions.class);
            break;
        case 6:
            builder.setType(Meter.class);
            break;
        default:
            throw new IllegalStateException("Unknown instruction type received, type: " + type);
        }
        return builder;
    }
}
