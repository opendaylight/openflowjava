/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionsInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MeterIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TableIdInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class InstructionsDeserializerTest {


    private DeserializerRegistry registry;

    /**
     * Initializes deserializer registry and lookups correct deserializer
     */
    @Before
    public void startUp() {
        registry = new DeserializerRegistryImpl();
        registry.init();
    }

    /**
     * Testing instructions translation
     */
    @Test
    public void test() {
        ByteBuf message = BufferHelper.buildBuffer("00 01 00 08 0A 00 00 00 00 02 00 18 00 00 00 00 "
                + "00 00 00 00 00 00 00 20 00 00 00 00 00 00 00 30 00 05 00 08 00 00 00 00 00 06 00 08 "
                + "00 01 02 03 00 03 00 20 00 00 00 00 00 00 00 10 00 00 00 25 00 35 00 00 00 00 00 00 "
                + "00 16 00 08 00 00 00 50 00 04 00 18 00 00 00 00 00 15 00 08 00 00 00 25 00 0F 00 08 05 00 00 00");
        
        message.skipBytes(4); // skip XID
        
        CodeKeyMaker keyMaker = CodeKeyMakerFactory.createInstructionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
        List<Instruction> instructions = ListDeserializer.deserializeList(EncodeConstants.OF13_VERSION_ID,
                message.readableBytes(), message, keyMaker, registry);
        Instruction i1 = instructions.get(0);
        Assert.assertEquals("Wrong type - i1", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.GotoTable", i1.getType().getName());
        Assert.assertEquals("Wrong table-id - i1", 10, i1.getAugmentation(TableIdInstruction.class).getTableId().intValue());
        Instruction i2 = instructions.get(1);
        Assert.assertEquals("Wrong type - i2", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.WriteMetadata", i2.getType().getName());
        Assert.assertArrayEquals("Wrong metadata - i2", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 20"),
                i2.getAugmentation(MetadataInstruction.class).getMetadata());
        Assert.assertArrayEquals("Wrong metadata-mask - i2", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 30"),
                i2.getAugmentation(MetadataInstruction.class).getMetadataMask());
        Instruction i3 = instructions.get(2);
        Assert.assertEquals("Wrong type - i3", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.ClearActions", i3.getType().getName());
        Assert.assertEquals("Wrong instructions - i3", 0, i3.getAugmentation(ActionsInstruction.class).getAction().size());
        Instruction i4 = instructions.get(3);
        Assert.assertEquals("Wrong type - i4", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.Meter", i4.getType().getName());
        Assert.assertEquals("Wrong meterId - i4", 66051, i4.getAugmentation(MeterIdInstruction.class).getMeterId().intValue());
        Instruction i5 = instructions.get(4);
        Assert.assertEquals("Wrong type - i5", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.WriteActions", i5.getType().getName());
        Assert.assertEquals("Wrong instructions - i5", 2, i5.getAugmentation(ActionsInstruction.class).getAction().size());
        Action action1 = i5.getAugmentation(ActionsInstruction.class).getAction().get(0);
        Assert.assertEquals("Wrong action", "org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output",
                action1.getType().getName());
        Assert.assertEquals("Wrong action", 37, action1.getAugmentation(PortAction.class).getPort().getValue().intValue());
        Assert.assertEquals("Wrong action", 53, action1.getAugmentation(MaxLengthAction.class).getMaxLength().intValue());
        Action action2 = i5.getAugmentation(ActionsInstruction.class).getAction().get(1);
        Assert.assertEquals("Wrong action", "org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Group",
                action2.getType().getName());
        Assert.assertEquals("Wrong action", 80, action2.getAugmentation(GroupIdAction.class).getGroupId().intValue());
        Instruction i6 = instructions.get(5);
        Assert.assertEquals("Wrong type - i6", "org.opendaylight.yang.gen.v1.urn."
                + "opendaylight.openflow.common.instruction.rev130731.ApplyActions", i6.getType().getName());
        Assert.assertEquals("Wrong instructions - i6", 2, i6.getAugmentation(ActionsInstruction.class).getAction().size());
        action1 = i6.getAugmentation(ActionsInstruction.class).getAction().get(0);
        Assert.assertEquals("Wrong action", "org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetQueue",
                action1.getType().getName());
        Assert.assertEquals("Wrong action", 37, action1.getAugmentation(QueueIdAction.class).getQueueId().intValue());
        action2 = i6.getAugmentation(ActionsInstruction.class).getAction().get(1);
        Assert.assertEquals("Wrong action", "org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetMplsTtl",
                action2.getType().getName());
        Assert.assertEquals("Wrong action", 5, action2.getAugmentation(MplsTtlAction.class).getMplsTtl().intValue());
    }

}
