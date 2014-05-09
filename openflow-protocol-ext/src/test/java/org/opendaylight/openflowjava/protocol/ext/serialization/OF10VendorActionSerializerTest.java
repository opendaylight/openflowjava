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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10VendorActionSerializerTest {

    /**
     * Testing of {@link OF10VendorActionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testVendorActionWithData() {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(82L);
        byte[] expData = new byte[]{0, 0, 0, 0, 0, 0, 0, 1};
        expBuilder.setData(expData);
        actionBuilder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        Action action = actionBuilder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10VendorActionSerializer serializer = new OF10VendorActionSerializer();
        serializer.serialize(action, buffer);
        
        Assert.assertEquals("Wrong action type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 82, buffer.readUnsignedInt());
        byte[] tmp = new byte[8];
        buffer.readBytes(tmp);
        Assert.assertArrayEquals("Wrong data", expData, tmp);
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Testing of {@link OF10VendorActionSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testVendorActionWithoutData() {
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(102L);
        actionBuilder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        Action action = actionBuilder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10VendorActionSerializer serializer = new OF10VendorActionSerializer();
        serializer.serialize(action, buffer);

        Assert.assertEquals("Wrong action type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 102, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

}
