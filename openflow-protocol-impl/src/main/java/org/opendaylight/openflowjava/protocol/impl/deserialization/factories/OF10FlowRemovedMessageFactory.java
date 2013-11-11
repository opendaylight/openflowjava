/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessageBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10FlowRemovedMessageFactory implements OFDeserializer<FlowRemovedMessage> {

    private static final byte PADDING_IN_FLOW_REMOVED_MESSAGE = 1;
    private static final byte PADDING_IN_FLOW_REMOVED_MESSAGE_2 = 2;
    
    
    private static OF10FlowRemovedMessageFactory instance;
    
    private OF10FlowRemovedMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10FlowRemovedMessageFactory getInstance(){
        if(instance == null){
            instance = new OF10FlowRemovedMessageFactory();
        }
        return instance;
    }

    @Override
    public FlowRemovedMessage bufferToMessage(ByteBuf rawMessage, short version) {
        FlowRemovedMessageBuilder builder = new FlowRemovedMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setMatchV10(OF10MatchDeserializer.createMatchV10(rawMessage));
        byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(cookie);
        builder.setCookie(new BigInteger(cookie));
        builder.setPriority(rawMessage.readUnsignedShort());
        builder.setReason(FlowRemovedReason.forValue(rawMessage.readUnsignedByte()));
        rawMessage.skipBytes(PADDING_IN_FLOW_REMOVED_MESSAGE);
        builder.setDurationSec(rawMessage.readUnsignedInt());
        builder.setDurationNsec(rawMessage.readUnsignedInt());
        builder.setIdleTimeout(rawMessage.readUnsignedShort());
        rawMessage.skipBytes(PADDING_IN_FLOW_REMOVED_MESSAGE_2);
        byte[] packet_count = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(packet_count);
        builder.setPacketCount(new BigInteger(packet_count));
        byte[] byte_count = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(byte_count);
        builder.setByteCount(new BigInteger(byte_count));
        return builder.build();
    }

    
}
