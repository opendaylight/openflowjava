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

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Dscp;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6FlowLabel;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.BosMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.BosMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DscpMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DscpMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EcnMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EcnMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthTypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthTypeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6CodeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6TypeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6FlabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6FlabelMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IsidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IsidMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MacAddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MacAddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsLabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsLabelMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OpCodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OpCodeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ProtocolNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ProtocolNumberMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.StandardMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpOp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.EthType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Metadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsBos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsLabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsTc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm1Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmMatchType;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.match.grouping.MatchBuilder;

import com.google.common.base.Joiner;

/**
 * Deserializes ofp_match (OpenFlow v1.3) and its oxm_fields structures
 * @author timotej.kubas
 * @author michal.polkorab
 */
public abstract class MatchDeserializer {

    /**
     * Creates match
     * @param in input ByteBuf
     * @return ofp_match (OpenFlow v1.3)
     */
    public static Match createMatch(ByteBuf in) {
        if (in.readableBytes() > 0) {
            MatchBuilder builder = new MatchBuilder();
            int type = in.readUnsignedShort();
            int length = in.readUnsignedShort();
            switch (type) {
            case 0:
                builder.setType(StandardMatchType.class);
                break;
            case 1:
                builder.setType(OxmMatchType.class);
                break;
            default:
                break;
            }
            builder.setMatchEntries(createMatchEntries(in, length - 2 * (EncodeConstants.SIZE_OF_SHORT_IN_BYTES)));
            int paddingRemainder = length % EncodeConstants.PADDING;
            if (paddingRemainder != 0) {
                in.skipBytes(EncodeConstants.PADDING - paddingRemainder);
            }
            return builder.build();
        }
        return null;
    }

    /**
     * Deserializes single match entry (oxm_field)
     * @param in input ByteBuf
     * @param matchLength length of match entry
     * @return MatchEntriesList list containing one match entry
     */
    public static List<MatchEntries> createMatchEntry(ByteBuf in, int matchLength) {
        return createMatchEntriesInternal(in, matchLength, true);
    }

    /**
     * @param in input ByteBuf
     * @param matchLength length of match entries
     * @return MatchEntriesList list of match entries
     */
    public static List<MatchEntries> createMatchEntries(ByteBuf in, int matchLength) {
        return createMatchEntriesInternal(in, matchLength, false);
    }

    private static List<MatchEntries> createMatchEntriesInternal(ByteBuf in, int matchLength, boolean oneEntry) {
        List<MatchEntries> matchEntriesList = new ArrayList<>();
        int currLength = 0;
        while(currLength < matchLength) {
            MatchEntriesBuilder matchEntriesBuilder = new MatchEntriesBuilder();
            switch (in.readUnsignedShort()) {
            case 0x0000:
                        matchEntriesBuilder.setOxmClass(Nxm0Class.class);
                        break;
            case 0x0001:
                        matchEntriesBuilder.setOxmClass(Nxm1Class.class);
                        break;
            case 0x8000:
                        matchEntriesBuilder.setOxmClass(OpenflowBasicClass.class);
                        break;
            case 0xFFFF:
                        matchEntriesBuilder.setOxmClass(ExperimenterClass.class);
                        break;
            default:
                        break;
            }

            int fieldAndMask = in.readUnsignedByte();
            boolean hasMask = (fieldAndMask & 1) != 0;
            matchEntriesBuilder.setHasMask(hasMask);
            int matchField =  fieldAndMask >> 1;
            int matchEntryLength = in.readUnsignedByte();
            currLength += EncodeConstants.SIZE_OF_SHORT_IN_BYTES +
                    (2 * EncodeConstants.SIZE_OF_BYTE_IN_BYTES) + matchEntryLength;

            switch(matchField) {
            case 0:
                matchEntriesBuilder.setOxmMatchField(InPort.class);
                PortNumberMatchEntryBuilder port = new PortNumberMatchEntryBuilder();
                port.setPortNumber(new PortNumber(in.readUnsignedInt()));
                matchEntriesBuilder.addAugmentation(PortNumberMatchEntry.class, port.build());
                break;
            case 1:
                matchEntriesBuilder.setOxmMatchField(InPhyPort.class);
                PortNumberMatchEntryBuilder phyPort = new PortNumberMatchEntryBuilder();
                phyPort.setPortNumber(new PortNumber(in.readUnsignedInt()));
                matchEntriesBuilder.addAugmentation(PortNumberMatchEntry.class, phyPort.build());
                break;
            case 2:
                matchEntriesBuilder.setOxmMatchField(Metadata.class);
                addMetadataAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_LONG_IN_BYTES);
                }
                break;
            case 3:
                matchEntriesBuilder.setOxmMatchField(EthDst.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.MAC_ADDRESS_LENGTH);
                }
                break;
            case 4:
                matchEntriesBuilder.setOxmMatchField(EthSrc.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.MAC_ADDRESS_LENGTH);
                }
                break;
            case 5:
                matchEntriesBuilder.setOxmMatchField(EthType.class);
                EthTypeMatchEntryBuilder ethertypeBuilder = new EthTypeMatchEntryBuilder();
                ethertypeBuilder.setEthType(new EtherType(in.readUnsignedShort()));
                matchEntriesBuilder.addAugmentation(EthTypeMatchEntry.class, ethertypeBuilder.build());
                break;
            case 6:
                matchEntriesBuilder.setOxmMatchField(VlanVid.class);
                VlanVidMatchEntryBuilder vlanVidBuilder = new VlanVidMatchEntryBuilder();
                int vidEntryValue = in.readUnsignedShort();
                vlanVidBuilder.setCfiBit((vidEntryValue & (1 << 12)) != 0); // cfi is 13-th bit
                vlanVidBuilder.setVlanVid(vidEntryValue & ((1 << 12) - 1)); // value without 13-th bit
                matchEntriesBuilder.addAugmentation(VlanVidMatchEntry.class, vlanVidBuilder.build());
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                }
                break;
            case 7:
                matchEntriesBuilder.setOxmMatchField(VlanPcp.class);
                VlanPcpMatchEntryBuilder vlanPcpBuilder = new VlanPcpMatchEntryBuilder();
                vlanPcpBuilder.setVlanPcp(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(VlanPcpMatchEntry.class, vlanPcpBuilder.build());
                break;
            case 8:
                matchEntriesBuilder.setOxmMatchField(IpDscp.class);
                DscpMatchEntryBuilder dscpBuilder = new DscpMatchEntryBuilder();
                dscpBuilder.setDscp(new Dscp(in.readUnsignedByte()));
                matchEntriesBuilder.addAugmentation(DscpMatchEntry.class, dscpBuilder.build());
                break;
            case 9:
                matchEntriesBuilder.setOxmMatchField(IpEcn.class);
                EcnMatchEntryBuilder ecnBuilder = new EcnMatchEntryBuilder();
                ecnBuilder.setEcn(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(EcnMatchEntry.class, ecnBuilder.build());
                break;
            case 10:
                matchEntriesBuilder.setOxmMatchField(IpProto.class);
                ProtocolNumberMatchEntryBuilder protoNumberBuilder = new ProtocolNumberMatchEntryBuilder();
                protoNumberBuilder.setProtocolNumber(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(ProtocolNumberMatchEntry.class, protoNumberBuilder.build());
                break;
            case 11:
                matchEntriesBuilder.setOxmMatchField(Ipv4Src.class);
                addIpv4AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                }
                break;
            case 12:
                matchEntriesBuilder.setOxmMatchField(Ipv4Dst.class);
                addIpv4AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                }
                break;
            case 13:
                matchEntriesBuilder.setOxmMatchField(TcpSrc.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 14:
                matchEntriesBuilder.setOxmMatchField(TcpDst.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 15:
                matchEntriesBuilder.setOxmMatchField(UdpSrc.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 16:
                matchEntriesBuilder.setOxmMatchField(UdpDst.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 17:
                matchEntriesBuilder.setOxmMatchField(SctpSrc.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 18:
                matchEntriesBuilder.setOxmMatchField(SctpDst.class);
                addPortAugmentation(matchEntriesBuilder, in);
                break;
            case 19:
                matchEntriesBuilder.setOxmMatchField(Icmpv4Type.class);
                Icmpv4TypeMatchEntryBuilder icmpv4TypeBuilder = new Icmpv4TypeMatchEntryBuilder();
                icmpv4TypeBuilder.setIcmpv4Type(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(Icmpv4TypeMatchEntry.class, icmpv4TypeBuilder.build());
                break;
            case 20:
                matchEntriesBuilder.setOxmMatchField(Icmpv4Code.class);
                Icmpv4CodeMatchEntryBuilder icmpv4CodeBuilder = new Icmpv4CodeMatchEntryBuilder();
                icmpv4CodeBuilder.setIcmpv4Code(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(Icmpv4CodeMatchEntry.class, icmpv4CodeBuilder.build());
                break;
            case 21:
                matchEntriesBuilder.setOxmMatchField(ArpOp.class);
                OpCodeMatchEntryBuilder opcodeBuilder = new OpCodeMatchEntryBuilder();
                opcodeBuilder.setOpCode(in.readUnsignedShort());
                matchEntriesBuilder.addAugmentation(OpCodeMatchEntry.class, opcodeBuilder.build());
                break;
            case 22:
                matchEntriesBuilder.setOxmMatchField(ArpSpa.class);
                addIpv4AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                }
                break;
            case 23:
                matchEntriesBuilder.setOxmMatchField(ArpTpa.class);
                addIpv4AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                }
                break;
            case 24:
                matchEntriesBuilder.setOxmMatchField(ArpSha.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.MAC_ADDRESS_LENGTH);
                }
                break;
            case 25:
                matchEntriesBuilder.setOxmMatchField(ArpTha.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.MAC_ADDRESS_LENGTH);
                }
                break;
            case 26:
                matchEntriesBuilder.setOxmMatchField(Ipv6Src.class);
                addIpv6AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
                }
                break;
            case 27:
                matchEntriesBuilder.setOxmMatchField(Ipv6Dst.class);
                addIpv6AddressAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
                }
                break;
            case 28:
                matchEntriesBuilder.setOxmMatchField(Ipv6Flabel.class);
                Ipv6FlabelMatchEntryBuilder ipv6FlabelBuilder = new Ipv6FlabelMatchEntryBuilder();
                ipv6FlabelBuilder.setIpv6Flabel(new Ipv6FlowLabel(in.readUnsignedInt()));
                matchEntriesBuilder.addAugmentation(Ipv6FlabelMatchEntry.class, ipv6FlabelBuilder.build());
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                }
                break;
            case 29:
                matchEntriesBuilder.setOxmMatchField(Icmpv6Type.class);
                Icmpv6TypeMatchEntryBuilder icmpv6TypeBuilder = new Icmpv6TypeMatchEntryBuilder();
                icmpv6TypeBuilder.setIcmpv6Type(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(Icmpv6TypeMatchEntry.class, icmpv6TypeBuilder.build());
                break;
            case 30:
                matchEntriesBuilder.setOxmMatchField(Icmpv6Code.class);
                Icmpv6CodeMatchEntryBuilder icmpv6CodeBuilder = new Icmpv6CodeMatchEntryBuilder();
                icmpv6CodeBuilder.setIcmpv6Code(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(Icmpv6CodeMatchEntry.class, icmpv6CodeBuilder.build());
                break;
            case 31:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdTarget.class);
                addIpv6AddressAugmentation(matchEntriesBuilder, in);
                break;
            case 32:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdSll.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                break;
            case 33:
                matchEntriesBuilder.setOxmMatchField(Ipv6NdTll.class);
                addMacAddressAugmentation(matchEntriesBuilder, in);
                break;
            case 34:
                matchEntriesBuilder.setOxmMatchField(MplsLabel.class);
                MplsLabelMatchEntryBuilder mplsLabelBuilder = new MplsLabelMatchEntryBuilder();
                mplsLabelBuilder.setMplsLabel(in.readUnsignedInt());
                matchEntriesBuilder.addAugmentation(MplsLabelMatchEntry.class, mplsLabelBuilder.build());
                break;
            case 35:
                matchEntriesBuilder.setOxmMatchField(MplsTc.class);
                TcMatchEntryBuilder tcBuilder = new TcMatchEntryBuilder();
                tcBuilder.setTc(in.readUnsignedByte());
                matchEntriesBuilder.addAugmentation(TcMatchEntry.class, tcBuilder.build());
                break;
            case 36:
                matchEntriesBuilder.setOxmMatchField(MplsBos.class);
                BosMatchEntryBuilder bosBuilder = new BosMatchEntryBuilder();
                if (in.readUnsignedByte() != 0) {
                    bosBuilder.setBos(true);
                } else {
                    bosBuilder.setBos(false);
                }
                matchEntriesBuilder.addAugmentation(BosMatchEntry.class, bosBuilder.build());
                break;
            case 37:
                matchEntriesBuilder.setOxmMatchField(PbbIsid.class);
                IsidMatchEntryBuilder isidBuilder = new IsidMatchEntryBuilder();
                Integer isid = in.readUnsignedMedium();
                isidBuilder.setIsid(isid.longValue());
                matchEntriesBuilder.addAugmentation(IsidMatchEntry.class, isidBuilder.build());
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_3_BYTES);
                }
                break;
            case 38:
                matchEntriesBuilder.setOxmMatchField(TunnelId.class);
                addMetadataAugmentation(matchEntriesBuilder, in);
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_LONG_IN_BYTES);
                }
                break;
            case 39:
                matchEntriesBuilder.setOxmMatchField(Ipv6Exthdr.class);
                PseudoFieldMatchEntryBuilder pseudoBuilder = new PseudoFieldMatchEntryBuilder();
                int bitmap = in.readUnsignedShort();
                final Boolean NONEXT = ((bitmap) & (1<<0)) != 0;
                final Boolean ESP = ((bitmap) & (1<<1)) != 0;
                final Boolean AUTH = ((bitmap) & (1<<2)) != 0;
                final Boolean DEST = ((bitmap) & (1<<3)) != 0;
                final Boolean FRAG = ((bitmap) & (1<<4)) != 0;
                final Boolean ROUTER = ((bitmap) & (1<<5)) != 0;
                final Boolean HOP = ((bitmap) & (1<<6)) != 0;
                final Boolean UNREP = ((bitmap) & (1<<7)) != 0;
                final Boolean UNSEQ = ((bitmap) & (1<<8)) != 0;
                pseudoBuilder.setPseudoField(new Ipv6ExthdrFlags(AUTH, DEST, ESP, FRAG, HOP, NONEXT, ROUTER, UNREP, UNSEQ));
                matchEntriesBuilder.addAugmentation(PseudoFieldMatchEntry.class, pseudoBuilder.build());
                if (hasMask) {
                    addMaskAugmentation(matchEntriesBuilder, in, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                }
                break;
            default:
                break;
            }
          matchEntriesList.add(matchEntriesBuilder.build());
          if (oneEntry) {
              break;
          }
        }
        if ((matchLength - currLength) > 0) {
            in.skipBytes(matchLength - currLength);
        }
        return matchEntriesList;
    }

    private static void addMaskAugmentation(MatchEntriesBuilder builder, ByteBuf in, int matchEntryLength) {
        MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
        byte[] mask = new byte[matchEntryLength];
        in.readBytes(mask);
        maskBuilder.setMask(mask);
        builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
    }

    private static void addIpv6AddressAugmentation(MatchEntriesBuilder builder, ByteBuf in) {
        Ipv6AddressMatchEntryBuilder ipv6AddressBuilder = new Ipv6AddressMatchEntryBuilder();
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV6_ADDRESS; i++) {
            groups.add(String.format("%04X", in.readUnsignedShort()));
        }
        Joiner joiner = Joiner.on(":");
        ipv6AddressBuilder.setIpv6Address(new Ipv6Address(joiner.join(groups)));
        builder.addAugmentation(Ipv6AddressMatchEntry.class, ipv6AddressBuilder.build());
    }

    private static void addMetadataAugmentation(MatchEntriesBuilder builder, ByteBuf in) {
        MetadataMatchEntryBuilder metadata = new MetadataMatchEntryBuilder();
        byte[] metadataBytes = new byte[Long.SIZE/Byte.SIZE];
        in.readBytes(metadataBytes);
        metadata.setMetadata(metadataBytes);
        builder.addAugmentation(MetadataMatchEntry.class, metadata.build());
    }

    private static void addIpv4AddressAugmentation(MatchEntriesBuilder builder, ByteBuf in) {
        Ipv4AddressMatchEntryBuilder ipv4AddressBuilder = new Ipv4AddressMatchEntryBuilder();
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV4_ADDRESS; i++) {
            groups.add(Short.toString(in.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        ipv4AddressBuilder.setIpv4Address(new Ipv4Address(joiner.join(groups)));
        builder.addAugmentation(Ipv4AddressMatchEntry.class, ipv4AddressBuilder.build());
    }

    private static void addMacAddressAugmentation(MatchEntriesBuilder builder, ByteBuf in) {
        MacAddressMatchEntryBuilder macAddress = new MacAddressMatchEntryBuilder();
        byte[] address = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        in.readBytes(address);
        macAddress.setMacAddress(new MacAddress(ByteBufUtils.macAddressToString(address)));
        builder.addAugmentation(MacAddressMatchEntry.class, macAddress.build());
    }

    private static void addPortAugmentation(MatchEntriesBuilder builder, ByteBuf in) {
        PortMatchEntryBuilder portBuilder = new PortMatchEntryBuilder();
        portBuilder.setPort(new org.opendaylight.yang.gen.v1.urn.ietf.params.
                xml.ns.yang.ietf.inet.types.rev100924.PortNumber(in.readUnsignedShort()));
        builder.addAugmentation(PortMatchEntry.class, portBuilder.build());
    }
}
