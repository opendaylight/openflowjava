/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.FlowRemovedMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.FlowRemovedMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PacketInMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PacketInMaskBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PortStatusMask;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.get.async.reply.PortStatusMaskBuilder;

/**
 * Translates GetAsyncReply messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetAsyncReplyMessageFactory implements OFDeserializer<GetAsyncOutput> {
    
    private static GetAsyncReplyMessageFactory instance;
    
    private GetAsyncReplyMessageFactory() {
        // singleton
    }

    /**
     * @return singleton factory
     */
    public static synchronized GetAsyncReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetAsyncReplyMessageFactory();
        }
        return instance;
    }
    
    @Override
    public GetAsyncOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetAsyncOutputBuilder builder = new GetAsyncOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setPacketInMask(decodePacketInMask(rawMessage));
        builder.setPortStatusMask(decodePortStatusMask(rawMessage));
        builder.setFlowRemovedMask(decodeFlowRemovedMask(rawMessage));
        return builder.build();
    }
    
    private static List<PacketInMask> decodePacketInMask(ByteBuf outputBuf) {
        List<PacketInReason> readPIRList = new ArrayList<>();
        List<PacketInMask> inMasks = new ArrayList<>();
        PacketInMaskBuilder maskBuilder = new PacketInMaskBuilder();
        
        readPIRList.add(decodedPacketInReason((int) outputBuf.readUnsignedInt()));
        readPIRList.add(decodedPacketInReason((int) outputBuf.readUnsignedInt()));
        inMasks.add(maskBuilder.setMask(readPIRList).build()); 
        return inMasks;
    }
    
    private static List<PortStatusMask> decodePortStatusMask(ByteBuf outputBuf) {
        List<PortReason> readPortReasonList = new ArrayList<>();
        List<PortStatusMask> inMasks = new ArrayList<>();
        PortStatusMaskBuilder maskBuilder = new PortStatusMaskBuilder();
        
        readPortReasonList.add(decodePortReason((int) outputBuf.readUnsignedInt()));
        readPortReasonList.add(decodePortReason((int) outputBuf.readUnsignedInt()));
        inMasks.add(maskBuilder.setMask(readPortReasonList).build()); 
        return inMasks;
    }
    
    private static List<FlowRemovedMask> decodeFlowRemovedMask(ByteBuf outputBuf) {
        List<FlowRemovedReason> readFlowRemovedReasonList = new ArrayList<>();
        List<FlowRemovedMask> inMasks = new ArrayList<>();
        FlowRemovedMaskBuilder maskBuilder = new FlowRemovedMaskBuilder();
        
        readFlowRemovedReasonList.add(decodeFlowRemovedReason((int) outputBuf.readUnsignedInt()));
        readFlowRemovedReasonList.add(decodeFlowRemovedReason((int) outputBuf.readUnsignedInt()));
        inMasks.add(maskBuilder.setMask(readFlowRemovedReasonList).build()); 
        return inMasks;
    }
    
    private static PacketInReason decodedPacketInReason(int input) {
        PacketInReason reason = null;
        Boolean OFPRNOMATCH = (input & (1 << 0)) != 0;
        Boolean OFPRACTION = (input & (1 << 1)) != 0;
        Boolean OFPRINVALIDTTL = (input & (1 << 2)) != 0;
        
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
    
    private static PortReason decodePortReason(int input) {
        PortReason reason = null;
        Boolean OFPPRADD = (input & (1 << 0)) != 0;
        Boolean OFPPRDELETE = (input & (1 << 1)) != 0;
        Boolean OFPPRMODIFY = (input & (1 << 2)) != 0;
        
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
    
    private static FlowRemovedReason decodeFlowRemovedReason(int input) {
        FlowRemovedReason reason = null;
        Boolean OFPRRIDLETIMEOUT = (input & (1 << 0)) != 0;
        Boolean OFPRRHARDTIMEOUT = (input & (1 << 1)) != 0;
        Boolean OFPRRDELETE = (input & (1 << 2)) != 0;
        Boolean OFPRRGROUPDELETE = (input & (1 << 3)) != 0;
        
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
