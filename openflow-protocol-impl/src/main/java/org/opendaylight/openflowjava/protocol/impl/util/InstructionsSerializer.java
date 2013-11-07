/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
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
 * @author michal.polkorab
 *
 */
public abstract class InstructionsSerializer {

    /**
     * Encodes instructions
     * @param instructions List of instructions
     * @param out output buffer
     */
    public static void encodeInstructions(List<Instructions> instructions, ByteBuf out) {
        if (instructions != null) {
            for (Instructions instruction : instructions) {
                Class<? extends Instruction> type = instruction.getType();
                if (type.equals(GotoTable.class)) {
                    final byte GOTO_TABLE_TYPE = 1;
                    final byte GOTO_TABLE_LENGTH = 8;
                    writeTypeAndLength(out, GOTO_TABLE_TYPE, GOTO_TABLE_LENGTH);
                    out.writeByte(instruction.getAugmentation(TableIdInstruction.class).getTableId());
                    ByteBufUtils.padBuffer(3, out);
                } else if (type.equals(WriteMetadata.class)) {
                    final byte WRITE_METADATA_TYPE = 2;
                    final byte WRITE_METADATA_LENGTH = 24;
                    writeTypeAndLength(out, WRITE_METADATA_TYPE, WRITE_METADATA_LENGTH);
                    ByteBufUtils.padBuffer(4, out);
                    MetadataInstruction metadata = instruction.getAugmentation(MetadataInstruction.class);
                    out.writeBytes(metadata.getMetadata());
                    out.writeBytes(metadata.getMetadataMask());
                } else if (type.equals(WriteActions.class)) {
                    final byte WRITE_ACTIONS_TYPE = 3;
                    writeActionsInstruction(out, instruction, WRITE_ACTIONS_TYPE);
                } else if (type.equals(ApplyActions.class)) {
                    final byte APPLY_ACTIONS_TYPE = 4;
                    writeActionsInstruction(out, instruction, APPLY_ACTIONS_TYPE);
                } else if (type.equals(ClearActions.class)) {
                    final byte CLEAR_ACTIONS_TYPE = 5;
                    final byte CLEAR_ACTIONS_LENGTH = 8;
                    writeTypeAndLength(out, CLEAR_ACTIONS_TYPE, CLEAR_ACTIONS_LENGTH);
                    ByteBufUtils.padBuffer(4, out);
                } else if (type.equals(Meter.class)) {
                    final byte METER_TYPE = 6;
                    final byte METER_LENGTH = 8;
                    writeTypeAndLength(out, METER_TYPE, METER_LENGTH);
                    out.writeInt(instruction.getAugmentation(MeterIdInstruction.class).getMeterId().intValue());
                } else if (type.equals(Experimenter.class)) {
                    final byte EXPERIMENTER_TYPE = 7;
                    final byte EXPERIMENTER_LENGTH = 8;
                    ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
                    byte[] data = experimenter.getData();
                    writeTypeAndLength(out, EXPERIMENTER_TYPE, EXPERIMENTER_LENGTH + data.length);
                    out.writeInt(experimenter.getExperimenter().intValue());
                    out.writeBytes(data);
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
        List<ActionsList> actions = instruction.getAugmentation(ActionsInstruction.class).getActionsList();
        out.writeShort(ActionsSerializer.computeLengthOfActions(actions));
        ByteBufUtils.padBuffer(4, out);
        ActionsSerializer.encodeActions(actions, out);
    }
    
    /**
     * Computes length of instructions
     * @param instructions List of instructions
     * @return length of instructions(in bytes)
     */
    public static int computeInstructionsLength(List<Instructions> instructions) {
        int length = 0;
        if (instructions != null) {
            for (Instructions instruction : instructions) {
                Class<? extends Instruction> type = instruction.getType();
                if (type.equals(GotoTable.class)) {
                    final byte GOTO_TABLE_LENGTH = 8;
                    length += GOTO_TABLE_LENGTH;
                } else if (type.equals(WriteMetadata.class)) {
                    final byte WRITE_METADATA_LENGTH = 24;
                    length += WRITE_METADATA_LENGTH;
                } else if (type.equals(WriteActions.class)) {
                    length += ActionsSerializer.computeLengthOfActions(
                            instruction.getAugmentation(ActionsInstruction.class).getActionsList());
                } else if (type.equals(ApplyActions.class)) {
                    length += ActionsSerializer.computeLengthOfActions(
                            instruction.getAugmentation(ActionsInstruction.class).getActionsList());
                } else if (type.equals(ClearActions.class)) {
                    final byte CLEAR_ACTIONS_LENGTH = 8;
                    length += CLEAR_ACTIONS_LENGTH;
                } else if (type.equals(Meter.class)) {
                    final byte METER_LENGTH = 8;
                    length += METER_LENGTH;
                } else if (type.equals(Experimenter.class)) {
                    final byte EXPERIMENTER_LENGTH = 8;
                    length += EXPERIMENTER_LENGTH;
                }
            }
        }
        return length;
    }
}
