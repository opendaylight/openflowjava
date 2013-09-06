/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessageBuilder;
import org.openflow.core.deserialization.OfDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class HelloMessageFactory implements OfDeserializer<HelloMessage> {

    private static HelloMessageFactory instance;

    private HelloMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static HelloMessageFactory getInstance() {
        if (instance == null) {
            instance = new HelloMessageFactory();
        }
        return instance;
    }
    
    @Override
    public HelloMessage createMessage(ByteBuf rawMessage, short version) {
        HelloMessageBuilder hmb = new HelloMessageBuilder();
        hmb.setVersion(version);
        rawMessage.readUnsignedShort();
        hmb.setXid(rawMessage.readUnsignedInt());
        return hmb.build();
    }
}
