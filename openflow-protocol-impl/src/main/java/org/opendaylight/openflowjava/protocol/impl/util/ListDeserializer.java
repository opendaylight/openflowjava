/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class ListDeserializer {

    /**
     * @param length
     * @param version 
     * @param input
     * @param keyMaker 
     * @param registry
     * @return
     */
    public static <E extends DataObject> List<E> deserializeList(short version, int length,
            ByteBuf input, CodeKeyMaker keyMaker, DeserializerRegistry registry) {
        List<E> items = null;
        if (input.readableBytes() > 0) {
            items = new ArrayList<>();
            int startIndex = input.readerIndex();
            while ((input.readerIndex() - startIndex) < length){
                OFDeserializer<E> deserializer = registry.getDeserializer(keyMaker.make(input));
                E item = deserializer.deserialize(input);
                items.add(item);
            }
        }
        return items;
    }
}
