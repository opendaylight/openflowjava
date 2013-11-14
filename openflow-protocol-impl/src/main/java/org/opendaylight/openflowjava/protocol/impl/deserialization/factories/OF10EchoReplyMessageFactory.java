/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutputBuilder;

/**
 * Translates EchoReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10EchoReplyMessageFactory implements OFDeserializer<EchoOutput> {

    private static OF10EchoReplyMessageFactory instance;

    private OF10EchoReplyMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10EchoReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10EchoReplyMessageFactory();
        }
        return instance;
    }
    
    @Override
    public EchoOutput bufferToMessage(ByteBuf rawMessage, short version) {
        EchoOutputBuilder builder = new EchoOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        int remainingBytes = rawMessage.readableBytes();
        if (remainingBytes > 0) {
            builder.setData(rawMessage.readBytes(remainingBytes).array());
        }
        return builder.build();
    }
}
