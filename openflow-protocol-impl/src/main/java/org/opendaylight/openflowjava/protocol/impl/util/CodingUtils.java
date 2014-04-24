/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * Class for common serialization & deserialization operations
 * @author michal.polkorab
 */
public abstract class CodingUtils {

    /**
     * Serializes list
     * @param list list of items to be serialized
     * @param serializer serializer that can serialize list items
     * @param outBuffer output buffer
     */
    public static <T extends DataObject> void serializeList(List<T> list, OFSerializer<T> serializer, ByteBuf outBuffer){
        if (list != null) {
            for (T item : list) {
                serializer.serialize(item, outBuffer);
            }
        }
    }

    /**
     * Serializes action list
     * @param list actions to be serialized
     * @param serializer serializer that can serialize 
     * @param outBuffer output buffer
     */
    public static void serializeOF10Actions(List<Action> list,
            SerializerRegistry registry, ByteBuf outBuffer){
        if (list != null) {
            for (Action action : list) {
                OFSerializer<Action> serializer = registry.getSerializer(
                        new EnhancedMessageTypeKey<>(EncodeConstants.OF10_VERSION_ID,
                                Action.class, action.getType()));
                serializer.serialize(action, outBuffer);
            }
        }
    }

    /**
     * Serializes header fields for all objects in a list
     * @param list list of items to be serialized
     * @param serializer serializer that can serialize list items
     * @param outBuffer output buffer
     */
    public static <T extends DataObject> void serializeHeaders(List<T> list, HeaderSerializer<T> serializer, ByteBuf outBuffer){
        if (list != null) {
            for (T item : list) {
                serializer.serializeHeader(item, outBuffer);
            }
        }
    }
}
