/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.InstructionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterInstructionSerializerTest {

    /**
     * Testing of {@link OF13ExperimenterInstructionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(Experimenter.class);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(142L);
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        expBuilder.setData(data);
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        Instruction instruction = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13ExperimenterInstructionSerializer serializer = new OF13ExperimenterInstructionSerializer();
        serializer.serialize(instruction, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 142, buffer.readUnsignedInt());
        byte[] expData = new byte[8];
        buffer.readBytes(expData);
        Assert.assertArrayEquals("Wrong experimenter data", data, expData);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(Experimenter.class);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(142L);
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        Instruction instruction = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13ExperimenterInstructionSerializer serializer = new OF13ExperimenterInstructionSerializer();
        serializer.serialize(instruction, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 8, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 142, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testHeader() {
        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(Experimenter.class);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(142L);
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        expBuilder.setData(data);
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        Instruction instruction = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13ExperimenterInstructionSerializer serializer = new OF13ExperimenterInstructionSerializer();
        serializer.serializeHeader(instruction, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 8, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 142, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testHeaderWithoutData() {
        InstructionBuilder builder = new InstructionBuilder();
        builder.setType(Experimenter.class);
        ExperimenterInstructionBuilder expBuilder = new ExperimenterInstructionBuilder();
        expBuilder.setExperimenter(142L);
        builder.addAugmentation(ExperimenterInstruction.class, expBuilder.build());
        Instruction instruction = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13ExperimenterInstructionSerializer serializer = new OF13ExperimenterInstructionSerializer();
        serializer.serializeHeader(instruction, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 8, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 142, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }
}
