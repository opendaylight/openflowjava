/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenterBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13MultipartExperimenterSerializerTest {

    /**
     * Testing of {@link OF10StatsRequestVendorSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        MultipartRequestExperimenterBuilder expBuilder = new MultipartRequestExperimenterBuilder();
        expBuilder.setExperimenter(42L);
        expBuilder.setExpType(84L);
        byte[] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
        expBuilder.setData(data);
        MultipartRequestExperimenter message = expBuilder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13MultipartExperimenterSerializer serializer = new OF13MultipartExperimenterSerializer();
        serializer.serialize(message, buffer);

        Assert.assertEquals("Wrong experimenter", 42, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 84, buffer.readUnsignedInt());
        byte[] expData = new byte[8];
        buffer.readBytes(expData);
        Assert.assertArrayEquals("Wrong experimenter data", data, expData);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF10StatsRequestVendorSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        MultipartRequestExperimenterBuilder expBuilder = new MultipartRequestExperimenterBuilder();
        expBuilder.setExperimenter(42L);
        expBuilder.setExpType(84L);
        MultipartRequestExperimenter message = expBuilder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13MultipartExperimenterSerializer serializer = new OF13MultipartExperimenterSerializer();
        serializer.serialize(message, buffer);

        Assert.assertEquals("Wrong experimenter", 42, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong exp-type", 84, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }
}
