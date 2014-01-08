/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpOp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Type;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv6Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv6Type;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpDscp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpEcn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.IpProto;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv4Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv4Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Exthdr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Flabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdSll;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdTarget;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdTll;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Metadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsBos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsLabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsTc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.PbbIsid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.SctpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TcpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.TunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.UdpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public abstract class MatchIdsWriter {

    /**
     * Encodes oxm headers (without values) 
     * @param entry match entry
     * @param out output ByteBuf
     */
    public static void encodeIdsRest(MatchEntries entry, ByteBuf out) {
        int fieldValue = 0;
        Class<? extends MatchField> field = entry.getOxmMatchField();
        if (field.isAssignableFrom(InPort.class)) {
            fieldValue = 0;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(InPhyPort.class)) {
            fieldValue = 1;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(Metadata.class)) {
            fieldValue = 2;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_LONG_IN_BYTES);
        } else if (field.isAssignableFrom(EthDst.class)) {
            fieldValue = 3;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(EthSrc.class)) {
            fieldValue = 4;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(EthType.class)) {
            fieldValue = 5;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(VlanVid.class)) {
            fieldValue = 6;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(VlanPcp.class)) {
            fieldValue = 7;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(IpDscp.class)) {
            fieldValue = 8;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(IpEcn.class)) {
            fieldValue = 9;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(IpProto.class)) {
            fieldValue = 10;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv4Src.class)) {
            fieldValue = 11;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv4Dst.class)) {
            fieldValue = 12;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(TcpSrc.class)) {
            fieldValue = 13;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(TcpDst.class)) {
            fieldValue = 14;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(UdpSrc.class)) {
            fieldValue = 15;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(UdpDst.class)) {
            fieldValue = 16;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(SctpSrc.class)) {
            fieldValue = 17;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(SctpDst.class)) {
            fieldValue = 18;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(Icmpv4Type.class)) {
            fieldValue = 19;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(Icmpv4Code.class)) {
            fieldValue = 20;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(ArpOp.class)) {
            fieldValue = 21;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(ArpSpa.class)) {
            fieldValue = 22;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(ArpTpa.class)) {
            fieldValue = 23;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(ArpSha.class)) {
            fieldValue = 24;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(ArpTha.class)) {
            fieldValue = 25;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(Ipv6Src.class)) {
            fieldValue = 26;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv6Dst.class)) {
            fieldValue = 27;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv6Flabel.class)) {
            fieldValue = 28;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(Icmpv6Type.class)) {
            fieldValue = 29;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(Icmpv6Code.class)) {
            fieldValue = 30;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv6NdTarget.class)) {
            fieldValue = 31;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv6NdSll.class)) {
            fieldValue = 32;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(Ipv6NdTll.class)) {
            fieldValue = 33;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.MAC_ADDRESS_LENGTH);
        } else if (field.isAssignableFrom(MplsLabel.class)) {
            fieldValue = 34;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(MplsTc.class)) {
            fieldValue = 35;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(MplsBos.class)) {
            fieldValue = 36;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        } else if (field.isAssignableFrom(PbbIsid.class)) {
            fieldValue = 37;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(), EncodeConstants.SIZE_OF_3_BYTES);
        } else if (field.isAssignableFrom(TunnelId.class)) {
            fieldValue = 38;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_LONG_IN_BYTES);
        } else if (field.isAssignableFrom(Ipv6Exthdr.class)) {
            fieldValue = 39;
            MatchSerializer.writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        }
    }

}
