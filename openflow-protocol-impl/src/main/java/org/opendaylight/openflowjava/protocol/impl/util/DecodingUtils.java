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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public class DecodingUtils {

    /**
     * Deserializes list of actions
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param deserializer 
     * @param onlyHeaders true if only headers should be deserialized,
     *  false in case of whole body deserialization
     * @return List of actions
     */
    @SuppressWarnings("unchecked")
    public static List<Action> deserializeActions(int length, ByteBuf input,
            OFGeneralDeserializer deserializer, boolean onlyHeaders) {
        List<Action> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        if (onlyHeaders) {
            HeaderDeserializer<Action> actionsDeserializer = (HeaderDeserializer<Action>) deserializer;
            while ((input.readerIndex() - startIndex) < length) {
                list.add(actionsDeserializer.deserializeHeader(input));
            }
        } else {
            OFDeserializer<Action> actionsDeserializer = (OFDeserializer<Action>) deserializer;
            while ((input.readerIndex() - startIndex) < length) {
                list.add(actionsDeserializer.deserialize(input));
            }
        }
        return list;
    }

    /**
     * Deserializes list of instructions
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param deserializer 
     * @param onlyHeaders true if only headers should be deserialized,
     *  false in case of whole body deserialization
     * @return List of instructions
     */
    @SuppressWarnings("unchecked")
    public static List<Instruction> deserializeInstructions(int length, ByteBuf input,
            OFGeneralDeserializer deserializer, boolean onlyHeaders) {
        List<Instruction> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        if (onlyHeaders) {
            HeaderDeserializer<Instruction> instructionsDeserializer = (HeaderDeserializer<Instruction>) deserializer;
            while ((input.readerIndex() - startIndex) < length) {
                list.add(instructionsDeserializer.deserializeHeader(input));
            }
        } else {
            OFDeserializer<Instruction> instructionsDeserializer = (OFDeserializer<Instruction>) deserializer;
            while ((input.readerIndex() - startIndex) < length) {
                list.add(instructionsDeserializer.deserialize(input));
            }
        }
        return list;
    }

    /**
     * Deserializes list of actions
     * @param length length of list (in bytes)
     * @param input input bytebuf
     * @param registry registry with deserializers
     * @param onlyHeaders true if only headers should be deserialized,
     *  false in case of whole body deserialization
     * @return List of actions
     */
    public static List<MatchEntries> deserializeMatchEntries(int length, ByteBuf input,
            DeserializerRegistry registry, boolean onlyHeaders) {
        List<MatchEntries> list = new ArrayList<>();
        int startIndex = input.readerIndex();
        if (onlyHeaders) {
            while ((input.readerIndex() - startIndex) < length) {
                int oxmClass = input.getUnsignedShort(input.readerIndex());
                int oxmField = input.getUnsignedByte(input.readerIndex()
                        + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >> 1;
                HeaderDeserializer<MatchEntries> deserializer = registry.getDeserializer(
                        new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID, oxmClass,
                                oxmField, MatchEntries.class));
                list.add(deserializer.deserializeHeader(input));
            }
        } else {
            while ((input.readerIndex() - startIndex) < length) {
                int oxmClass = input.getUnsignedShort(input.readerIndex());
                int oxmField = input.getUnsignedByte(input.readerIndex()
                        + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >> 1;
                OFDeserializer<MatchEntries> deserializer = registry.getDeserializer(
                        new EnhancedMessageCodeKey(EncodeConstants.OF13_VERSION_ID, oxmClass,
                                oxmField, MatchEntries.class));
                list.add(deserializer.deserialize(input));
            }
        }
        return list;
    }
}
