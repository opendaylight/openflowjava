/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowWildcardsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;

/**
 * Serializes ofp_match (OpenFlow v1.0) structure
 * @author michal.polkorab
 */
public abstract class OF10MatchSerializer {

    private static final byte PADDING_IN_MATCH = 1;
    private static final byte PADDING_IN_MATCH_2 = 2;
    private static final byte NW_SRC_SHIFT = 8;
    private static final byte NW_DST_SHIFT = 14;
    private static final int ALL = ((1 << 22) - 1);
    
    /**
     * Encodes ofp_match (OpenFlow v1.0)
     * @param out output ByteBuf that match will be written into
     * @param match match to be encoded
     */
    public static void encodeMatchV10(ByteBuf out, MatchV10 match) {
        out.writeInt(encodeWildcards(match.getWildcards(), match.getNwSrcMask(), match.getNwDstMask()));
        out.writeShort(match.getInPort());
        out.writeBytes(ByteBufUtils.macAddressToBytes(match.getDlSrc().getValue()));
        out.writeBytes(ByteBufUtils.macAddressToBytes(match.getDlDst().getValue()));
        out.writeShort(match.getDlVlan());
        out.writeByte(match.getDlVlanPcp());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH, out);
        out.writeShort(match.getDlType());
        out.writeByte(match.getNwTos());
        out.writeByte(match.getNwProto());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH_2, out);
        String[] srcGroups = match.getNwSrc().getValue().split("\\.");
        for (int i = 0; i < srcGroups.length; i++) {
            out.writeByte(Integer.parseInt(srcGroups[i]));
        }
        String[] dstGroups = match.getNwDst().getValue().split("\\.");
        for (int i = 0; i < dstGroups.length; i++) {
            out.writeByte(Integer.parseInt(dstGroups[i]));
        }
        out.writeShort(match.getTpSrc());
        out.writeShort(match.getTpDst());
    }
    
    private static int encodeWildcards(FlowWildcardsV10 wildcards, short srcMask, short dstMask) {
        int bitmask = 0;
        if (wildcards.isALL()) {
            bitmask |= ALL;
        } else {
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
        }
        return bitmask;
    }
    
}
