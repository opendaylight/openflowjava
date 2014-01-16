/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Instruction;

/**
 * Serializes ofp_instruction (OpenFlow v 1.3) structure
 * @author michal.polkorab
 * @author timotej.kubas
 */
public abstract class InstructionsSerializer {

    private static final byte GOTO_TABLE_TYPE = 1;
    private static final byte WRITE_METADATA_TYPE = 2;
    private static final byte WRITE_ACTIONS_TYPE = 3;
    private static final byte APPLY_ACTIONS_TYPE = 4;
    private static final byte CLEAR_ACTIONS_TYPE = 5;
    private static final byte METER_TYPE = 6;
    private static final byte EXPERIMENTER_TYPE = 7;
    private static final byte GOTO_TABLE_LENGTH = 8;
    private static final byte WRITE_METADATA_LENGTH = 24;
    private static final byte METER_LENGTH = 8;
    private static final byte EXPERIMENTER_LENGTH = 8;
    private static final byte ACTIONS_INSTRUCTION_LENGTH = 8;
    private static final byte PADDING_IN_GOTO_TABLE = 3;
    private static final byte PADDING_IN_WRITE_METADATA = 4;
    private static final byte PADDING_IN_CLEAR_ACTIONS = 4;
    private static final byte INSTRUCTION_IDS_LENGTH = 4;
    private static final byte PADDING_IN_ACTIONS_INSTRUCTION = 4;
    
    /**
     * Encodes instructions
     * @param instructions List of instructions
     * @param out output buffer
     */
    public static void encodeInstructions(List<Instructions> instructions, ByteBuf out) {
        if (instructions != null) {
            for (Instructions instruction : instructions) {
                Class<? extends Instruction> type = instruction.getType();
                if (type.isAssignableFrom(GotoTable.class)) {
                    writeTypeAndLength(out, GOTO_TABLE_TYPE, GOTO_TABLE_LENGTH);
                    out.writeByte(instruction.getAugmentation(TableIdInstruction.class).getTableId());
                    ByteBufUtils.padBuffer(PADDING_IN_GOTO_TABLE, out);
                } else if (type.isAssignableFrom(WriteMetadata.class)) {
                    writeTypeAndLength(out, WRITE_METADATA_TYPE, WRITE_METADATA_LENGTH);
                    ByteBufUtils.padBuffer(PADDING_IN_WRITE_METADATA, out);
                    MetadataInstruction metadata = instruction.getAugmentation(MetadataInstruction.class);
                    out.writeBytes(metadata.getMetadata());
                    out.writeBytes(metadata.getMetadataMask());
                } else if (type.isAssignableFrom(WriteActions.class)) {
                    writeActionsInstruction(out, instruction, WRITE_ACTIONS_TYPE);
                } else if (type.isAssignableFrom(ApplyActions.class)) {
                    writeActionsInstruction(out, instruction, APPLY_ACTIONS_TYPE);
                } else if (type.isAssignableFrom(ClearActions.class)) {
                    writeTypeAndLength(out, CLEAR_ACTIONS_TYPE, ACTIONS_INSTRUCTION_LENGTH);
                    ByteBufUtils.padBuffer(PADDING_IN_CLEAR_ACTIONS, out);
                } else if (type.isAssignableFrom(Meter.class)) {
                    writeTypeAndLength(out, METER_TYPE, METER_LENGTH);
                    out.writeInt(instruction.getAugmentation(MeterIdInstruction.class).getMeterId().intValue());
                } else if (type.isAssignableFrom(Experimenter.class)) {
                    ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
                    byte[] data = experimenter.getData();
                    writeTypeAndLength(out, EXPERIMENTER_TYPE, EXPERIMENTER_LENGTH + data.length);
                    out.writeInt(experimenter.getExperimenter().intValue());
                    out.writeBytes(data);
                }
            }
        }
        
    }
    
    /**
     * Encodes instruction ids (for Multipart - TableFeatures messages)
     * @param instructions List of instruction identifiers (without values)
     * @param out output buffer
     */
    public static void encodeInstructionIds(List<Instructions> instructions, ByteBuf out) {
        if (instructions != null) {
            for (Instructions instruction : instructions) {
                Class<? extends Instruction> type = instruction.getType();
                if (type.isAssignableFrom(GotoTable.class)) {
                    writeTypeAndLength(out, GOTO_TABLE_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(WriteMetadata.class)) {
                    writeTypeAndLength(out, WRITE_METADATA_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(WriteActions.class)) {
                    writeTypeAndLength(out, WRITE_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(ApplyActions.class)) {
                    writeTypeAndLength(out, APPLY_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(ClearActions.class)) {
                    writeTypeAndLength(out, CLEAR_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(Meter.class)) {
                    writeTypeAndLength(out, METER_TYPE, INSTRUCTION_IDS_LENGTH);
                } else if (type.isAssignableFrom(Experimenter.class)) {
                    ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
                    writeTypeAndLength(out, EXPERIMENTER_TYPE, EncodeConstants.EXPERIMENTER_IDS_LENGTH);
                    out.writeInt(experimenter.getExperimenter().intValue());
                }
            }
        }
    }

    private static void writeTypeAndLength(ByteBuf out, int type, int length) {
        out.writeShort(type);
        out.writeShort(length);
    }

    private static void writeActionsInstruction(ByteBuf out,
            Instructions instruction, int type) {
        out.writeShort(type);
        if (instruction.getAugmentation(ActionsInstruction.class) != null) {
            List<ActionsList> actions = instruction.getAugmentation(ActionsInstruction.class).getActionsList();
            out.writeShort(ACTIONS_INSTRUCTION_LENGTH + ActionsSerializer.computeLengthOfActions(actions));
            ByteBufUtils.padBuffer(PADDING_IN_ACTIONS_INSTRUCTION, out);
            ActionsSerializer.encodeActions(actions, out);
        } else {
            out.writeShort(ACTIONS_INSTRUCTION_LENGTH);
            ByteBufUtils.padBuffer(PADDING_IN_ACTIONS_INSTRUCTION, out);
        }
    }
    
    /**
     * Computes length of instructions
     * @param instructions List of instructions
     * @return length of instructions (in bytes)
     */
    public static int computeInstructionsLength(List<Instructions> instructions) {
        int length = 0;
        if (instructions != null) {
            for (Instructions instruction : instructions) {
                Class<? extends Instruction> type = instruction.getType();
                if (type.isAssignableFrom(GotoTable.class)) {
                    length += GOTO_TABLE_LENGTH;
                } else if (type.isAssignableFrom(WriteMetadata.class)) {
                    length += WRITE_METADATA_LENGTH;
                } else if (type.isAssignableFrom(WriteActions.class)) {
                    length += ACTIONS_INSTRUCTION_LENGTH;
                    if (instruction.getAugmentation(ActionsInstruction.class) != null) {
                        length += ActionsSerializer.computeLengthOfActions(
                            instruction.getAugmentation(ActionsInstruction.class).getActionsList());
                    }
                } else if (type.isAssignableFrom(ApplyActions.class)) {
                    length += ACTIONS_INSTRUCTION_LENGTH;
                    if (instruction.getAugmentation(ActionsInstruction.class) != null) {
                        length += ActionsSerializer.computeLengthOfActions(
                                instruction.getAugmentation(ActionsInstruction.class).getActionsList());
                    }
                } else if (type.isAssignableFrom(ClearActions.class)) {
                    length += ACTIONS_INSTRUCTION_LENGTH;
                } else if (type.isAssignableFrom(Meter.class)) {
                    length += METER_LENGTH;
                } else if (type.isAssignableFrom(Experimenter.class)) {
                    ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
                    byte[] data = experimenter.getData();
                    length += EXPERIMENTER_LENGTH + data.length;
                }
            }
        }
        return length;
    }
}
