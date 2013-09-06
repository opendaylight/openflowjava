/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutputBuilder;
import org.openflow.lib.deserialization.OfDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class EchoReplyMessageFactory implements OfDeserializer<EchoOutput> {

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
    
    /* (non-Javadoc)
     * @see org.openflow.core.deserialization.OfDeserializer#createMessage(io.netty.buffer.ByteBuf, short)
     */
    @Override
    public EchoOutput bufferToMessage(ByteBuf rawMessage, short version) {
        EchoOutputBuilder eob = new EchoOutputBuilder();
        eob.setVersion(version);
        eob.setXid(rawMessage.readUnsignedInt());
        // read the rest of EchoReply message
        eob.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return eob.build();
    }

}
