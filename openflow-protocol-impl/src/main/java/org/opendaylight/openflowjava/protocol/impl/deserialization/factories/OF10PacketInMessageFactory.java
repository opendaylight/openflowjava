/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessageBuilder;

/**
 * Translates PacketIn messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10PacketInMessageFactory implements OFDeserializer<PacketInMessage> {

    private static final byte PADDING_IN_PACKET_IN_HEADER = 1;

    private static OF10PacketInMessageFactory instance;
    
    private OF10PacketInMessageFactory() {
        // Singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10PacketInMessageFactory getInstance(){
        if(instance == null){
            instance = new OF10PacketInMessageFactory();
        }
        return instance;
    }

    @Override
    public PacketInMessage bufferToMessage(ByteBuf rawMessage, short version) {
        PacketInMessageBuilder builder = new PacketInMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setBufferId(rawMessage.readUnsignedInt());
        builder.setTotalLen(rawMessage.readUnsignedShort());
        builder.setInPort(rawMessage.readUnsignedShort());
        builder.setReason(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_PACKET_IN_HEADER);
        int remainingBytes = rawMessage.readableBytes();
        if (remainingBytes > 0) {
            builder.setData(rawMessage.readBytes(remainingBytes).array());
        }
        return builder.build();
    }
}
