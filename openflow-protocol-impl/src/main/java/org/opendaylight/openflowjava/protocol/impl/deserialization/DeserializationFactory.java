/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 *
 * @author michal.polkorab
 */
public abstract class DeserializationFactory {

    /**
     * Transforms ByteBuf into correct POJO message
     * @param rawMessage 
     * @param version version decoded from OpenFlow protocol message
     * @return correct POJO as DataObject
     */
    public static DataObject bufferToMessage(ByteBuf rawMessage, short version) {
        DataObject dataObject = null;
        short type = rawMessage.readUnsignedByte();
        
        // TODO - check if no change happened, so that skipping length would cause problems
        rawMessage.skipBytes(Short.SIZE / Byte.SIZE);

        MessageTypeCodeKey msgTypeCodeKey = new MessageTypeCodeKey(version, type);
        OFDeserializer<?> decoder = DecoderTable.getInstance().getDecoder(msgTypeCodeKey);
        dataObject = decoder.bufferToMessage(rawMessage, version);
        return dataObject;
    }
}
