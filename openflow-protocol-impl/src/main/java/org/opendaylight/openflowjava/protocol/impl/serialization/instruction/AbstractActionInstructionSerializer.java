/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.instruction;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.EnhancedTypeKeyMakerFactory;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;
import org.opendaylight.openflowjava.protocol.impl.util.ListSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractActionInstructionSerializer extends AbstractInstructionSerializer
        implements SerializerRegistryInjector {

    private SerializerRegistry registry;

    @Override
    public void serialize(Instruction instruction, ByteBuf outBuffer) {
        int startIndex = outBuffer.writerIndex();
        outBuffer.writeShort(getType());
        if (instruction.getAugmentation(ActionsInstruction.class) != null) {
            List<Action> actions = instruction.getAugmentation(ActionsInstruction.class).getAction();
            int lengthIndex = outBuffer.writerIndex();
            outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
            ByteBufUtils.padBuffer(InstructionConstants.PADDING_IN_ACTIONS_INSTRUCTION, outBuffer);
            ListSerializer.serializeList(actions, EnhancedTypeKeyMakerFactory
                    .createActionKeyMaker(EncodeConstants.OF13_VERSION_ID), getRegistry(), outBuffer);
            int instructionLength = outBuffer.writerIndex() - startIndex;
            outBuffer.setShort(lengthIndex, instructionLength);
        } else {
            outBuffer.writeShort(InstructionConstants.STANDARD_INSTRUCTION_LENGTH);
            ByteBufUtils.padBuffer(InstructionConstants.PADDING_IN_ACTIONS_INSTRUCTION, outBuffer);
        }
    }

    protected SerializerRegistry getRegistry() {
        return registry;
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        registry = serializerRegistry;
    }
}
