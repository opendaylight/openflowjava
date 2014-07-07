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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;

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
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(24);
        buffer.writeZero(4);
        buffer.writeInt(128);
        buffer.writeZero(4);
        byte[] data = new byte[]{0, 0, 1, 1, 2, 2, 3, 3};
        buffer.writeBytes(data);

        OF13QueueGetConfigReplyExperimenterDeserializer deserializer =
                new OF13QueueGetConfigReplyExperimenterDeserializer();
        QueueProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong property", QueueProperties.OFPQTEXPERIMENTER, message.getProperty());
        ExperimenterQueueProperty exp = message.getAugmentation(ExperimenterQueueProperty.class);
        Assert.assertEquals("Wrong experimenter", 128, exp.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, exp.getData());
    }

    /**
     * Testing of {@link OF13QueueGetConfigReplyExperimenterDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(16);
        buffer.writeZero(4);
        buffer.writeInt(128);
        buffer.writeZero(4);

        OF13QueueGetConfigReplyExperimenterDeserializer deserializer =
                new OF13QueueGetConfigReplyExperimenterDeserializer();
        QueueProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong property", QueueProperties.OFPQTEXPERIMENTER, message.getProperty());
        ExperimenterQueueProperty exp = message.getAugmentation(ExperimenterQueueProperty.class);
        Assert.assertEquals("Wrong experimenter", 128, exp.getExperimenter().intValue());
        Assert.assertNull("Unexpected data", exp.getData());
    }
}
