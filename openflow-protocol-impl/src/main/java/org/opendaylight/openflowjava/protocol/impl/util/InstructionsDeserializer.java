/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.InstructionsBuilder;

import io.netty.buffer.ByteBuf;

/**
 * @author michal.polkorab
 *
 */
public class InstructionsDeserializer {
    
    private static final byte WRITE_APPLY_CLEAR_ACTION_LENGTH = 8;

    /**
     * Creates list of instructions
     * @param input
     * @param length
     * @return list of ofp_instruction
     */
    public static List<Instructions> createInstructions(ByteBuf input, int length) {
        List<Instructions> instructions = new ArrayList<>();
        if (input.readableBytes() != 0) {
            int lengthOfInstructions = length;
            while (lengthOfInstructions > 0) {
                InstructionsBuilder builder = new InstructionsBuilder();
                int type = input.readUnsignedShort();
                int instructionLength = input.readUnsignedShort();
                lengthOfInstructions -= instructionLength;
                switch (type) {
                case 1:
                    createGotoTableInstruction(builder, input);
                    break;
                case 2:
                    createMetadataInstruction(builder, input);
                    break;
                case 3:
                    builder.setType(WriteActions.class);
                    createActionRelatedInstruction(input, builder, instructionLength - WRITE_APPLY_CLEAR_ACTION_LENGTH);
                    break;
                case 4:
                    builder.setType(ApplyActions.class);
                    createActionRelatedInstruction(input, builder, instructionLength - WRITE_APPLY_CLEAR_ACTION_LENGTH);
                    break;
                case 5:
                    builder.setType(ClearActions.class);
                    createActionRelatedInstruction(input, builder, instructionLength - WRITE_APPLY_CLEAR_ACTION_LENGTH);
                    break;
                case 6:
                    builder.setType(Meter.class);
                    MeterIdInstructionBuilder meterBuilder = new MeterIdInstructionBuilder();
                    meterBuilder.setMeterId(input.readUnsignedInt());
                    builder.addAugmentation(MeterIdInstruction.class, meterBuilder.build());
                    break;
                case 65535:
                    final byte EXPERIMENTER_HEADER_LENGTH = 8;
                    builder.setType(Experimenter.class);
                    ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
                    expBuilder.setExperimenter(input.readUnsignedInt());
                    int dataLength = instructionLength - EXPERIMENTER_HEADER_LENGTH;
                    if (dataLength > 0) {
                        byte[] data = new byte[dataLength];
                        input.readBytes(data);
                        expBuilder.setData(data);
                    }
                    builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
                    break;
                default:
                    break;
                }
                instructions.add(builder.build());
            }
        }
        return instructions;
    }

    private static void createGotoTableInstruction(InstructionsBuilder builder,
            ByteBuf input) {
        final byte GOTO_TABLE_PADDING = 3;
        builder.setType(GotoTable.class);
        TableIdInstructionBuilder tableBuilder = new TableIdInstructionBuilder();
        tableBuilder.setTableId(input.readUnsignedByte());
        builder.addAugmentation(TableIdInstruction.class, tableBuilder.build());
        input.skipBytes(GOTO_TABLE_PADDING);
    }
    
    private static void createMetadataInstruction(InstructionsBuilder builder,
            ByteBuf input) {
        final byte WRITE_METADATA_PADDING = 4;
        input.skipBytes(WRITE_METADATA_PADDING);
        builder.setType(WriteMetadata.class);
        MetadataInstructionBuilder metadataBuilder = new MetadataInstructionBuilder();
        byte[] metadata = new byte[Long.SIZE / Byte.SIZE];
        input.readBytes(metadata);
        metadataBuilder.setMetadata(metadata);
        byte[] metadata_mask = new byte[Long.SIZE / Byte.SIZE];
        input.readBytes(metadata_mask);
        metadataBuilder.setMetadata(metadata_mask);
        builder.addAugmentation(MetadataInstruction.class, metadataBuilder.build());
    }
    
    private static void createActionRelatedInstruction(ByteBuf input,
            InstructionsBuilder builder, int instructionLength) {
        final byte ACTIONS_RELATED_INSTRUCTION_PADDING = 4;
        input.skipBytes(ACTIONS_RELATED_INSTRUCTION_PADDING);
        ActionsInstructionBuilder actionsBuilder = new ActionsInstructionBuilder();
        actionsBuilder.setActionsList(ActionsDeserializer.createActionsList(input, instructionLength));
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
    }

}
