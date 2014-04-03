/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import io.netty.buffer.ByteBuf;

/**
 * Uniform interface for serializers
 * @author michal.polkorab
 * @author timotej.kubas
 * @param <E> message type
 */
public interface OFSerializer <E> {

    /**
     * Transforms POJO/DTO into byte message (ByteBuf).
     * @param object object to be serialized
     * @param outBuffer output buffer
     */
    public void serialize(E object, ByteBuf outBuffer);

    /**
     * Injects serializer table
     * @param table table instance
     */
    public void injectSerializerTable(SerializerTable table);

}
