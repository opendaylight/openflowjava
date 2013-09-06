/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessageBuilder;
import org.openflow.lib.deserialization.OfDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class EchoRequestMessageFactory implements OfDeserializer<EchoRequestMessage>{

    
    private static EchoRequestMessageFactory instance;

    private EchoRequestMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static EchoRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoRequestMessageFactory();
        }
        return instance;
    }

    @Override
    public EchoRequestMessage bufferToMessage(ByteBuf rawMessage, short version) {
        EchoRequestMessageBuilder emb = new EchoRequestMessageBuilder();
        emb.setVersion(version);
        emb.setXid(rawMessage.readUnsignedInt());
        // read the rest of EchoRequest message
        emb.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return emb.build();
    }
}
