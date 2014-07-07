/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterInstructionDeserializerTest {

    /**
     * Testing of {@link OF13ExperimenterInstructionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(16);
        buffer.writeInt(42);
        byte[] data = new byte[]{0, 1, 2, 3, 4, 0, 0, 0};
        buffer.writeBytes(data);

        OF13ExperimenterInstructionDeserializer deserializer =
                new OF13ExperimenterInstructionDeserializer();
        Instruction instruction = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, instruction.getType());
        ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);

        OF13ExperimenterInstructionDeserializer deserializer =
                new OF13ExperimenterInstructionDeserializer();
        Instruction instruction = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, instruction.getType());
        ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testHeader() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);

        OF13ExperimenterInstructionDeserializer deserializer =
                new OF13ExperimenterInstructionDeserializer();
        Instruction instruction = deserializer.deserializeHeader(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, instruction.getType());
        ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterInstructionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testHeaderWithData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);
        byte[] data = new byte[]{0, 1, 2, 3, 4, 0, 0, 0};
        buffer.writeBytes(data);

        OF13ExperimenterInstructionDeserializer deserializer =
                new OF13ExperimenterInstructionDeserializer();
        Instruction instruction = deserializer.deserializeHeader(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, instruction.getType());
        ExperimenterInstruction experimenter = instruction.getAugmentation(ExperimenterInstruction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }
}
