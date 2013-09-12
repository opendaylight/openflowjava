/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.opendaylight.yangtools.yang.binding.DataObject;

import io.netty.buffer.ByteBuf;

/**
 * @author michal.polkorab
 *
 * @param <E> message code type
 */
public interface OFDeserializer<E extends DataObject> {

    /**
     * Transforms byte message into POJO/DTO (of type E).
     * Assumes that input ByteBuf's readerIndex is pointing on length in OpenFlow header
     * 
     * @param rawMessage message as bytes in ByteBuf
     * @param version version of used OF Protocol
     * @return HelloMessage as DataObject
     */
    public abstract E bufferToMessage(ByteBuf rawMessage, short version);

}
