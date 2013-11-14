/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortStateV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;

/**
 * Translates PortStatus messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10PortStatusMessageFactory implements OFDeserializer<PortStatusMessage> {

    private static final byte PADDING_IN_PORT_STATUS_HEADER = 7;
    private static final int MAC_ADDRESS_LENGTH = 6;
    private static final int MAX_PORT_NAME_LENGTH = 16;

    private static OF10PortStatusMessageFactory instance;
    
    private OF10PortStatusMessageFactory() {
        // Singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10PortStatusMessageFactory getInstance(){
        if(instance == null){
            instance = new OF10PortStatusMessageFactory();
        }
        return instance;
    }
    
    @Override
    public PortStatusMessage bufferToMessage(ByteBuf rawMessage, short version) {
        PortStatusMessageBuilder builder = new PortStatusMessageBuilder(); 
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setReason(PortReason.forValue(rawMessage.readUnsignedByte()));
        rawMessage.skipBytes(PADDING_IN_PORT_STATUS_HEADER);
        deserializePort(rawMessage, builder);
        return builder.build();
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
    
    private static void deserializePort(ByteBuf rawMessage, PortStatusMessageBuilder builder) {
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
}
