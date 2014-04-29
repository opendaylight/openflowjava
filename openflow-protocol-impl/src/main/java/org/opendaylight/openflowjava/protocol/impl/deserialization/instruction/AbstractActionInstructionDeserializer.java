/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.CodeKeyMaker;
import org.opendaylight.openflowjava.protocol.impl.util.CodeKeyMakerFactory;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;
import org.opendaylight.openflowjava.protocol.impl.util.ListDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.InstructionBase;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractActionInstructionDeserializer extends AbstractInstructionDeserializer
        implements DeserializerRegistryInjector {

    private DeserializerRegistry registry;

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(getType());
        int instructionLength = input.readUnsignedShort();
        input.skipBytes(InstructionConstants.PADDING_IN_ACTIONS_INSTRUCTION);
        ActionsInstructionBuilder actionsBuilder = new ActionsInstructionBuilder();
        int length = instructionLength - InstructionConstants.STANDARD_INSTRUCTION_LENGTH;
        CodeKeyMaker keyMaker = CodeKeyMakerFactory.createActionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
        List<Action> actions = ListDeserializer.deserializeList(
                EncodeConstants.OF13_VERSION_ID, length, input, keyMaker, getRegistry());
        actionsBuilder.setAction(actions);
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
        return builder.build();
    }

    protected abstract Class<? extends InstructionBase> getType();

    protected DeserializerRegistry getRegistry() {
        return registry;
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        this.registry = deserializerRegistry;
    }
}
