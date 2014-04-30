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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;

/**
 * @author michal.polkorab
 *
 */
public class OF13TableFeaturesExperimenterDeserializerTest {

    /**
     * Testing of {@link OF13TableFeaturesExperimenterDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(256);
        buffer.writeInt(546);
        byte[] data = new byte[]{1, 2, 3, 4};
        buffer.writeBytes(data);

        OF13TableFeaturesExperimenterDeserializer deserializer =
                new OF13TableFeaturesExperimenterDeserializer();
        ExperimenterRelatedTableFeatureProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 256, message.getExperimenter().intValue());
        Assert.assertEquals("Wrong exp-type", 546, message.getExpType().intValue());
        Assert.assertArrayEquals("Wrong data", data, message.getData());
    }

    /**
     * Testing of {@link OF13TableFeaturesExperimenterDeserializer} for correct translation into POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutData() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(256);
        buffer.writeInt(546);

        OF13TableFeaturesExperimenterDeserializer deserializer =
                new OF13TableFeaturesExperimenterDeserializer();
        ExperimenterRelatedTableFeatureProperty message = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong experimenter", 256, message.getExperimenter().intValue());
        Assert.assertEquals("Wrong exp-type", 546, message.getExpType().intValue());
        Assert.assertNull("Unexpected data", message.getData());
    }
}
