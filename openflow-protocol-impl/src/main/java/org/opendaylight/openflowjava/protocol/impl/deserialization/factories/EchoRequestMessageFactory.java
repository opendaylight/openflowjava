/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessageBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoRequestMessageFactory implements OFDeserializer<EchoRequestMessage>{

    private static EchoRequestMessageFactory instance;

    private EchoRequestMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized EchoRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoRequestMessageFactory();
        }
        return instance;
    }

    @Override
    public EchoRequestMessage bufferToMessage(ByteBuf rawMessage, short version) {
        EchoRequestMessageBuilder builder = new EchoRequestMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return builder.build();
    }
}
