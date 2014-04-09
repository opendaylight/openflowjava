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

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * Deserializes ofp_instruction (OpenFlow v1.3) structures
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class InstructionsDeserializer implements OFDeserializer<Instruction>,
        HeaderDeserializer<Instruction>, DeserializerRegistryInjector {
    
    private static final byte WRITE_APPLY_CLEAR_ACTION_LENGTH = 8;
    private static final byte GOTO_TABLE_PADDING = 3;
    private static final byte WRITE_METADATA_PADDING = 4;
    private static final byte ACTIONS_RELATED_INSTRUCTION_PADDING = 4;
    private DeserializerRegistry registry;

    @Override
    public Instruction deserialize(ByteBuf input) {
        InstructionBuilder builder = new InstructionBuilder();
        int type = input.readUnsignedShort();
        switch (type) {
        case 1:
            createGotoTableInstruction(builder, input);
            break;
        case 2:
            createMetadataInstruction(builder, input);
            break;
        case 3:
            builder.setType(WriteActions.class);
            createActionRelatedInstruction(input, builder);
            break;
        case 4:
            builder.setType(ApplyActions.class);
            createActionRelatedInstruction(input, builder);
            break;
        case 5:
            builder.setType(ClearActions.class);
            createActionRelatedInstruction(input, builder);
            break;
        case 6:
            builder.setType(Meter.class);
            input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            MeterIdInstructionBuilder meterBuilder = new MeterIdInstructionBuilder();
            meterBuilder.setMeterId(input.readUnsignedInt());
            builder.addAugmentation(MeterIdInstruction.class, meterBuilder.build());
            break;
        case 65535:
            builder.setType(Experimenter.class);
            OFDeserializer<ExperimenterInstruction> deserializer = registry.getDeserializer(
                    new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, type, ExperimenterInstruction.class));
            ExperimenterInstruction expInstruction = deserializer.deserialize(input);
            builder.addAugmentation(ExperimenterInstruction.class, expInstruction);
            break;
        default:
            break;
        }
        return builder.build();
    }

    @Override
    public Instruction deserializeHeader(ByteBuf input) {
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
        case 65535:
            builder.setType(Experimenter.class);
            ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
            expBuilder.setExperimenter(input.readUnsignedInt());
            builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
            break;
        default:
            break;
        }
        return builder.build();
    }

    private static void createGotoTableInstruction(InstructionBuilder builder,
            ByteBuf input) {
        builder.setType(GotoTable.class);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        TableIdInstructionBuilder tableBuilder = new TableIdInstructionBuilder();
        tableBuilder.setTableId(input.readUnsignedByte());
        builder.addAugmentation(TableIdInstruction.class, tableBuilder.build());
        input.skipBytes(GOTO_TABLE_PADDING);
    }
    
    private static void createMetadataInstruction(InstructionBuilder builder,
            ByteBuf input) {
        builder.setType(WriteMetadata.class);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        input.skipBytes(WRITE_METADATA_PADDING);
        MetadataInstructionBuilder metadataBuilder = new MetadataInstructionBuilder();
        byte[] metadata = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(metadata);
        metadataBuilder.setMetadata(metadata);
        byte[] metadata_mask = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(metadata_mask);
        metadataBuilder.setMetadataMask(metadata_mask);
        builder.addAugmentation(MetadataInstruction.class, metadataBuilder.build());
    }
    
    private void createActionRelatedInstruction(ByteBuf input, InstructionBuilder builder) {
        int instructionLength = input.readUnsignedShort();
        input.skipBytes(ACTIONS_RELATED_INSTRUCTION_PADDING);
        ActionsInstructionBuilder actionsBuilder = new ActionsInstructionBuilder();
        OFDeserializer<Action> deserializer = registry.getDeserializer(new MessageCodeKey(
                EncodeConstants.OF13_VERSION_ID, EncodeConstants.EMPTY_VALUE, Action.class));
        List<Action> actions = DecodingUtils.deserializeActions(
                instructionLength - WRITE_APPLY_CLEAR_ACTION_LENGTH, input, deserializer, false);
        actionsBuilder.setAction(actions);
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        registry = deserializerRegistry;
    }
}
