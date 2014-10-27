/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.yangtools.yang.binding.DataObject;

import io.netty.buffer.ByteBuf;

/**
 * Does only-header serialization (such as oxm_ids, action_ids, instruction_ids)
 * @author michal.polkorab
 * @param <SERIALIZER_TYPE>
 */
public interface HeaderSerializer<SERIALIZER_TYPE extends DataObject> extends OFGeneralSerializer {

    /**
     * Serializes object headers (e.g. for Multipart message - Table Features)
     * @param input object whose headers should be serialized
     * @param outBuffer output buffer
     */
    void serializeHeader(SERIALIZER_TYPE input, ByteBuf outBuffer);
}
