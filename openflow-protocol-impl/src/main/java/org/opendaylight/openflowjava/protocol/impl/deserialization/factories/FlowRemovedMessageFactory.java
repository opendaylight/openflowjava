package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
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
    public static FlowRemovedMessageFactory getInstance(){
        if(instance == null){
            instance = new FlowRemovedMessageFactory();
        }
        
        return instance;
    }

    @Override
    public FlowRemovedMessage bufferToMessage(ByteBuf rawMessage, short version) {
        FlowRemovedMessageBuilder frmb = new FlowRemovedMessageBuilder();
        
        frmb.setVersion(version);
        frmb.setXid(rawMessage.readUnsignedInt());
        
        byte[] cookie = new byte[8];
        rawMessage.readBytes(cookie);
        frmb.setCookie(new BigInteger(cookie));
        frmb.setPriority(rawMessage.readUnsignedShort());
        
//        TODO enum! 
//        frmb.setReason(FlowRemovedReason.values()[rawMessage.readInt()]);
        rawMessage.skipBytes(1); //instead of setReason
        
        frmb.setTableId(new TableId((long)rawMessage.readUnsignedByte()));
        frmb.setDurationSec(rawMessage.readUnsignedInt());
        frmb.setDurationNsec(rawMessage.readUnsignedInt());
        frmb.setIdleTimeout(rawMessage.readUnsignedShort());
        frmb.setHardTimeout(rawMessage.readUnsignedShort());
        
        byte[] packet_count = new byte[8];
        rawMessage.readBytes(packet_count);
        frmb.setPacketCount(new BigInteger(packet_count));
        
        byte[] byte_count = new byte[8];
        rawMessage.readBytes(byte_count);
        frmb.setByteCount(new BigInteger(byte_count));
        
        return frmb.build();
    }

}
