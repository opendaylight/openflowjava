/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;

/**
 * Serializes ofp_match (OpenFlow v1.0) structure
 * @author michal.polkorab
 */
public abstract class OF10MatchSerializer {

    private static final byte PADDING_IN_MATCH = 1;
    private static final byte PADDING_IN_MATCH_2 = 2;
    
    /**
     * Encodes ofp_match (OpenFlow v1.0)
     * @param out output ByteBuf that match will be written into
     * @param match match to be encoded
     */
    public static void encodeMatchV10(ByteBuf out, MatchV10 match) {
        out.writeInt(match.getWildcards().intValue());
        out.writeShort(match.getInPort());
        out.writeBytes(match.getDlSrc().getValue().getBytes());
        out.writeBytes(match.getDlDst().getValue().getBytes());
        out.writeShort(match.getDlVlan());
        out.writeByte(match.getDlVlanPcp());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH, out);
        out.writeShort(match.getDlType());
        out.writeByte(match.getNwTos());
        out.writeByte(match.getNwProto());
        ByteBufUtils.padBuffer(PADDING_IN_MATCH_2, out);
        String[] srcGroups = match.getNwSrc().getValue().split(".");
        for (int i = 0; i < srcGroups.length; i++) {
            out.writeByte(Integer.parseInt(srcGroups[i]));
        }
        String[] dstGroups = match.getNwSrc().getValue().split(".");
        for (int i = 0; i < dstGroups.length; i++) {
            out.writeByte(Integer.parseInt(dstGroups[i]));
        }
        out.writeShort(match.getTpSrc());
        out.writeShort(match.getTpDst());
    }
    
}
