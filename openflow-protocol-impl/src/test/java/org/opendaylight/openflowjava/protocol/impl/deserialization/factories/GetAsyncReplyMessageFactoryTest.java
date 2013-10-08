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
       
        Assert.assertTrue("Wrong packetInMask",comparePIRLists(createPacketInMask().get(0).getMask(), 
                                                   builtByFactory.getPacketInMask().get(0).getMask()));
        Assert.assertTrue("Wrong portStatusMask",comparePortReasonLists(createPortStatusMask().get(0).getMask(), 
                                                            builtByFactory.getPortStatusMask().get(0).getMask()));
        Assert.assertTrue("Wrong flowRemovedMask",compareFlowRemovedReasonLists(createFlowRemovedMask().get(0).getMask(), 
                builtByFactory.getFlowRemovedMask().get(0).getMask()));
    }
    
    private static List<PacketInMask> createPacketInMask() {
        List<PacketInReason> readPIRList = new ArrayList<PacketInReason>();
        List<PacketInMask> inMasks = new ArrayList<PacketInMask>();
        PacketInMaskBuilder maskBuilder = new PacketInMaskBuilder();
        
        readPIRList.add(PacketInReason.forValue(1));
        readPIRList.add(PacketInReason.forValue(1));
        inMasks.add(maskBuilder.setMask(readPIRList).build());
        return inMasks;
    }
    
    private static List<PortStatusMask> createPortStatusMask() {
        List<PortReason> readPortReasonList = new ArrayList<PortReason>();
        List<PortStatusMask> inMasks = new ArrayList<PortStatusMask>();
        PortStatusMaskBuilder maskBuilder = new PortStatusMaskBuilder();
        
        readPortReasonList.add(PortReason.forValue(1));
        readPortReasonList.add(PortReason.forValue(1));
        inMasks.add(maskBuilder.setMask(readPortReasonList).build()); 
        return inMasks;
    }
    
    private static List<FlowRemovedMask> createFlowRemovedMask() {
        List<FlowRemovedReason> readFlowRemovedReasonList = new ArrayList<FlowRemovedReason>();
        List<FlowRemovedMask> inMasks = new ArrayList<FlowRemovedMask>();
        FlowRemovedMaskBuilder maskBuilder = new FlowRemovedMaskBuilder();
        
        readFlowRemovedReasonList.add(FlowRemovedReason.forValue(2));
        readFlowRemovedReasonList.add(FlowRemovedReason.forValue(2));
        inMasks.add(maskBuilder.setMask(readFlowRemovedReasonList).build()); 
        return inMasks;
    }
    
    private static boolean comparePIRLists(List<PacketInReason> createdMessage, List<PacketInReason> fromBuffer) {
        boolean result = false;
        int romMessageLength = createdMessage.size();
        for (int i = 0; i < romMessageLength; i++) {
            if ((createdMessage.get(i).getIntValue()) == (fromBuffer.get(i).getIntValue())) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
    
    private static boolean comparePortReasonLists(List<PortReason> createdMessage, 
            List<PortReason> fromBuffer) {
        boolean result = false;
        int fromMessageLength = createdMessage.size();
        for (int i = 0; i < fromMessageLength; i++) {
            if ((createdMessage.get(i).getIntValue()) == (fromBuffer.get(i).getIntValue())) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
    
    private static boolean compareFlowRemovedReasonLists(List<FlowRemovedReason> fromMessage, 
            List<FlowRemovedReason> fromBuffer) {
        boolean result = false;
        int fromMessageLength = fromMessage.size();
        for (int i = 0; i < fromMessageLength; i++) {
            if ((fromMessage.get(i).getIntValue()) == (fromBuffer.get(i).getIntValue())) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
}
