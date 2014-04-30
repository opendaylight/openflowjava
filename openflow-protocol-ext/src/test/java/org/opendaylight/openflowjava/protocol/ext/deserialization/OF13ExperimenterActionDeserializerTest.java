/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
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
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterActionDeserializerTest {

    /**
     * Testing of {@link OF13ExperimenterActionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(16);
        buffer.writeInt(42);
        byte[] data = new byte[]{0, 1, 2, 3, 4, 0, 0, 0};
        buffer.writeBytes(data);

        OF13ExperimenterActionDeserializer deserializer =
                new OF13ExperimenterActionDeserializer();
        Action action = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, action.getType());
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterActionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);

        OF13ExperimenterActionDeserializer deserializer =
                new OF13ExperimenterActionDeserializer();
        Action action = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, action.getType());
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterActionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testHeader() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);

        OF13ExperimenterActionDeserializer deserializer =
                new OF13ExperimenterActionDeserializer();
        Action action = deserializer.deserializeHeader(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, action.getType());
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }

    /**
     * Testing of {@link OF13ExperimenterActionDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testHeaderWithData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(8);
        buffer.writeInt(42);
        byte[] data = new byte[]{0, 1, 2, 3, 4, 0, 0, 0};
        buffer.writeBytes(data);

        OF13ExperimenterActionDeserializer deserializer =
                new OF13ExperimenterActionDeserializer();
        Action action = deserializer.deserializeHeader(buffer);

        Assert.assertEquals("Wrong type", Experimenter.class, action.getType());
        ExperimenterAction experimenter = action.getAugmentation(ExperimenterAction.class);
        Assert.assertEquals("Wrong experimenter", 42, experimenter.getExperimenter().intValue());
        Assert.assertNull("Data not null", experimenter.getData());
    }
}
