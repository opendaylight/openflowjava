/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutputBuilder;

/**
 * Translates BarrierReply messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class BarrierReplyMessageFactory implements
        OFDeserializer<BarrierOutput> {

    private static BarrierReplyMessageFactory instance;

    private BarrierReplyMessageFactory() {
        // do nothing, just singleton
    }

    /**
     * @return singleton factory
     */
    public static synchronized BarrierReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new BarrierReplyMessageFactory();
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
