/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10Builder;

import com.google.common.base.Joiner;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10MatchDeserializer {
    
    private static final byte MAC_ADDRESS_LENGTH = 6;
    private static final byte PADDING_IN_MATCH = 1;
    private static final byte PADDING_IN_MATCH_2 = 2;
    private static final byte GROUPS_IN_IPV4_ADDRESS = 4;

    /**
     * Creates match 
     * @param rawMessage
     * @return ofp_match (OpenFlow v1.0)
     */
    public static MatchV10 createMatchV10(ByteBuf rawMessage) {
        MatchV10Builder builder = new MatchV10Builder();
        builder.setWildcards(rawMessage.readUnsignedInt());
        builder.setInPort(rawMessage.readUnsignedShort());
        StringBuffer dlSrc = new StringBuffer();
        for(int i = 0; i < MAC_ADDRESS_LENGTH; i++){
            short mac = rawMessage.readUnsignedByte();
            dlSrc.append(String.format("%02X", mac));
        }
        builder.setDlSrc(new MacAddress(dlSrc.toString()));
        StringBuffer dlDst = new StringBuffer();
        for(int i = 0; i < MAC_ADDRESS_LENGTH; i++){
            short mac = rawMessage.readUnsignedByte();
            dlDst.append(String.format("%02X", mac));
        }
        builder.setDlDst(new MacAddress(dlDst.toString()));
        builder.setDlVlan(rawMessage.readUnsignedShort());
        builder.setDlVlanPcp(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_MATCH);
        builder.setDlType(rawMessage.readUnsignedShort());
        builder.setNwTos(rawMessage.readUnsignedByte());
        builder.setNwProto(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_MATCH_2);
        List<String> srcGroups = new ArrayList<>();
        for (int i = 0; i < GROUPS_IN_IPV4_ADDRESS; i++) {
            srcGroups.add(Short.toString(rawMessage.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        builder.setNwSrc(new Ipv4Address(joiner.join(srcGroups)));
        List<String> dstGroups = new ArrayList<>();
        for (int i = 0; i < GROUPS_IN_IPV4_ADDRESS; i++) {
            dstGroups.add(Short.toString(rawMessage.readUnsignedByte()));
        }
        builder.setNwSrc(new Ipv4Address(joiner.join(dstGroups)));
        builder.setTpSrc(rawMessage.readUnsignedShort());
        builder.setTpDst(rawMessage.readUnsignedShort());
        return builder.build();
    }
    
}
