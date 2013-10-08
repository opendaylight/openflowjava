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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class SetAsyncInputMessageFactoryTest {

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
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength());
        Assert.assertEquals("Wrong packetInMask", message.getPacketInMask(), readPacketInMask(out));
        Assert.assertEquals("Wrong packetInMask", message.getPortStatusMask(), readPortStatusMask(out));
        Assert.assertEquals("Wrong packetInMask", message.getFlowRemovedMask(), readFlowRemovedReasonMask(out));
        
    }
    
    private static List<PacketInReason> createPacketInMask() {
        List<PacketInReason> packetInReasonList = new ArrayList<PacketInReason>();
        packetInReasonList.add(PacketInReason.forValue(1));
        packetInReasonList.add(PacketInReason.forValue(2));
        return packetInReasonList;
    }
    
    private static List<PortReason> createPortStatusMask() {
        List<PortReason> portReasonList = new ArrayList<PortReason>();
        portReasonList.add(PortReason.forValue(1));
        portReasonList.add(PortReason.forValue(2));
        return portReasonList;
    }
    
    private static List<FlowRemovedReason> createFlowRemowedMask() {
        List<FlowRemovedReason> flowRemovedReasonList = new ArrayList<FlowRemovedReason>();
        flowRemovedReasonList.add(FlowRemovedReason.forValue(2));
        flowRemovedReasonList.add(FlowRemovedReason.forValue(3));
        return flowRemovedReasonList;
    }
    
    private static List<PacketInReason> readPacketInMask(ByteBuf outputBuf) {
        List<PacketInReason> readPIRList = new ArrayList<PacketInReason>();
        readPIRList.add(readPacketInReason((int) outputBuf.readUnsignedInt()));
        readPIRList.add(readPacketInReason((int) outputBuf.readUnsignedInt()));
        return readPIRList;
    }
    
    private static List<PortReason> readPortStatusMask(ByteBuf outputBuf) {
        List<PortReason> readPortReasonList = new ArrayList<PortReason>();
        readPortReasonList.add(readPortReason((int) outputBuf.readUnsignedInt()));
        readPortReasonList.add(readPortReason((int) outputBuf.readUnsignedInt()));
        return readPortReasonList;
    }
    
    private static List<FlowRemovedReason> readFlowRemovedReasonMask(ByteBuf outputBuf) {
        List<FlowRemovedReason> readFlowRemovedReasonList = new ArrayList<FlowRemovedReason>();
        readFlowRemovedReasonList.add(readFlowRemovedReason((int) outputBuf.readUnsignedInt()));
        readFlowRemovedReasonList.add(readFlowRemovedReason((int) outputBuf.readUnsignedInt()));
        return readFlowRemovedReasonList;
    }
    
    private static PacketInReason readPacketInReason(int input) {
        PacketInReason reason = null;
        boolean OFPRNOMATCH = (input & (1 << 0)) > 0;
        boolean OFPRACTION = (input & (1 << 1)) > 0;
        boolean OFPRINVALIDTTL = (input & (1 << 2)) > 0;
        
        if (OFPRNOMATCH) {
            return PacketInReason.forValue(0);
            }
        if (OFPRACTION) {
            return PacketInReason.forValue(1);
            }
        if (OFPRINVALIDTTL) {
            return PacketInReason.forValue(2);
            }
        
        return reason;
    }
    
    private static PortReason readPortReason(int input) {
        PortReason reason = null;
        boolean OFPPRADD = (input & (1 << 0)) > 0;
        boolean OFPPRDELETE = (input & (1 << 1)) > 0;
        boolean OFPPRMODIFY = (input & (1 << 2)) > 0;
        
        if (OFPPRADD) {
            return PortReason.forValue(0);
            }
        if (OFPPRDELETE) {
            return PortReason.forValue(1);
            }
        if (OFPPRMODIFY) {
            return PortReason.forValue(2);
            }
        
        return reason;
    }
    
    private static FlowRemovedReason readFlowRemovedReason(int input) {
        FlowRemovedReason reason = null;
        boolean OFPRRIDLETIMEOUT = (input & (1 << 0)) > 0;
        boolean OFPRRHARDTIMEOUT = (input & (1 << 1)) > 0;
        boolean OFPRRDELETE = (input & (1 << 2)) > 0;
        boolean OFPRRGROUPDELETE = (input & (1 << 3)) > 0;
        
        if (OFPRRIDLETIMEOUT) {
            return FlowRemovedReason.forValue(0);
            }
        if (OFPRRHARDTIMEOUT) {
            return FlowRemovedReason.forValue(1);
            }
        if (OFPRRDELETE) {
            return FlowRemovedReason.forValue(2);
            }
        if (OFPRRGROUPDELETE) {
            return FlowRemovedReason.forValue(3);
            }
        
        return reason;
    }
}
