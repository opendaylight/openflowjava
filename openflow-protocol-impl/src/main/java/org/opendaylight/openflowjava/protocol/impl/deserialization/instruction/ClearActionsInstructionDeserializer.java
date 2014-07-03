/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class ClearActionsInstructionDeserializer extends AbstractInstructionDeserializer {

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = super.processHeader(input);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        input.skipBytes(InstructionConstants.PADDING_IN_ACTIONS_INSTRUCTION);
        ActionsInstructionBuilder actionsBuilder = 
                new ActionsInstructionBuilder();
        List<Action> actions = new ArrayList<>();
        actionsBuilder.setAction(actions);
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
        return builder.build();
    }
}