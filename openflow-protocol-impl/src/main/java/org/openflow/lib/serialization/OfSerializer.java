/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 * @param <E> message type
 */
public interface OfSerializer <E extends DataObject> {

    /**
     * Transforms POJO/DTO into byte message (ByteBuf).
     * @param version version of used OF Protocol
     * @param out ByteBuf used for output
     * @param message message that will be transformed into ByteBuf
     */
    public abstract void messageToBuffer(short version, ByteBuf out, E message);
}
