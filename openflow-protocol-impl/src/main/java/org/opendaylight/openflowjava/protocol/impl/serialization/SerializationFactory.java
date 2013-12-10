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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class SerializationFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SerializationFactory.class);
    /**
     * Transforms POJO message into ByteBuf
     * @param version version used for encoding received message
     * @param out ByteBuf for storing and sending transformed message
     * @param message POJO message
     */
    public static <E extends DataObject> void messageToBuffer(short version, ByteBuf out, E message) {
        @SuppressWarnings("unchecked")
        MessageTypeKey<E> msgTypeKey = new MessageTypeKey<>(version, (Class<E>) message.getClass());
        OFSerializer<E> encoder = EncoderTable.getInstance().getEncoder(msgTypeKey);
        if (encoder != null) {
            encoder.messageToBuffer(version, out, message);
        } else {
            LOGGER.warn("No correct encoder found in EncoderTable for arguments: " + msgTypeKey.toString());
        }
    }
}
