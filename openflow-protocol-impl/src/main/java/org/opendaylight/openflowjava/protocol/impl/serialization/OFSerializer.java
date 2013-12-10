/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * Uniform interface for serializing factories
 * @author michal.polkorab
 * @author timotej.kubas
 * @param <E> message type
 */
public interface OFSerializer <E extends DataObject> {

    /**
     * Transforms POJO/DTO into byte message (ByteBuf).
     * @param version version of used OF Protocol
     * @param out ByteBuf used for output
     * @param message message that will be transformed into ByteBuf
     */
    public abstract void messageToBuffer(short version, ByteBuf out, E message);
    
    /**
     * Compute length of received message
     * @param message 
     * @return computed length
     */
    public abstract int computeLength(E message);
    
    /**
     * @return message code type
     */
    public byte getMessageType();
}
