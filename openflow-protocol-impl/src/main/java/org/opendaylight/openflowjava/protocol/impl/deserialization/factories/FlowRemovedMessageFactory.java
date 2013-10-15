package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowRemovedReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessageBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class FlowRemovedMessageFactory implements OFDeserializer<FlowRemovedMessage> {
    
    private static FlowRemovedMessageFactory instance;
    
    private FlowRemovedMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized FlowRemovedMessageFactory getInstance(){
        if(instance == null){
            instance = new FlowRemovedMessageFactory();
        }
        return instance;
    }

    @Override
    public FlowRemovedMessage bufferToMessage(ByteBuf rawMessage, short version) {
        FlowRemovedMessageBuilder builder = new FlowRemovedMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(cookie);
        builder.setCookie(new BigInteger(cookie));
        builder.setPriority(rawMessage.readUnsignedShort());
        builder.setReason(FlowRemovedReason.forValue(rawMessage.readUnsignedByte()));
        builder.setTableId(new TableId((long)rawMessage.readUnsignedByte()));
        builder.setDurationSec(rawMessage.readUnsignedInt());
        builder.setDurationNsec(rawMessage.readUnsignedInt());
        builder.setIdleTimeout(rawMessage.readUnsignedShort());
        builder.setHardTimeout(rawMessage.readUnsignedShort());
        byte[] packet_count = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(packet_count);
        builder.setPacketCount(new BigInteger(packet_count));
        byte[] byte_count = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(byte_count);
        builder.setByteCount(new BigInteger(byte_count));
        return builder.build();
    }
}
