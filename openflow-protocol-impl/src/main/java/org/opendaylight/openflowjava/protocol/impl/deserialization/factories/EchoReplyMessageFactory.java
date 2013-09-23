/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoReplyMessageFactory implements OFDeserializer<EchoOutput> {

    private static EchoReplyMessageFactory instance;

    private EchoReplyMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static EchoReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoReplyMessageFactory();
        }
        return instance;
    }
    
    @Override
    public EchoOutput bufferToMessage(ByteBuf rawMessage, short version) {
        EchoOutputBuilder eob = new EchoOutputBuilder();
        eob.setVersion(version);
        eob.setXid(rawMessage.readUnsignedInt());
        eob.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return eob.build();
    }

}
