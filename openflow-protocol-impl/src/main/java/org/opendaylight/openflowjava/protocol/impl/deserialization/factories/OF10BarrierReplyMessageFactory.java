/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutputBuilder;

/**
 * Translates BarrierReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10BarrierReplyMessageFactory implements OFDeserializer<BarrierOutput> {

    private static OF10BarrierReplyMessageFactory instance;

    private OF10BarrierReplyMessageFactory() {
        // do nothing, just singleton
    }

    /**
     * @return singleton factory
     */
    public static synchronized OF10BarrierReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10BarrierReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public BarrierOutput bufferToMessage(ByteBuf rawMessage, short version) {
        BarrierOutputBuilder builder = new BarrierOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        return builder.build();
    }
}
