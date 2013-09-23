package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PortStatusMessageFactory implements OFDeserializer<PortStatusMessage> {

    private static PortStatusMessageFactory instance;
    private static final byte PADDING_IN_FEATURES_REPLY_HEADER = 7;
    
    private PortStatusMessageFactory() {
        // Singleton
    }
    
    /**
     * @return singleton factory
     */
    public static PortStatusMessageFactory getInstance(){
        if(instance == null){
            instance = new PortStatusMessageFactory();
        }
        return instance;
    }
    
    @Override
    public PortStatusMessage bufferToMessage(ByteBuf rawMessage, short version) {
        PortStatusMessageBuilder builder = new PortStatusMessageBuilder(); 
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setReason(PortReason.values()[rawMessage.readUnsignedByte()]);
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        // TODO - implement ofp_port desc
        return builder.build();
    }

    
    
}
