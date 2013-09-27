/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public abstract class DeserializationFactory {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DeserializationFactory.class);

    /**
     * Transforms ByteBuf into correct POJO message
     * @param rawMessage 
     * @param version version decoded from OpenFlow protocol message
     * @return correct POJO as DataObject
     */
    public static DataObject bufferToMessage(ByteBuf rawMessage, short version) {
        DataObject dataObject = null;
        short type = rawMessage.readUnsignedByte();
        rawMessage.skipBytes(Short.SIZE / Byte.SIZE);

        MessageTypeCodeKey msgTypeCodeKey = new MessageTypeCodeKey(version, type);
        OFDeserializer<?> decoder = DecoderTable.getInstance().getDecoder(msgTypeCodeKey);
        if (decoder != null) {
            dataObject = decoder.bufferToMessage(rawMessage, version);
        } else {
            LOGGER.warn("No correct decoder found in DecoderTable - it is null");
        }
        return dataObject;
    }
}
