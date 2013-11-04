/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class SetAsyncInputMessageFactory implements OFSerializer<SetAsyncInput> {
    private static final byte MESSAGE_TYPE = 28;
    private static final int MESSAGE_LENGTH = 32; 
    private static SetAsyncInputMessageFactory instance;
    
    private SetAsyncInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized SetAsyncInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new SetAsyncInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            SetAsyncInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        encodePacketInMask(message.getPacketInMask(), out);
        encodePortStatusMask(message.getPortStatusMask(), out);
        encodeFlowRemovedMask(message.getFlowRemovedMask(), out);
    }

    @Override
    public int computeLength(SetAsyncInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
    private static void encodePacketInMask(List<PacketInReason> packetInMask, ByteBuf outBuffer) {
        for (Iterator<PacketInReason> iterator = packetInMask.iterator(); iterator.hasNext();) {
            PacketInReason currentPacketInReason = iterator.next();
            outBuffer.writeInt(packetInReasonToBitmask(currentPacketInReason.getIntValue()));
        }
    }
    
    private static void encodePortStatusMask(List<PortReason> portStatusMask, ByteBuf outBuffer) {
        for (Iterator<PortReason> iterator = portStatusMask.iterator(); iterator.hasNext();) {
            PortReason currentPortReason = iterator.next();
            outBuffer.writeInt(portReasonToBitmask(currentPortReason.getIntValue()));
        }
    }
    
    private static void encodeFlowRemovedMask(List<FlowRemovedReason> flowRemovedMask, ByteBuf outBuffer) {
        for (Iterator<FlowRemovedReason> iterator = flowRemovedMask.iterator(); iterator.hasNext();) {
            FlowRemovedReason currentFlowRemovedReason = iterator.next();
            outBuffer.writeInt(flowRemovedReasonToBitmask(currentFlowRemovedReason.getIntValue()));
        }
    }
    
    private static int packetInReasonToBitmask(int option) {
        Boolean OFPRNOMATCH = false;
        Boolean OFPRACTION = false;
        Boolean OFPRINVALIDTTL = false;
        int packetInReasonBitmask = 0;
        
        switch(option) {
        case 0: OFPRNOMATCH = true; break;
        case 1: OFPRACTION = true; break;
        case 2: OFPRINVALIDTTL = true; break;
        default: break;
        }
        
        Map<Integer, Boolean> packetInReasonMap = new HashMap<>();
        packetInReasonMap.put(0, OFPRNOMATCH);
        packetInReasonMap.put(1, OFPRACTION);
        packetInReasonMap.put(2, OFPRINVALIDTTL);
        
        packetInReasonBitmask = ByteBufUtils.fillBitMaskFromMap(packetInReasonMap);
        
        return packetInReasonBitmask;
    }
    
    private static int portReasonToBitmask(int option) {
        Boolean OFPPRADD = false;
        Boolean OFPPRDELETE = false;
        Boolean OFPPRMODIFY = false;
        int portReasonBitmask = 0;
        
        switch(option) {
        case 0: OFPPRADD = true; break;
        case 1: OFPPRDELETE = true; break;
        case 2: OFPPRMODIFY = true; break;
        default: break;
        }
        
        Map<Integer, Boolean> portReasonMap = new HashMap<>();
        portReasonMap.put(0, OFPPRADD);
        portReasonMap.put(1, OFPPRDELETE);
        portReasonMap.put(2, OFPPRMODIFY);
        
        portReasonBitmask = ByteBufUtils.fillBitMaskFromMap(portReasonMap);
        
        return portReasonBitmask;
    }
    
    private static int flowRemovedReasonToBitmask(int option) {
        Boolean OFPRRIDLETIMEOUT = false;
        Boolean OFPRRHARDTIMEOUT = false;
        Boolean OFPRRDELETE = false;
        Boolean OFPRRGROUPDELETE = false;
        int flowRemovedReasonBitmask = 0;
        
        switch(option) {
        case 0: OFPRRIDLETIMEOUT = true; break;
        case 1: OFPRRHARDTIMEOUT = true; break;
        case 2: OFPRRDELETE = true; break;
        case 3: OFPRRGROUPDELETE = true; break;
        default: break;
        }
        
        Map<Integer, Boolean> portReasonMap = new HashMap<>();
        portReasonMap.put(0, OFPRRIDLETIMEOUT);
        portReasonMap.put(1, OFPRRHARDTIMEOUT);
        portReasonMap.put(2, OFPRRDELETE);
        portReasonMap.put(3, OFPRRGROUPDELETE);
        
        flowRemovedReasonBitmask = ByteBufUtils.fillBitMaskFromMap(portReasonMap);
        
        return flowRemovedReasonBitmask;
    }
}
