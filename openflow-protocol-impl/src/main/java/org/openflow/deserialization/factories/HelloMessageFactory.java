/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessageBuilder;
import org.openflow.lib.deserialization.OfDeserializer;

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
    public HelloMessage bufferToMessage(ByteBuf rawMessage, short version) {
        HelloMessageBuilder hmb = new HelloMessageBuilder();
        hmb.setVersion(version);
        hmb.setXid(rawMessage.readUnsignedInt());
        return hmb.build();
    }
}
