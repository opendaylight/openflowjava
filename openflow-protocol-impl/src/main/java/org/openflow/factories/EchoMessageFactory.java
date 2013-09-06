/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessageBuilder;
import org.openflow.core.deserialization.OfDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class EchoMessageFactory implements OfDeserializer<EchoRequestMessage>{

    
    private static EchoMessageFactory instance;

    private EchoMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static EchoMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoMessageFactory();
        }
        return instance;
    }

    @Override
    public EchoRequestMessage createMessage(ByteBuf rawMessage, short version) {
        EchoRequestMessageBuilder emb = new EchoRequestMessageBuilder();
        emb.setVersion(version);
        rawMessage.readUnsignedShort();
        emb.setXid(rawMessage.readUnsignedInt());
        return emb.build();
    }
}
