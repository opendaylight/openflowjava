/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmit;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmitBuilder;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitSerializerTest {

    /**
     * Test serialization of NX_ACTION_RESUBMIT action
     */
    @Test
    public void test() {
        ActionBuilder builder = new ActionBuilder();
        builder.setType(Experimenter.class);
        NxActionResubmitBuilder nxBuilder = new NxActionResubmitBuilder();
        nxBuilder.setVendor(12345L);
        nxBuilder.setSubtype(1);
        nxBuilder.setInPort(42);
        nxBuilder.setTable((short) 5);
        builder.addAugmentation(NxActionResubmit.class, nxBuilder.build());
        Action action = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        NxActionResubmitSerializer serializer = new NxActionResubmitSerializer();
        serializer.serialize(action, buffer);

        Assert.assertEquals("Wrong action type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong vendor id", 12345, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong subtype", 1, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong in-port", 42, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong table", 5, buffer.readUnsignedByte());
        buffer.skipBytes(3);
        Assert.assertTrue("Unread data", buffer.readableBytes() == 0);
    }

    /**
     * Test serialization of NX_ACTION_RESUBMIT action
     */
    @Test
    public void testHeaderSerialization() {
        ActionBuilder builder = new ActionBuilder();
        builder.setType(Experimenter.class);
        NxActionResubmitBuilder nxBuilder = new NxActionResubmitBuilder();
        nxBuilder.setVendor(12345L);
        builder.addAugmentation(NxActionResubmit.class, nxBuilder.build());
        Action action = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        NxActionResubmitSerializer serializer = new NxActionResubmitSerializer();
        serializer.serializeHeader(action, buffer);

        Assert.assertEquals("Wrong action type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong length", 8, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong vendor id", 12345, buffer.readUnsignedInt());
        Assert.assertTrue("Unread data", buffer.readableBytes() == 0);
    }
}