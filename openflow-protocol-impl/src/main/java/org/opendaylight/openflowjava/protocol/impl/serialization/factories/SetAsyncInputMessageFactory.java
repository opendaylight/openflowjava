/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.FlowRemovedMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PacketInMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.async.body.grouping.PortStatusMask;

/**
 * Translates SetAsync messages
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
    
    private static void encodePacketInMask(List<PacketInMask> packetInMask, ByteBuf outBuffer) {
        if (packetInMask != null) {
            for (PacketInMask currentPacketMask : packetInMask) {
                List<PacketInReason> mask = currentPacketMask.getMask();
                if (mask != null)  {
                    Map<Integer, Boolean> packetInReasonMap = new HashMap<>();
                    for (PacketInReason packetInReason : mask) {
                        if (PacketInReason.OFPRNOMATCH.equals(packetInReason)) {
                            packetInReasonMap.put(PacketInReason.OFPRNOMATCH.getIntValue(), true);
                        } else if (PacketInReason.OFPRACTION.equals(packetInReason)) {
                            packetInReasonMap.put(PacketInReason.OFPRACTION.getIntValue(), true);
                        } else if (PacketInReason.OFPRINVALIDTTL.equals(packetInReason)) {
                            packetInReasonMap.put(PacketInReason.OFPRINVALIDTTL.getIntValue(), true);
                        }
                    }
                    outBuffer.writeInt(ByteBufUtils.fillBitMaskFromMap(packetInReasonMap));
                }
            }
        }
    }
    
    private static void encodePortStatusMask(List<PortStatusMask> portStatusMask, ByteBuf outBuffer) {
        if (portStatusMask != null) {
            for (PortStatusMask currentPortStatusMask : portStatusMask) {
                List<PortReason> mask = currentPortStatusMask.getMask();
                if (mask != null)  {
                    Map<Integer, Boolean> portStatusReasonMap = new HashMap<>();
                    for (PortReason packetInReason : mask) {
                        if (PortReason.OFPPRADD.equals(packetInReason)) {
                            portStatusReasonMap.put(PortReason.OFPPRADD.getIntValue(), true);
                        } else if (PortReason.OFPPRDELETE.equals(packetInReason)) {
                            portStatusReasonMap.put(PortReason.OFPPRDELETE.getIntValue(), true);
                        } else if (PortReason.OFPPRMODIFY.equals(packetInReason)) {
                            portStatusReasonMap.put(PortReason.OFPPRMODIFY.getIntValue(), true);
                        }
                    }
                    outBuffer.writeInt(ByteBufUtils.fillBitMaskFromMap(portStatusReasonMap));
                }
            }
        }
    }
    
    private static void encodeFlowRemovedMask(List<FlowRemovedMask> flowRemovedMask, ByteBuf outBuffer) {
        if (flowRemovedMask != null) {
            for (FlowRemovedMask currentFlowRemovedMask : flowRemovedMask) {
                List<FlowRemovedReason> mask = currentFlowRemovedMask.getMask();
                if (mask != null)  {
                    Map<Integer, Boolean> flowRemovedReasonMap = new HashMap<>();
                    for (FlowRemovedReason packetInReason : mask) {
                        if (FlowRemovedReason.OFPRRIDLETIMEOUT.equals(packetInReason)) {
                            flowRemovedReasonMap.put(FlowRemovedReason.OFPRRIDLETIMEOUT.getIntValue(), true);
                        } else if (FlowRemovedReason.OFPRRHARDTIMEOUT.equals(packetInReason)) {
                            flowRemovedReasonMap.put(FlowRemovedReason.OFPRRHARDTIMEOUT.getIntValue(), true);
                        } else if (FlowRemovedReason.OFPRRDELETE.equals(packetInReason)) {
                            flowRemovedReasonMap.put(FlowRemovedReason.OFPRRDELETE.getIntValue(), true);
                        } else if (FlowRemovedReason.OFPRRGROUPDELETE.equals(packetInReason)) {
                            flowRemovedReasonMap.put(FlowRemovedReason.OFPRRGROUPDELETE.getIntValue(), true);
                        }
                    }
                    outBuffer.writeInt(ByteBufUtils.fillBitMaskFromMap(flowRemovedReasonMap));
                }
            }
        }
    }
    
}