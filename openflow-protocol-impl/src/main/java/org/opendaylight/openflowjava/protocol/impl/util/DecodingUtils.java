/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
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
import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public abstract class DecodingUtils {

    /**
     * Deserializes lists of actions or instructions
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param deserializer 
     *  false in case of whole body deserialization
     * @return List of actions or instructions
     */
    @SuppressWarnings("unchecked")
    public static <E extends DataObject> List<E> deserializeList(int length, ByteBuf input,
            OFGeneralDeserializer deserializer) {
        List<E> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        OFDeserializer<E> actionsDeserializer = (OFDeserializer<E>) deserializer;
        while ((input.readerIndex() - startIndex) < length) {
            list.add(actionsDeserializer.deserialize(input));
        }
        return list;
    }

    /**
     * Deserializes action or instruction headers
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param deserializer 
     *  false in case of whole body deserialization
     * @return List of action or instruction headers
     */
    @SuppressWarnings("unchecked")
    public static <E extends DataObject> List<E> deserializeHeaders(int length, ByteBuf input,
            OFGeneralDeserializer deserializer) {
        List<E> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        HeaderDeserializer<E> instructionsDeserializer = (HeaderDeserializer<E>) deserializer;
        while ((input.readerIndex() - startIndex) < length) {
            list.add(instructionsDeserializer.deserializeHeader(input));
        }
        return list;
    }

    /**
     * Deserializes match entry headers
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param registry registry with deserializers
     *  false in case of whole body deserialization
     * @return List of match entry headers
     */
    public static List<MatchEntries> deserializeMatchEntryHeaders(int length, ByteBuf input,
            DeserializerRegistry registry) {
        List<MatchEntries> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        while ((input.readerIndex() - startIndex) < length) {
            int oxmClass = input.getUnsignedShort(input.readerIndex());
            int oxmField = input.getUnsignedByte(input.readerIndex()
                    + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >> 1;
        HeaderDeserializer<MatchEntries> deserializer = registry.getDeserializer(
                new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID, oxmClass,
                        oxmField, MatchEntries.class));
        list.add(deserializer.deserializeHeader(input));
        }
        return list;
    }

    /**
     * Deserializes list of match entries
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param registry registry with deserializers
     *  false in case of whole body deserialization
     * @return List of match entries
     */
    public static List<MatchEntries> deserializeMatchEntries(int length, ByteBuf input,
            DeserializerRegistry registry) {
        List<MatchEntries> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        while ((input.readerIndex() - startIndex) < length) {
            int oxmClass = input.getUnsignedShort(input.readerIndex());
            int oxmField = input.getUnsignedByte(input.readerIndex()
                    + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >> 1;
        OFDeserializer<MatchEntries> deserializer = registry.getDeserializer(
                new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID, oxmClass,
                        oxmField, MatchEntries.class));
        list.add(deserializer.deserialize(input));
        }
        return list;
    }
}
