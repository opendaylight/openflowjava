/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;




import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;

/**
 * @author michal.polkorab
 *
 */
public class InstructionsSerializerTest {

    /**
     * Testing instructions translation
     */
    @Test
    public void test() {
        List<Instructions> instructions = new ArrayList<>();
        // Goto_table instruction
        InstructionsBuilder builder = new InstructionsBuilder();
        builder.setType(GotoTable.class);
        TableIdInstructionBuilder tableIdBuilder = new TableIdInstructionBuilder();
        tableIdBuilder.setTableId((short) 5);
        builder.addAugmentation(TableIdInstruction.class, tableIdBuilder.build());
        instructions.add(builder.build());
        builder = new InstructionsBuilder();
        // Write_metadata instruction
        builder.setType(WriteMetadata.class);
        MetadataInstructionBuilder metaBuilder = new MetadataInstructionBuilder();
        metaBuilder.setMetadata(ByteBufUtils.hexStringToBytes("00 01 02 03 04 05 06 07"));
        metaBuilder.setMetadataMask(ByteBufUtils.hexStringToBytes("07 06 05 04 03 02 01 00"));
        builder.addAugmentation(MetadataInstruction.class, metaBuilder.build());
        instructions.add(builder.build());
        // Clear_actions instruction
        builder = new InstructionsBuilder();
        builder.setType(ClearActions.class);
        instructions.add(builder.build());
        // Meter instruction
        builder = new InstructionsBuilder();
        builder.setType(Meter.class);
        MeterIdInstructionBuilder meterBuilder = new MeterIdInstructionBuilder();
        meterBuilder.setMeterId(42L);
        builder.addAugmentation(MeterIdInstruction.class, meterBuilder.build());
        instructions.add(builder.build());
        // Write_actions instruction
        builder = new InstructionsBuilder();
        builder.setType(WriteActions.class);
        ActionsInstructionBuilder actionsBuilder = new ActionsInstructionBuilder();
        List<ActionsList> actions = new ArrayList<>();
        ActionsListBuilder listBuilder = new ActionsListBuilder();
        ActionBuilder actBuilder = new ActionBuilder();
        actBuilder.setType(Output.class);
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(45L));
        actBuilder.addAugmentation(PortAction.class, portBuilder.build());
        MaxLengthActionBuilder maxBuilder = new MaxLengthActionBuilder();
        maxBuilder.setMaxLength(55);
        actBuilder.addAugmentation(MaxLengthAction.class, maxBuilder.build());
        listBuilder.setAction(actBuilder.build());
        actions.add(listBuilder.build());
        listBuilder = new ActionsListBuilder();
        actBuilder = new ActionBuilder();
        actBuilder.setType(SetNwTtl.class);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl((short) 64);
        actBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        listBuilder.setAction(actBuilder.build());
        actions.add(listBuilder.build());
        actionsBuilder.setActionsList(actions);
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
        instructions.add(builder.build());
        // Apply_actions instruction
        builder = new InstructionsBuilder();
        builder.setType(ApplyActions.class);
        actionsBuilder = new ActionsInstructionBuilder();
        actions = new ArrayList<>();
        listBuilder = new ActionsListBuilder();
        actBuilder = new ActionBuilder();
        actBuilder.setType(PushVlan.class);
        EthertypeActionBuilder ethertypeBuilder = new EthertypeActionBuilder();
        ethertypeBuilder.setEthertype(new EtherType(14));
        actBuilder.addAugmentation(EthertypeAction.class, ethertypeBuilder.build());
        listBuilder.setAction(actBuilder.build());
        actions.add(listBuilder.build());
        listBuilder = new ActionsListBuilder();
        actBuilder = new ActionBuilder();
        actBuilder.setType(PopPbb.class);
        listBuilder.setAction(actBuilder.build());
        actions.add(listBuilder.build());
        actionsBuilder.setActionsList(actions);
        builder.addAugmentation(ActionsInstruction.class, actionsBuilder.build());
        instructions.add(builder.build());
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        InstructionsSerializer.encodeInstructions(instructions, out);
        
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction table-id", 5, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong instruction type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 24, out.readUnsignedShort());
        out.skipBytes(4);
        byte[] actual = new byte[8];
        out.readBytes(actual);
        Assert.assertEquals("Wrong instruction metadata", "00 01 02 03 04 05 06 07",
                ByteBufUtils.bytesToHexString(actual));
        actual = new byte[8];
        out.readBytes(actual);
        Assert.assertEquals("Wrong instruction metadata-mask", "07 06 05 04 03 02 01 00",
                ByteBufUtils.bytesToHexString(actual));
        Assert.assertEquals("Wrong instruction type", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong instruction type", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction meter-id", 42, out.readUnsignedInt());
        Assert.assertEquals("Wrong instruction type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 32, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 45, out.readUnsignedInt());
        Assert.assertEquals("Wrong action type", 55, out.readUnsignedShort());
        out.skipBytes(6);
        Assert.assertEquals("Wrong action type", 23, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 64, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong instruction type", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 24, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 17, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action ethertype", 14, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 27, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertTrue("Not all data were read", out.readableBytes() == 0);
    }

}
