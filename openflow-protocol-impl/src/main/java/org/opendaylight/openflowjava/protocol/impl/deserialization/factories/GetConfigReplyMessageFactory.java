package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.SwitchConfigFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutputBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class GetConfigReplyMessageFactory implements OFDeserializer<GetConfigOutput> {

    private static GetConfigReplyMessageFactory instance;
    
    private GetConfigReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static GetConfigReplyMessageFactory getInstance(){
        if(instance == null){
            instance = new GetConfigReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public GetConfigOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetConfigOutputBuilder gcob = new GetConfigOutputBuilder();
        gcob.setVersion(version);
        gcob.setXid(rawMessage.readUnsignedInt());
        gcob.setFlags(SwitchConfigFlag.values()[rawMessage.readUnsignedShort()]);
        gcob.setMissSendLen(rawMessage.readUnsignedShort());
        return gcob.build();
    }
}
