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

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.RegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.InstructionBase;

/**
 * Serializes ofp_instruction (OpenFlow v 1.3) structure
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF13InstructionsSerializer implements OFSerializer<Instruction>,
        HeaderSerializer<Instruction>, RegistryInjector {

    private static final byte GOTO_TABLE_TYPE = 1;
    private static final byte WRITE_METADATA_TYPE = 2;
    private static final byte WRITE_ACTIONS_TYPE = 3;
    private static final byte APPLY_ACTIONS_TYPE = 4;
    private static final byte CLEAR_ACTIONS_TYPE = 5;
    private static final byte METER_TYPE = 6;
    private static final byte GOTO_TABLE_LENGTH = 8;
    private static final byte WRITE_METADATA_LENGTH = 24;
    private static final byte METER_LENGTH = 8;
    private static final byte ACTIONS_INSTRUCTION_LENGTH = 8;
    private static final byte PADDING_IN_GOTO_TABLE = 3;
    private static final byte PADDING_IN_WRITE_METADATA = 4;
    private static final byte PADDING_IN_CLEAR_ACTIONS = 4;
    private static final byte INSTRUCTION_IDS_LENGTH = 4;
    private static final byte PADDING_IN_ACTIONS_INSTRUCTION = 4;
    private SerializerRegistry registry;

    private static void writeTypeAndLength(ByteBuf out, int type, int length) {
        out.writeShort(type);
        out.writeShort(length);
    }

    private void writeActionsInstruction(ByteBuf out,
            Instruction instruction, int type) {
        int instructionStartIndex = out.writerIndex();
        out.writeShort(type);
        if (instruction.getAugmentation(ActionsInstruction.class) != null) {
            List<Action> actions = instruction.getAugmentation(ActionsInstruction.class).getAction();
            int instructionLengthIndex = out.writerIndex();
            out.writeShort(EncodeConstants.EMPTY_LENGTH);
            ByteBufUtils.padBuffer(PADDING_IN_ACTIONS_INSTRUCTION, out);
            OFSerializer<Action> serializer = registry.getSerializer(
                    new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, Action.class));
            CodingUtils.serializeList(actions, serializer, out);
            int instructionLength = out.writerIndex() - instructionStartIndex;
            out.setShort(instructionLengthIndex, instructionLength);
        } else {
            out.writeShort(ACTIONS_INSTRUCTION_LENGTH);
            ByteBufUtils.padBuffer(PADDING_IN_ACTIONS_INSTRUCTION, out);
        }
    }

    @Override
    public void serialize(Instruction instruction, ByteBuf outBuffer) {
        Class<? extends InstructionBase> type = instruction.getType();
        if (type.isAssignableFrom(GotoTable.class)) {
            writeTypeAndLength(outBuffer, GOTO_TABLE_TYPE, GOTO_TABLE_LENGTH);
            outBuffer.writeByte(instruction.getAugmentation(TableIdInstruction.class).getTableId());
            ByteBufUtils.padBuffer(PADDING_IN_GOTO_TABLE, outBuffer);
        } else if (type.isAssignableFrom(WriteMetadata.class)) {
            writeTypeAndLength(outBuffer, WRITE_METADATA_TYPE, WRITE_METADATA_LENGTH);
            ByteBufUtils.padBuffer(PADDING_IN_WRITE_METADATA, outBuffer);
            MetadataInstruction metadata = instruction.getAugmentation(MetadataInstruction.class);
            outBuffer.writeBytes(metadata.getMetadata());
            outBuffer.writeBytes(metadata.getMetadataMask());
        } else if (type.isAssignableFrom(WriteActions.class)) {
            writeActionsInstruction(outBuffer, instruction, WRITE_ACTIONS_TYPE);
        } else if (type.isAssignableFrom(ApplyActions.class)) {
            writeActionsInstruction(outBuffer, instruction, APPLY_ACTIONS_TYPE);
        } else if (type.isAssignableFrom(ClearActions.class)) {
            writeTypeAndLength(outBuffer, CLEAR_ACTIONS_TYPE, ACTIONS_INSTRUCTION_LENGTH);
            ByteBufUtils.padBuffer(PADDING_IN_CLEAR_ACTIONS, outBuffer);
        } else if (type.isAssignableFrom(Meter.class)) {
            writeTypeAndLength(outBuffer, METER_TYPE, METER_LENGTH);
            outBuffer.writeInt(instruction.getAugmentation(MeterIdInstruction.class).getMeterId().intValue());
        } else if (type.isAssignableFrom(Experimenter.class)) {
            OFSerializer<ExperimenterInstruction> serializer = registry.getSerializer(new MessageTypeKey<>(
                    EncodeConstants.OF13_VERSION_ID, Experimenter.class));
            serializer.serialize((ExperimenterInstruction) instruction, outBuffer);
        }
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
    }

    @Override
    public void serializeHeader(Instruction instruction, ByteBuf outBuffer) {
        Class<? extends InstructionBase> type = instruction.getType();
        if (type.isAssignableFrom(GotoTable.class)) {
            writeTypeAndLength(outBuffer, GOTO_TABLE_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(WriteMetadata.class)) {
            writeTypeAndLength(outBuffer, WRITE_METADATA_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(WriteActions.class)) {
            writeTypeAndLength(outBuffer, WRITE_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(ApplyActions.class)) {
            writeTypeAndLength(outBuffer, APPLY_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(ClearActions.class)) {
            writeTypeAndLength(outBuffer, CLEAR_ACTIONS_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(Meter.class)) {
            writeTypeAndLength(outBuffer, METER_TYPE, INSTRUCTION_IDS_LENGTH);
        } else if (type.isAssignableFrom(Experimenter.class)) {
            HeaderSerializer<ExperimenterInstruction> serializer = registry.getSerializer(new MessageTypeKey<>(
                    EncodeConstants.OF13_VERSION_ID, Experimenter.class));
            serializer.serializeHeader((ExperimenterInstruction) instruction, outBuffer);
        }
    }

}
