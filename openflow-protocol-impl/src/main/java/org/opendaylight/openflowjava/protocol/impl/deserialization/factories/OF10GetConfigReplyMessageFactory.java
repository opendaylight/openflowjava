/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.SwitchConfigFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutputBuilder;

/**
 * Translates GetConfigReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10GetConfigReplyMessageFactory implements OFDeserializer<GetConfigOutput> {
    
private static OF10GetConfigReplyMessageFactory instance;
    
    private OF10GetConfigReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10GetConfigReplyMessageFactory getInstance(){
        if(instance == null){
            instance = new OF10GetConfigReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public GetConfigOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetConfigOutputBuilder builder = new GetConfigOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setFlags(SwitchConfigFlag.forValue(rawMessage.readUnsignedShort()));
        builder.setMissSendLen(rawMessage.readUnsignedShort());
        return builder.build();
    }

}
