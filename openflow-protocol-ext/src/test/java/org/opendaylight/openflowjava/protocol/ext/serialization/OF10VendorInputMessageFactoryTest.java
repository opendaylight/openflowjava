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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10VendorInputMessageFactoryTest {

    /**
     * Testing of {@link OF10VendorInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        builder.setVersion((short) 1);
        builder.setXid(1024L);
        builder.setExperimenter(0x0001020304L);
        byte[] data = new byte[]{5, 6, 7, 8};
        builder.setData(data);
        ExperimenterInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10VendorInputMessageFactory vendorFactory = new OF10VendorInputMessageFactory();
        vendorFactory.serialize(message, out);
        
        Assert.assertEquals("Wrong version", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong type", 4, out.readUnsignedByte());
        Assert.assertEquals("Wrong length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong xid", 1024, out.readUnsignedInt());
        Assert.assertEquals("Wrong experimenter", 0x0001020304L, out.readUnsignedInt());
        byte[] expData = new byte[4];
        out.readBytes(expData);
        Assert.assertArrayEquals("Wrong data", data, expData);
        Assert.assertTrue("Unexpected data", out.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF10VendorInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() throws Exception {
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        builder.setVersion((short) 1);
        builder.setXid(1024L);
        builder.setExperimenter(0x0001020304L);
        ExperimenterInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10VendorInputMessageFactory vendorFactory = new OF10VendorInputMessageFactory();
        vendorFactory.serialize(message, out);
        
        Assert.assertEquals("Wrong version", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong type", 4, out.readUnsignedByte());
        Assert.assertEquals("Wrong length", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong xid", 1024, out.readUnsignedInt());
        Assert.assertEquals("Wrong experimenter", 0x0001020304L, out.readUnsignedInt());
        Assert.assertTrue("Unexpected data", out.readableBytes() == 0);
    }
}