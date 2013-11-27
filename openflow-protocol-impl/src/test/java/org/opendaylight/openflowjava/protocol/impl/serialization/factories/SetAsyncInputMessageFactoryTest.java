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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.FlowRemovedMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.FlowRemovedMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PacketInMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PacketInMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PortStatusMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PortStatusMaskBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class SetAsyncInputMessageFactoryTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SetAsyncInputMessageFactoryTest.class);
    /**
     * @throws Exception 
     * Testing of {@link SetAsyncInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testSetAsyncInputMessage() throws Exception {
        SetAsyncInputBuilder builder = new SetAsyncInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setPacketInMask(createPacketInMask());
        builder.setPortStatusMask(createPortStatusMask());
        builder.setFlowRemovedMask(createFlowRemowedMask());
        SetAsyncInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        SetAsyncInputMessageFactory factory = SetAsyncInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        LOGGER.debug("<< " + ByteBufUtils.byteBufToHexString(out));
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong packetInMask", 5, out.readUnsignedInt());
        Assert.assertEquals("Wrong packetInMask", 7, out.readUnsignedInt());
        Assert.assertEquals("Wrong portStatusMask", 6, out.readUnsignedInt());
        Assert.assertEquals("Wrong portStatusMask", 0, out.readUnsignedInt());
        Assert.assertEquals("Wrong flowRemovedMask", 10, out.readUnsignedInt());
        Assert.assertEquals("Wrong flowRemovedMask", 5, out.readUnsignedInt());
        
    }
    
    private static List<PacketInMask> createPacketInMask() {
        List<PacketInMask> masks = new ArrayList<>();
        PacketInMaskBuilder builder;
        // OFPCR_ROLE_EQUAL or OFPCR_ROLE_MASTER
        builder = new PacketInMaskBuilder();
        List<PacketInReason> packetInReasonList = new ArrayList<>();
        packetInReasonList.add(PacketInReason.OFPRNOMATCH);
        packetInReasonList.add(PacketInReason.OFPRINVALIDTTL);
        builder.setMask(packetInReasonList);
        masks.add(builder.build());
        // OFPCR_ROLE_SLAVE
        builder = new PacketInMaskBuilder();
        packetInReasonList = new ArrayList<>();
        packetInReasonList.add(PacketInReason.OFPRNOMATCH);
        packetInReasonList.add(PacketInReason.OFPRACTION);
        packetInReasonList.add(PacketInReason.OFPRINVALIDTTL);
        builder.setMask(packetInReasonList);
        masks.add(builder.build());
        System.out.println(masks.size());
        return masks;
    }
    
    private static List<PortStatusMask> createPortStatusMask() {
        List<PortStatusMask> masks = new ArrayList<>();
        PortStatusMaskBuilder builder;
        builder = new PortStatusMaskBuilder();
        // OFPCR_ROLE_EQUAL or OFPCR_ROLE_MASTER
        List<PortReason> portReasonList = new ArrayList<>();
        portReasonList.add(PortReason.OFPPRDELETE);
        portReasonList.add(PortReason.OFPPRMODIFY);
        builder.setMask(portReasonList);
        masks.add(builder.build());
        // OFPCR_ROLE_SLAVE
        builder = new PortStatusMaskBuilder();
        portReasonList = new ArrayList<>();
        builder.setMask(portReasonList);
        masks.add(builder.build());
        return masks;
    }
    
    private static List<FlowRemovedMask> createFlowRemowedMask() {
        List<FlowRemovedMask> masks = new ArrayList<>();
        FlowRemovedMaskBuilder builder;
        // OFPCR_ROLE_EQUAL or OFPCR_ROLE_MASTER
        builder = new FlowRemovedMaskBuilder();
        List<FlowRemovedReason> flowRemovedReasonList = new ArrayList<>();
        flowRemovedReasonList.add(FlowRemovedReason.OFPRRHARDTIMEOUT);
        flowRemovedReasonList.add(FlowRemovedReason.OFPRRGROUPDELETE);
        builder.setMask(flowRemovedReasonList);
        masks.add(builder.build());
        // OFPCR_ROLE_SLAVE
        builder = new FlowRemovedMaskBuilder();
        flowRemovedReasonList = new ArrayList<>();
        flowRemovedReasonList.add(FlowRemovedReason.OFPRRIDLETIMEOUT);
        flowRemovedReasonList.add(FlowRemovedReason.OFPRRDELETE);
        builder.setMask(flowRemovedReasonList);
        masks.add(builder.build());
        return masks;
    }
}
