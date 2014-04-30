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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.experimenter._case.MultipartReplyExperimenter;

/**
 * @author michal.polkorab
 *
 */
public class OF10StatsReplyVendorDeserializerTest {

    /**
     * Testing of {@link OF10StatsReplyVendorDeserializer} for correct translation into POJO
     * @throws Exception
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(42);
        byte[] data = new byte[]{0, 1, 2, 3, 3, 2, 1, 0};
        buffer.writeBytes(data);

        OF10StatsReplyVendorDeserializer deserializer =
                new OF10StatsReplyVendorDeserializer();
        MultipartReplyExperimenter message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 42, message.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, message.getData());
    }

    /**
     * Testing of {@link OF10StatsReplyVendorDeserializer} for correct translation into POJO
     * @throws Exception
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(42);

        OF10StatsReplyVendorDeserializer deserializer =
                new OF10StatsReplyVendorDeserializer();
        MultipartReplyExperimenter message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 42, message.getExperimenter().intValue());
        Assert.assertNull("Unexpected data", message.getData());
    }
}
