/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.actions.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInputBuilder;

/**
 * @author timotej.kubas
 *
 */
public class PacketOutInputMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 13;
    private static final byte PADDING_IN_PACKET_OUT_MESSAGE = 6;
    private static final int PADDING_IN_ACTION_HEADER = 4;
       
    /**
     * Testing of {@link PacketOutInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testPacketOutInputMessage() throws Exception {
        PacketOutInputBuilder builder = new PacketOutInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setBufferId(256L);
        builder.setInPort(new PortNumber(256L));
        List<ActionsList> actions = new ArrayList<>();
        ActionsListBuilder actionsListBuilder = new ActionsListBuilder();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(PushVlan.class);
        EthertypeActionBuilder ethertypeBuilder = new EthertypeActionBuilder();
        ethertypeBuilder.setEthertype(new EtherType(25));
        actionBuilder.addAugmentation(EthertypeAction.class, ethertypeBuilder.build());
        actionsListBuilder.setAction(actionBuilder.build());
        actions.add(actionsListBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PopVlan.class);
        actionsListBuilder.setAction(actionBuilder.build());
        actions.add(actionsListBuilder.build());
        builder.setActionsList(actions);
        builder.setData(ByteBufUtils.hexStringToBytes("00 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14"));
        PacketOutInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        PacketOutInputMessageFactory factory = PacketOutInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, 56);
        Assert.assertEquals("Wrong BufferId", message.getBufferId().longValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong PortNumber", message.getInPort().getValue().longValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong ActionsLength", 16, out.readUnsignedShort());
        out.skipBytes(PADDING_IN_PACKET_OUT_MESSAGE);
        Assert.assertEquals("Wrong action type", 17, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong ethertype", 25, out.readUnsignedShort());
        out.skipBytes(Short.SIZE / Byte.SIZE);
        Assert.assertEquals("Wrong action type", 18, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(PADDING_IN_ACTION_HEADER);
        Assert.assertArrayEquals("Wrong data", message.getData(), out.readBytes(out.readableBytes()).array());
    }
}
