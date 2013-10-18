/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.FlowRemovedMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.FlowRemovedMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PacketInMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PacketInMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PortStatusMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PortStatusMaskBuilder;

/**
 * @author timotej.kubas
 *
 */
public class GetAsyncReplyMessageFactoryTest {

    /**
     * Testing {@link GetAsyncReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testGetAsyncReplyMessage() {
        ByteBuf bb = BufferHelper.buildBuffer("00 00 00 02 "+ 
                                              "00 00 00 02 "+
                                              "00 00 00 02 "+
                                              "00 00 00 02 "+
                                              "00 00 00 04 "+
                                              "00 00 00 04");
        GetAsyncOutput builtByFactory = BufferHelper.decodeV13(GetAsyncReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong packetInMask",createPacketInMask().get(0).getMask(), 
                                                 builtByFactory.getPacketInMask().get(0).getMask());
        Assert.assertEquals("Wrong portStatusMask",createPortStatusMask().get(0).getMask(), 
                                                   builtByFactory.getPortStatusMask().get(0).getMask());
        Assert.assertEquals("Wrong flowRemovedMask",createFlowRemovedMask().get(0).getMask(), 
                                                    builtByFactory.getFlowRemovedMask().get(0).getMask());
    }
    
    private static List<PacketInMask> createPacketInMask() {
        List<PacketInReason> readPIRList = new ArrayList<>();
        List<PacketInMask> inMasks = new ArrayList<>();
        PacketInMaskBuilder maskBuilder = new PacketInMaskBuilder();
        
        readPIRList.add(PacketInReason.forValue(1));
        readPIRList.add(PacketInReason.forValue(1));
        inMasks.add(maskBuilder.setMask(readPIRList).build());
        return inMasks;
    }
    
    private static List<PortStatusMask> createPortStatusMask() {
        List<PortReason> readPortReasonList = new ArrayList<>();
        List<PortStatusMask> inMasks = new ArrayList<>();
        PortStatusMaskBuilder maskBuilder = new PortStatusMaskBuilder();
        
        readPortReasonList.add(PortReason.forValue(1));
        readPortReasonList.add(PortReason.forValue(1));
        inMasks.add(maskBuilder.setMask(readPortReasonList).build()); 
        return inMasks;
    }
    
    private static List<FlowRemovedMask> createFlowRemovedMask() {
        List<FlowRemovedReason> readFlowRemovedReasonList = new ArrayList<>();
        List<FlowRemovedMask> inMasks = new ArrayList<>();
        FlowRemovedMaskBuilder maskBuilder = new FlowRemovedMaskBuilder();
        
        readFlowRemovedReasonList.add(FlowRemovedReason.forValue(2));
        readFlowRemovedReasonList.add(FlowRemovedReason.forValue(2));
        inMasks.add(maskBuilder.setMask(readFlowRemovedReasonList).build()); 
        return inMasks;
    }
}
