/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowWildcardsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;

import com.google.common.base.Splitter;

/**
 * Serializes ofp_match (OpenFlow v1.0) structure
 * @author michal.polkorab
 */
public class OF10MatchSerializer implements OFSerializer<MatchV10> {

    private static final byte PADDING_IN_MATCH = 1;
    private static final byte PADDING_IN_MATCH_2 = 2;
    private static final byte NW_SRC_SHIFT = 8;
    private static final byte NW_DST_SHIFT = 14;

    /**
     * Serializes ofp_match (OpenFlow v1.0)
     * @param outBuffer output ByteBuf
     * @param match match to be serialized
     */
    @Override
    public void serialize(MatchV10 match, ByteBuf outBuffer) {
        outBuffer.writeInt(encodeWildcards(match.getWildcards(), match.getNwSrcMask(), match.getNwDstMask()));
        outBuffer.writeShort(match.getInPort());
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(match.getDlSrc().getValue()));
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(match.getDlDst().getValue()));
        outBuffer.writeShort(match.getDlVlan());
        outBuffer.writeByte(match.getDlVlanPcp());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH, outBuffer);
        outBuffer.writeShort(match.getDlType());
        outBuffer.writeByte(match.getNwTos());
        outBuffer.writeByte(match.getNwProto());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH_2, outBuffer);
        Iterable<String> srcGroups = Splitter.on(".").split(match.getNwSrc().getValue());
        for (String group : srcGroups) {
            outBuffer.writeByte(Short.parseShort(group));
        }
        Iterable<String> dstGroups = Splitter.on(".").split(match.getNwDst().getValue());
        for (String group : dstGroups) {
            outBuffer.writeByte(Short.parseShort(group));
        }
        outBuffer.writeShort(match.getTpSrc());
        outBuffer.writeShort(match.getTpDst());
    }

    private static int encodeWildcards(FlowWildcardsV10 wildcards, short srcMask, short dstMask) {
        int bitmask = 0;
        Map<Integer, Boolean> wildcardsMap = new HashMap<>();
        wildcardsMap.put(0, wildcards.isINPORT());
        wildcardsMap.put(1, wildcards.isDLVLAN());
        wildcardsMap.put(2, wildcards.isDLSRC());
        wildcardsMap.put(3, wildcards.isDLDST());
        wildcardsMap.put(4, wildcards.isDLTYPE());
        wildcardsMap.put(5, wildcards.isNWPROTO());
        wildcardsMap.put(6, wildcards.isTPSRC());
        wildcardsMap.put(7, wildcards.isTPDST());
        wildcardsMap.put(20, wildcards.isDLVLANPCP());
        wildcardsMap.put(21, wildcards.isNWTOS());
        bitmask = ByteBufUtils.fillBitMaskFromMap(wildcardsMap);
        bitmask |= ((32 - srcMask) << NW_SRC_SHIFT);
        bitmask |= ((32 - dstMask) << NW_DST_SHIFT);
        return bitmask;
    }

}
