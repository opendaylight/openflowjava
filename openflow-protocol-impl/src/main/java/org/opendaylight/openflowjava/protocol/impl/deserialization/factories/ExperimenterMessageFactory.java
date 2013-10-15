package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessageBuilder;

/**
 * 
 * @author michal.polkorab, 
 * @author timotej.kubas
 *
 */
public class ExperimenterMessageFactory implements OFDeserializer<ExperimenterMessage>{

    private static ExperimenterMessageFactory instance;
    
    private ExperimenterMessageFactory() {
        //singleton
    }
    
    
    /**
     * @return singleton factory
     */
    public static synchronized ExperimenterMessageFactory getInstance(){
        if (instance == null){
           instance = new ExperimenterMessageFactory(); 
        }
        return instance;
    }

    @Override
    public ExperimenterMessage bufferToMessage(ByteBuf rawMessage, short version) {
        ExperimenterMessageBuilder builder = new ExperimenterMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setExperimenter(rawMessage.readUnsignedInt());
        builder.setExpType(rawMessage.readUnsignedInt());
        return builder.build();
    }
}
