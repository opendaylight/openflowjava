/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.StripVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10PacketOutInputMessageFactoryTest {

    /**
     * Testing of {@link OF10PacketOutInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testPacketOutInputMessage() throws Exception {
        PacketOutInputBuilder builder = new PacketOutInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setBufferId(256L);
        builder.setInPort(new PortNumber(257L));
        List<ActionsList> actions = new ArrayList<>();
        ActionsListBuilder actionsListBuilder = new ActionsListBuilder();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber((long) 42));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(50);
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actionsListBuilder.setAction(actionBuilder.build());
        actions.add(actionsListBuilder.build());
        actionsListBuilder = new ActionsListBuilder();
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(StripVlan.class);
        actionsListBuilder.setAction(actionBuilder.build());
        builder.setActionsList(actions);
        actions.add(actionsListBuilder.build());
        builder.setActionsList(actions);
        builder.setData(ByteBufUtils.hexStringToBytes("00 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14"));
        PacketOutInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10PacketOutInputMessageFactory factory = OF10PacketOutInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV10(out, (byte) 13, 48);
        Assert.assertEquals("Wrong BufferId", 256, out.readUnsignedInt());
        Assert.assertEquals("Wrong PortNumber", 257, out.readUnsignedShort());
        Assert.assertEquals("Wrong actions length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 42, out.readUnsignedShort());
        Assert.assertEquals("Wrong maxlength", 50, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertArrayEquals("Wrong data", message.getData(), out.readBytes(out.readableBytes()).array());
        Assert.assertTrue("Unread data", out.readableBytes() == 0);
    }

}
