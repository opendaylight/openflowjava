package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessageBuilder;

/**
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
    public static PacketInMessageFactory getInstance(){
        if(instance == null){
            instance = new PacketInMessageFactory();
        }
        
        return instance;
    }

    @Override
    public PacketInMessage bufferToMessage(ByteBuf rawMessage, short version) {
        PacketInMessageBuilder pimb = new PacketInMessageBuilder();
        pimb.setVersion(version);
        pimb.setXid(rawMessage.readUnsignedInt());
        pimb.setBufferId(rawMessage.readUnsignedInt());
        pimb.setTotalLen(rawMessage.readUnsignedShort());
        pimb.setReason(rawMessage.readUnsignedByte());
        pimb.setTableId(new TableId((long)rawMessage.readUnsignedByte()));
        
        byte[] cookie = new byte[8];
        rawMessage.readBytes(cookie);
        pimb.setCookie(new BigInteger(cookie));
        // TODO - implement match factories to finish this factory 
        rawMessage.skipBytes(PADDING_IN_PACKET_IN_HEADER);
        
        pimb.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        
        return pimb.build();
    }
}
