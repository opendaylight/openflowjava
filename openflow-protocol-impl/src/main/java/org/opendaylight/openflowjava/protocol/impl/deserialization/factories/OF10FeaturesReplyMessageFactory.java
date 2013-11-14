/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionTypeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.CapabilitiesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortStateV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutputBuilder;

/**
 * Translates FeaturesReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10FeaturesReplyMessageFactory implements OFDeserializer<GetFeaturesOutput> {
    
    private static final byte MAC_ADDRESS_LENGTH = 6;
    private static final byte MAX_PORT_NAME_LENGTH = 16;
    
    private static final byte PADDING_IN_FEATURES_REPLY_HEADER = 3;
    
    private static OF10FeaturesReplyMessageFactory instance;

    private OF10FeaturesReplyMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10FeaturesReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10FeaturesReplyMessageFactory();
        }
        return instance;
    }
    
    @Override
    public GetFeaturesOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetFeaturesOutputBuilder builder = new GetFeaturesOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        byte[] datapathId = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(datapathId);
        builder.setDatapathId(new BigInteger(datapathId));
        builder.setBuffers(rawMessage.readUnsignedInt());
        builder.setTables(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        builder.setCapabilitiesV10(createCapabilitiesV10(rawMessage.readUnsignedInt()));
        builder.setActionsV10(createActionsV10(rawMessage.readUnsignedInt()));
        deserializePort(rawMessage, builder);
        return builder.build();
    }

    private static CapabilitiesV10 createCapabilitiesV10(long input) {
        final Boolean FLOW_STATS = (input & (1 << 0)) != 0;
        final Boolean TABLE_STATS = (input & (1 << 1)) != 0;
        final Boolean PORT_STATS = (input & (1 << 2)) != 0;
        final Boolean STP = (input & (1 << 3)) != 0;
        final Boolean RESERVED = (input & (1 << 4)) != 0;
        final Boolean IP_REASM = (input & (1 << 5)) != 0;
        final Boolean QUEUE_STATS = (input & (1 << 6)) != 0;
        final Boolean ARP_MATCH_IP = (input & (1 << 7)) != 0;
        return new CapabilitiesV10(ARP_MATCH_IP, FLOW_STATS, IP_REASM,
                PORT_STATS, QUEUE_STATS, RESERVED, STP, TABLE_STATS);
    }
    
    private static ActionTypeV10 createActionsV10(long input) {
        final Boolean OUTPUT = (input & (1 << 0)) != 0;
        final Boolean SET_VLAN_VID = (input & (1 << 1)) != 0;
        final Boolean SET_VLAN_PCP = (input & (1 << 2)) != 0;
        final Boolean STRIP_VLAN = (input & (1 << 3)) != 0;
        final Boolean SET_DL_SRC = (input & (1 << 4)) != 0;
        final Boolean SET_DL_DST = (input & (1 << 5)) != 0;
        final Boolean SET_NW_SRC = (input & (1 << 6)) != 0;
        final Boolean SET_NW_DST = (input & (1 << 7)) != 0;
        final Boolean SET_NW_TOS = (input & (1 << 8)) != 0;
        final Boolean SET_TP_SRC = (input & (1 << 9)) != 0;
        final Boolean SET_TP_DST = (input & (1 << 10)) != 0;
        final Boolean ENQUEUE = (input & (1 << 11)) != 0;
        final Boolean VENDOR = (input & (1 << 12)) != 0;
        return new ActionTypeV10(ENQUEUE, OUTPUT, SET_DL_DST, SET_DL_SRC,
                SET_NW_DST, SET_NW_SRC, SET_NW_TOS, SET_TP_DST, SET_TP_SRC,
                SET_VLAN_PCP, SET_VLAN_VID, STRIP_VLAN, VENDOR);
    }
    
    private static void deserializePort(ByteBuf rawMessage, GetFeaturesOutputBuilder builder) {
        builder.setPortNo((long) rawMessage.readUnsignedShort());
        StringBuffer macToString = new StringBuffer();
        for(int i = 0; i < MAC_ADDRESS_LENGTH; i++){
            short mac = rawMessage.readUnsignedByte();
            macToString.append(String.format("%02X", mac));
        }
        builder.setHwAddr(new MacAddress(macToString.toString()));
        byte[] name = new byte[MAX_PORT_NAME_LENGTH];
        rawMessage.readBytes(name);
        builder.setName(name.toString());
        builder.setConfigV10(createPortConfig(rawMessage.readUnsignedInt()));
        builder.setStateV10(createPortState(rawMessage.readUnsignedInt()));
        builder.setCurrentFeaturesV10(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setAdvertisedFeaturesV10(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setSupportedFeaturesV10(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setPeerFeaturesV10(createPortFeatures(rawMessage.readUnsignedInt()));
    }
    
    private static PortStateV10 createPortState(long input){
        final Boolean _linkDown = ((input) & (1<<0)) != 0;
        final Boolean _blocked = ((input) & (1<<1)) != 0;
        final Boolean _live = ((input) & (1<<2)) != 0;
        final Boolean _stpListen = ((input) & (0<<8)) != 0;
        final Boolean _stpLearn = ((input) & (1<<8)) != 0;
        final Boolean _stpForward = ((input) & (1<<9)) != 0; // equals 2 << 8
        final Boolean _stpBlock = (((input) & (1<<9)) != 0) && (((input) & (1<<8)) != 0); // equals 3 << 8
        final Boolean _stpMask = ((input) & (1<<10)) != 0; // equals 4 << 8
        return new PortStateV10(_blocked, _linkDown, _live, _stpBlock, _stpForward, _stpLearn, _stpListen, _stpMask);
    }
    
    private static PortConfigV10 createPortConfig(long input){
        final Boolean _portDown = ((input) & (1<<0)) != 0;
        final Boolean _noStp = ((input) & (1<<1)) != 0;
        final Boolean _noRecv = ((input) & (1<<2)) != 0;
        final Boolean _noRecvStp = ((input) & (1<<3)) != 0;
        final Boolean _noFlood = ((input) & (1<<4)) != 0;
        final Boolean _noFwd  = ((input) & (1<<5)) != 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) != 0;
        return new PortConfigV10(_noFlood, _noFwd, _noPacketIn, _noRecv, _noRecvStp, _noStp, _portDown);
    }
    
    private static PortFeaturesV10 createPortFeatures(long input){
        final Boolean _10mbHd = ((input) & (1<<0)) != 0;
        final Boolean _10mbFd = ((input) & (1<<1)) != 0;
        final Boolean _100mbHd = ((input) & (1<<2)) != 0;
        final Boolean _100mbFd = ((input) & (1<<3)) != 0;
        final Boolean _1gbHd = ((input) & (1<<4)) != 0;
        final Boolean _1gbFd = ((input) & (1<<5)) != 0;
        final Boolean _10gbFd = ((input) & (1<<6)) != 0;
        final Boolean _copper = ((input) & (1<<7)) != 0;
        final Boolean _fiber = ((input) & (1<<8)) != 0;
        final Boolean _autoneg = ((input) & (1<<9)) != 0;
        final Boolean _pause = ((input) & (1<<10)) != 0;
        final Boolean _pauseAsym = ((input) & (1<<11)) != 0;
        return new PortFeaturesV10(_100mbFd, _100mbHd, _10gbFd, _10mbFd, _10mbHd,
                _1gbFd, _1gbHd, _autoneg, _copper, _fiber, _pause, _pauseAsym);
    }
    
}
