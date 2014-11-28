/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.instruction;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;

/**
 * @author michal.polkorab
 *
 */
public class AbstractInstructionDeserializerTest {

    /**
     * Tests {@link AbstractInstructionDeserializer#deserializeHeader(ByteBuf)} with different
     * instruction types
     */
    @Test(expected=IllegalStateException.class)
    public void test() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("00 01 00 04");
        GoToTableInstructionDeserializer deserializer = new GoToTableInstructionDeserializer();
        Instruction instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", GotoTable.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 02 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", WriteMetadata.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 03 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", WriteActions.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 04 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", ApplyActions.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 05 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", ClearActions.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 06 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong type", Meter.class, instruction.getType());

        buffer = ByteBufUtils.hexStringToByteBuf("00 00 00 04");
        instruction = deserializer.deserializeHeader(buffer);
        // exception expected
    }
}