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

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowWildcardsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10Builder;

import com.google.common.base.Joiner;

/**
 * Deserializes ofp_match (OpenFlow v1.0) structure
 * @author michal.polkorab
 */
public abstract class OF10MatchDeserializer {
    
    private static final byte PADDING_IN_MATCH = 1;
    private static final byte PADDING_IN_MATCH_2 = 2;
    private static final byte NW_SRC_BITS = 6;
    private static final byte NW_SRC_SHIFT = 8;
    private static final int NW_SRC_MASK = ((1 << NW_SRC_BITS) - 1) << NW_SRC_SHIFT;
    private static final byte NW_DST_BITS = 6;
    private static final byte NW_DST_SHIFT = 14;
    private static final int NW_DST_MASK = ((1 << NW_DST_BITS) - 1) << NW_DST_SHIFT;

    /**
     * Creates ofp_match (OpenFlow v1.0) structure
     * @param rawMessage ByteBuf with input data
     * @return ofp_match (OpenFlow v1.0)
     */
    public static MatchV10 createMatchV10(ByteBuf rawMessage) {
        MatchV10Builder builder = new MatchV10Builder();
        long wildcards = rawMessage.readUnsignedInt();
        builder.setWildcards(createWildcards(wildcards));
        builder.setNwSrcMask(decodeNwSrcMask(wildcards));
        builder.setNwDstMask(decodeNwDstMask(wildcards));
        builder.setInPort(rawMessage.readUnsignedShort());
        byte[] dlSrc = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        rawMessage.readBytes(dlSrc);
        builder.setDlSrc(new MacAddress(ByteBufUtils.macAddressToString(dlSrc)));
        byte[] dlDst = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        rawMessage.readBytes(dlDst);
        builder.setDlDst(new MacAddress(ByteBufUtils.macAddressToString(dlDst)));

        builder.setDlVlan(rawMessage.readUnsignedShort());
        builder.setDlVlanPcp(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_MATCH);
        builder.setDlType(rawMessage.readUnsignedShort());
        builder.setNwTos(rawMessage.readUnsignedByte());
        builder.setNwProto(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_MATCH_2);
        List<String> srcGroups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV4_ADDRESS; i++) {
            srcGroups.add(Short.toString(rawMessage.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        builder.setNwSrc(new Ipv4Address(joiner.join(srcGroups)));
        List<String> dstGroups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV4_ADDRESS; i++) {
            dstGroups.add(Short.toString(rawMessage.readUnsignedByte()));
        }
        builder.setNwDst(new Ipv4Address(joiner.join(dstGroups)));
        builder.setTpSrc(rawMessage.readUnsignedShort());
        builder.setTpDst(rawMessage.readUnsignedShort());
        return builder.build();
    }
    
    private static FlowWildcardsV10 createWildcards(long input) {
        boolean _iNPORT = (input & (1 << 0)) != 0;
        boolean _dLVLAN = (input & (1 << 1)) != 0;
        boolean _dLSRC = (input & (1 << 2)) != 0;
        boolean _dLDST = (input & (1 << 3)) != 0;
        boolean _dLTYPE = (input & (1 << 4)) != 0;
        boolean _nWPROTO = (input & (1 << 5)) != 0;
        boolean _tPSRC = (input & (1 << 6)) != 0;
        boolean _tPDST = (input & (1 << 7)) != 0;
        boolean _dLVLANPCP = (input & (1 << 20)) != 0;
        boolean _nWTOS = (input & (1 << 21)) != 0;
        return new FlowWildcardsV10(_dLDST, _dLSRC, _dLTYPE, _dLVLAN,
                _dLVLANPCP, _iNPORT, _nWPROTO, _nWTOS, _tPDST, _tPSRC);
    }
    
    private static short decodeNwSrcMask(long input) {
        return (short) Math.max(32 - ((input & NW_SRC_MASK) >> NW_SRC_SHIFT), 0);
    }
    
    private static short decodeNwDstMask(long input) {
        return (short) Math.max(32 - ((input & NW_DST_MASK) >> NW_DST_SHIFT), 0);
    }
    
}
