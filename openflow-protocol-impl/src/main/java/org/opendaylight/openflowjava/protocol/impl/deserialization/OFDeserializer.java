/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.opendaylight.yangtools.yang.binding.DataObject;

import io.netty.buffer.ByteBuf;

/**
 * Uniform interface for deserializing factories
 * @author michal.polkorab
 * @author timotej.kubas
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
