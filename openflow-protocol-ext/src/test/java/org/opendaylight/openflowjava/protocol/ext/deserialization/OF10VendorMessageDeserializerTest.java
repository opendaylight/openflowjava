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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;

/**
 * @author michal.polkorab
 *
 */
public class OF10VendorMessageDeserializerTest {

    /**
     * Testing of {@link OF10VendorMessageDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(12345);
        buffer.writeInt(98765);
        byte[] data = new byte[]{5, 6, 7, 8};
        buffer.writeBytes(data);

        OF10VendorMessageDeserializer deserializer =
                new OF10VendorMessageDeserializer();
        ExperimenterMessage message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong version", 1, message.getVersion().shortValue());
        Assert.assertEquals("Wrong XID", 12345, message.getXid().intValue());
        Assert.assertEquals("Wrong experimenter", 98765, message.getExperimenter().intValue());
        Assert.assertArrayEquals("Wrong data", data, message.getData());
    }

    /**
     * Testing of {@link OF10VendorMessageDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(12345);
        buffer.writeInt(98765);

        OF10VendorMessageDeserializer deserializer =
                new OF10VendorMessageDeserializer();
        ExperimenterMessage message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong version", 1, message.getVersion().shortValue());
        Assert.assertEquals("Wrong XID", 12345, message.getXid().intValue());
        Assert.assertEquals("Wrong experimenter", 98765, message.getExperimenter().intValue());
        Assert.assertNull("Unexpected data", message.getData());
    }
}
