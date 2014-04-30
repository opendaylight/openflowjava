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
import org.opendaylight.openflowjava.protocol.ext.util.ExtBufferUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueueProperty;

/**
 * @author michal.polkorab
 *
 */
public class OF13QueueGetConfigReplyExperimenterDeserializerTest {

    /**
     * Testing of {@link OF13QueueGetConfigReplyExperimenterDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(128);
        ExtBufferUtils.padBuffer(4, buffer);
        byte[] data = new byte[]{0, 0, 1, 1, 2, 2, 3, 3};
        buffer.writeBytes(data);

        OF13QueueGetConfigReplyExperimenterDeserializer deserializer =
                new OF13QueueGetConfigReplyExperimenterDeserializer();
        ExperimenterQueueProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 128, message.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, message.getData());
    }

    /**
     * Testing of {@link OF13QueueGetConfigReplyExperimenterDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(128);
        ExtBufferUtils.padBuffer(4, buffer);

        OF13QueueGetConfigReplyExperimenterDeserializer deserializer =
                new OF13QueueGetConfigReplyExperimenterDeserializer();
        ExperimenterQueueProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 128, message.getExperimenter().intValue());
        Assert.assertNull("Unexpected data", message.getData());
    }
}
