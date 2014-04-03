/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.deserialization.EnhancedMessageTypeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.StandardMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes ofp_match (OpenFlow v1.3) and its oxm_fields structures
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF13MatchSerializer implements OFSerializer<Match> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OF13MatchSerializer.class);
    private static final byte STANDARD_MATCH_TYPE_CODE = 0;
    private static final byte OXM_MATCH_TYPE_CODE = 1;
    private SerializerTable serializerTable;

    @Override
    public void serialize(Match object, ByteBuf outBuffer) {
        if (object == null) {
            LOGGER.debug("Match is null");
            return;
        }
        int matchStartIndex = outBuffer.writerIndex();
        serializeType(object, outBuffer);
        int matchLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        serializeMatchEntries(object.getMatchEntries(), outBuffer);
        // Length of ofp_match (excluding padding)
        int matchLength = outBuffer.writerIndex() - matchStartIndex;
        outBuffer.setShort(matchLengthIndex, matchLength);
        int paddingRemainder = matchLength % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            ByteBufUtils.padBuffer(EncodeConstants.PADDING - paddingRemainder, outBuffer);
        }
    }

    private static void serializeType(Match match, ByteBuf out) {
        if (match.getType().isAssignableFrom(StandardMatchType.class)) {
            out.writeShort(STANDARD_MATCH_TYPE_CODE);
        } else if (match.getType().isAssignableFrom(OxmMatchType.class)) {
            out.writeShort(OXM_MATCH_TYPE_CODE);
        }
    }

    /**
     * Encodes MatchEntries
     * @param matchEntries list of match entries (oxm_fields)
     * @param out output ByteBuf
     */
    public void serializeMatchEntries(List<MatchEntries> matchEntries, ByteBuf out) {
        if (matchEntries == null) {
            LOGGER.debug("Match entries are null");
            return;
        }
        for (MatchEntries entry : matchEntries) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            OFSerializer<MatchEntries> entrySerializer = serializerTable.getSerializer(
                    new EnhancedMessageTypeKey(EncodeConstants.OF13_VERSION_ID, entry.getOxmClass(),
                            entry.getOxmMatchField()));
            entrySerializer.serialize(entry, out);
        }
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }

}
