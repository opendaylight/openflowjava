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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterError;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterErrorMessageDeserializerTest {

    /**
     * Testing of {@link OF13ExperimenterErrorMessageDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(35000);
        buffer.writeInt(70000);
        byte[] data = new byte[]{5, 5, 5, 6, 7, 8, 9, 10};
        buffer.writeBytes(data);

        OF13ExperimenterErrorMessageDeserializer deserializer =
                new OF13ExperimenterErrorMessageDeserializer();
        ExperimenterError message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong exp-type", 35000, message.getExpType().intValue());
        Assert.assertEquals("Wrong experimenter", 70000, message.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, message.getExpData());
    }

    /**
     * Testing of {@link OF13ExperimenterErrorMessageDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(35000);
        buffer.writeInt(70000);

        OF13ExperimenterErrorMessageDeserializer deserializer =
                new OF13ExperimenterErrorMessageDeserializer();
        ExperimenterError message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong exp-type", 35000, message.getExpType().intValue());
        Assert.assertEquals("Wrong experimenter", 70000, message.getExperimenter().intValue());
        Assert.assertNull("Unexpected data", message.getExpData());
    }
}
