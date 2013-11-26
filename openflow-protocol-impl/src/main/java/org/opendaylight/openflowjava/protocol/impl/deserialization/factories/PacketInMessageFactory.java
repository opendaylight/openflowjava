/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.MatchDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessageBuilder;

/**
 * Translates PacketIn messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PacketInMessageFactory implements OFDeserializer<PacketInMessage> {

    private static PacketInMessageFactory instance;
    private static final byte PADDING_IN_PACKET_IN_HEADER = 2;
    
    private PacketInMessageFactory() {
        // Singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized PacketInMessageFactory getInstance(){
        if(instance == null){
            instance = new PacketInMessageFactory();
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
        builder.setReason(PacketInReason.forValue(rawMessage.readUnsignedByte()));
        builder.setTableId(new TableId((long)rawMessage.readUnsignedByte()));
        byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(cookie);
        builder.setCookie(new BigInteger(cookie));
        builder.setMatch(MatchDeserializer.createMatch(rawMessage)); 
        rawMessage.skipBytes(PADDING_IN_PACKET_IN_HEADER);
        builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return builder.build();
    }
}
