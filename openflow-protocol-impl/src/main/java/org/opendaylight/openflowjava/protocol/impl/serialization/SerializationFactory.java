/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class SerializationFactory {

    /**
     * Transforms POJO message into ByteBuf
     * @param version version used for encoding received message
     * @param out ByteBuf for storing and sending transformed message
     * @param message POJO message
     */
    public static <E extends DataObject> void messageToBuffer(short version, ByteBuf out, E message) {
        @SuppressWarnings("unchecked")
        MessageTypeKey<E> msgTypeKey = new MessageTypeKey<E>(version, (Class<E>) message.getClass());
        OFSerializer<E> encoder = EncoderTable.getInstance().getEncoder(msgTypeKey);
        encoder.messageToBuffer(version, out, message);
    }
}
