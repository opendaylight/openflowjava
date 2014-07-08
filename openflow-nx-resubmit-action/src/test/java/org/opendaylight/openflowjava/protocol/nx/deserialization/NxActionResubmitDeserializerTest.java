/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx.deserialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmitAugment;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitDeserializerTest {

    /**
     * Test deserialization of NX_ACTION_RESUBMIT action
     */
    @Test
    public void test() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        buffer.writeShort(16);
        buffer.writeInt(12345);
        buffer.writeShort(1);
        buffer.writeShort(42);
        buffer.writeByte(5);
        buffer.writeZero(3);

        NxActionResubmitDeserializer deserializer = new NxActionResubmitDeserializer();
        Action action = deserializer.deserialize(buffer);

        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.Experimenter", action.getType().getName());
        ExperimenterIdAction expAction = action.getAugmentation(ExperimenterIdAction.class);
        Assert.assertEquals("Wrong vendor id", 12345, expAction.getExperimenter().getValue().intValue());
        NxActionResubmitAugment nxResubmit = action.getAugmentation(NxActionResubmitAugment.class);
        Assert.assertEquals("Wrong subtype", 1, nxResubmit.getSubtype().intValue());
        Assert.assertEquals("Wrong in-port", 42, nxResubmit.getInPort().intValue());
        Assert.assertEquals("Wrong table", 5, nxResubmit.getTable().intValue());
        Assert.assertTrue("Unread data", buffer.readableBytes() == 0);
    }
}