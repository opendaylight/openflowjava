/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.BosMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DscpMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EcnMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthTypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6FlabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IsidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MacAddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsLabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OpCodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ProtocolNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.StandardMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpOp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpSpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTha;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes ofp_match (OpenFlow v1.3) and its oxm_fields structures
 * @author michal.polkorab
 * @author timotej.kubas
 */
public abstract class MatchSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchSerializer.class);
    private static final byte STANDARD_MATCH_TYPE_CODE = 0;
    private static final byte OXM_MATCH_TYPE_CODE = 1;
    private static final int NXM0_CLASS_CODE = 0x0000;
    private static final int NXM1_CLASS_CODE = 0x0001;
    private static final int OPENFLOW_BASIC_CLASS_CODE = 0x8000;
    private static final int EXPERIMENTER_CLASS_CODE = 0xFFFF;
    private static final byte MATCH_TYPE_AND_LENGTH_SIZE = 4;
    private static final byte MATCH_ENTRY_HEADER_LENGTH = 4;

    /**
     * Encodes match (OpenFlow v1.3)
     * @param match ofp_match object
     * @param out output ByteBuf
     */
    public static void encodeMatch(Match match, ByteBuf out) {
        if (match == null) {
            LOGGER.debug("Match is null");
            return;
        }
        encodeType(match, out);
        // Length of ofp_match (excluding padding)
        int length = computeMatchLengthInternal(match);
        out.writeShort(length);
        encodeMatchEntries(match.getMatchEntries(), out);
        int paddingRemainder = length % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            ByteBufUtils.padBuffer(EncodeConstants.PADDING - paddingRemainder, out);
        }
    }

    private static void encodeType(Match match, ByteBuf out) {
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
    public static void encodeMatchEntries(List<MatchEntries> matchEntries, ByteBuf out) {
        if (matchEntries == null) {
            LOGGER.debug("Match entries are null");
            return;
        }
        for (MatchEntries entry : matchEntries) {
            encodeClass(entry.getOxmClass(), out);
            encodeRest(entry, out);
        }
    }

    /**
     * Encodes OXM ids (for Multipart - TableFeatures messages)
     * @param matchEntries list of match entries (oxm_fields)
     * @param out output ByteBuf
     */
    public static void encodeMatchIds(List<MatchEntries> matchEntries, ByteBuf out) {
        if (matchEntries == null) {
            LOGGER.debug("Match entries are null");
            return;
        }
        for (MatchEntries entry : matchEntries) {
            encodeClass(entry.getOxmClass(), out);
            MatchIdsSerializer.encodeIdsRest(entry, out);
        }
    }

    private static void encodeClass(Class<? extends Clazz> clazz, ByteBuf out) {
        if (clazz.isAssignableFrom(Nxm0Class.class)) {
            out.writeShort(NXM0_CLASS_CODE);
        } else if (clazz.isAssignableFrom(Nxm1Class.class)) {
            out.writeShort(NXM1_CLASS_CODE);
        } else if (clazz.isAssignableFrom(OpenflowBasicClass.class)) {
            out.writeShort(OPENFLOW_BASIC_CLASS_CODE);
        } else if (clazz.isAssignableFrom(ExperimenterClass.class)) {
            out.writeShort(EXPERIMENTER_CLASS_CODE);
        }
    }

    private static void encodeRest(MatchEntries entry, ByteBuf out) {
        int fieldValue = 0;
        Class<? extends MatchField> field = entry.getOxmMatchField();
        if (field.isAssignableFrom(InPort.class)) {
            fieldValue = 0;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
            out.writeInt(entry.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        } else if (field.isAssignableFrom(InPhyPort.class)) {
            fieldValue = 1;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
            out.writeInt(entry.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        } else if (field.isAssignableFrom(Metadata.class)) {
            fieldValue = 2;
            writeMetadataRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(EthDst.class)) {
            fieldValue = 3;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(EthSrc.class)) {
            fieldValue = 4;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(EthType.class)) {
            fieldValue = 5;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(EthTypeMatchEntry.class).getEthType().getValue().shortValue());
        } else if (field.isAssignableFrom(VlanVid.class)) {
            fieldValue = 6;
            VlanVidMatchEntry vlanVid = entry.getAugmentation(VlanVidMatchEntry.class);
            int vlanVidValue = vlanVid.getVlanVid();
            if (vlanVid.isCfiBit()) {
                short cfi = 1 << 12; // 13-th bit
                vlanVidValue = vlanVidValue | cfi;
            }

            writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(vlanVidValue);
            writeMask(entry, out, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        } else if (field.isAssignableFrom(VlanPcp.class)) {
            fieldValue = 7;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(VlanPcpMatchEntry.class).getVlanPcp().byteValue());
        } else if (field.isAssignableFrom(IpDscp.class)) {
            fieldValue = 8;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(DscpMatchEntry.class).getDscp().getValue());
        } else if (field.isAssignableFrom(IpEcn.class)) {
            fieldValue = 9;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(EcnMatchEntry.class).getEcn());
        } else if (field.isAssignableFrom(IpProto.class)) {
            fieldValue = 10;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(ProtocolNumberMatchEntry.class).getProtocolNumber());
        } else if (field.isAssignableFrom(Ipv4Src.class)) {
            fieldValue = 11;
            writeIpv4AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv4Dst.class)) {
            fieldValue = 12;
            writeIpv4AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(TcpSrc.class)) {
            fieldValue = 13;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(TcpDst.class)) {
            fieldValue = 14;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(UdpSrc.class)) {
            fieldValue = 15;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(UdpDst.class)) {
            fieldValue = 16;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(SctpSrc.class)) {
            fieldValue = 17;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(SctpDst.class)) {
            fieldValue = 18;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.isAssignableFrom(Icmpv4Type.class)) {
            fieldValue = 19;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(Icmpv4TypeMatchEntry.class).getIcmpv4Type());
        } else if (field.isAssignableFrom(Icmpv4Code.class)) {
            fieldValue = 20;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(Icmpv4CodeMatchEntry.class).getIcmpv4Code());
        } else if (field.isAssignableFrom(ArpOp.class)) {
            fieldValue = 21;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(entry.getAugmentation(OpCodeMatchEntry.class).getOpCode());
        } else if (field.isAssignableFrom(ArpSpa.class)) {
            fieldValue = 22;
            writeIpv4AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(ArpTpa.class)) {
            fieldValue = 23;
            writeIpv4AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(ArpSha.class)) {
            fieldValue = 24;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(ArpTha.class)) {
            fieldValue = 25;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6Src.class)) {
            fieldValue = 26;
            writeIpv6AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6Dst.class)) {
            fieldValue = 27;
            writeIpv6AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6Flabel.class)) {
            fieldValue = 28;
            writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_INT_IN_BYTES);
            out.writeInt(entry.getAugmentation(Ipv6FlabelMatchEntry.class).getIpv6Flabel().getValue().intValue());
            writeMask(entry, out, EncodeConstants.SIZE_OF_INT_IN_BYTES);
        } else if (field.isAssignableFrom(Icmpv6Type.class)) {
            fieldValue = 29;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(Icmpv6TypeMatchEntry.class).getIcmpv6Type());
        } else if (field.isAssignableFrom(Icmpv6Code.class)) {
            fieldValue = 30;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(Icmpv6CodeMatchEntry.class).getIcmpv6Code());
        } else if (field.isAssignableFrom(Ipv6NdTarget.class)) {
            fieldValue = 31;
            writeIpv6AddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6NdSll.class)) {
            fieldValue = 32;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6NdTll.class)) {
            fieldValue = 33;
            writeMacAddressRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(MplsLabel.class)) {
            fieldValue = 34;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_INT_IN_BYTES);
            out.writeInt(entry.getAugmentation(MplsLabelMatchEntry.class).getMplsLabel().intValue());
        } else if (field.isAssignableFrom(MplsTc.class)) {
            fieldValue = 35;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeByte(entry.getAugmentation(TcMatchEntry.class).getTc());
        } else if (field.isAssignableFrom(MplsBos.class)) {
            fieldValue = 36;
            writeOxmFieldAndLength(out, fieldValue, false, EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            out.writeBoolean(entry.getAugmentation(BosMatchEntry.class).isBos().booleanValue());
        } else if (field.isAssignableFrom(PbbIsid.class)) {
            fieldValue = 37;
            writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(), EncodeConstants.SIZE_OF_3_BYTES);
            out.writeMedium(entry.getAugmentation(IsidMatchEntry.class).getIsid().intValue());
            writeMask(entry, out, EncodeConstants.SIZE_OF_3_BYTES);
        } else if (field.isAssignableFrom(TunnelId.class)) {
            fieldValue = 38;
            writeMetadataRelatedEntry(entry, out, fieldValue);
        } else if (field.isAssignableFrom(Ipv6Exthdr.class)) {
            fieldValue = 39;
            Ipv6ExthdrFlags pseudoField = entry.getAugmentation(PseudoFieldMatchEntry.class).getPseudoField();
            Map<Integer, Boolean> map = new HashMap<>();
            map.put(0, pseudoField.isNonext());
            map.put(1, pseudoField.isEsp());
            map.put(2, pseudoField.isAuth());
            map.put(3, pseudoField.isDest());
            map.put(4, pseudoField.isFrag());
            map.put(5, pseudoField.isRouter());
            map.put(6, pseudoField.isHop());
            map.put(7, pseudoField.isUnrep());
            map.put(8, pseudoField.isUnseq());
            int bitmap = ByteBufUtils.fillBitMaskFromMap(map);

            writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                    EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            out.writeShort(bitmap);
            writeMask(entry, out, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        }
    }

    private static void writeMask(MatchEntries entry, ByteBuf out, int length) {
        if (entry.isHasMask()) {
            byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
            if (mask != null && mask.length != length) {
                throw new IllegalArgumentException("incorrect length of mask: "+
                        mask.length + ", expected: " + length);
            }
            out.writeBytes(mask);
        }
    }

    protected static void writeOxmFieldAndLength(ByteBuf out, int fieldValue, boolean hasMask, int lengthArg) {
        int fieldAndMask = fieldValue << 1;
        int length = lengthArg;
        if (hasMask) {
            fieldAndMask |= 1;
            length *= 2;
        }

        out.writeByte(fieldAndMask);
        out.writeByte(length);

    }

    private static void writeMetadataRelatedEntry(MatchEntries entry, ByteBuf out, int value) {
        int fieldValue = value;
        writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                EncodeConstants.SIZE_OF_LONG_IN_BYTES);
        out.writeBytes(entry.getAugmentation(MetadataMatchEntry.class).getMetadata());
        writeMask(entry, out, EncodeConstants.SIZE_OF_LONG_IN_BYTES);
    }

    private static void writeMacAddressRelatedEntry(MatchEntries entry, ByteBuf out, int value) {
        int fieldValue = value;

        writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                EncodeConstants.MAC_ADDRESS_LENGTH);
        String macAddress = entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue();
        out.writeBytes(ByteBufUtils.macAddressToBytes(macAddress)); // 48 b + mask [OF 1.3.2 spec]
        writeMask(entry, out, EncodeConstants.MAC_ADDRESS_LENGTH);
    }

    private static void writeIpv4AddressRelatedEntry(MatchEntries entry, ByteBuf out, int value) {
        int fieldValue = value;

        writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                EncodeConstants.SIZE_OF_INT_IN_BYTES);
        writeIpv4Address(entry, out);
        writeMask(entry, out, EncodeConstants.SIZE_OF_INT_IN_BYTES);
    }

    private static void writeIpv4Address(MatchEntries entry, ByteBuf out) {
        String[] addressGroups = entry.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address().getValue().split("\\.");
        for (int i = 0; i < addressGroups.length; i++) {
            out.writeByte(Integer.parseInt(addressGroups[i]));
        }
    }

    private static void writeIpv6AddressRelatedEntry(MatchEntries entry, ByteBuf out, int value) {
        int fieldValue = value;
        String textAddress = entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address().getValue();
        String[] address;
        if (textAddress.equals("::")) {
            address = new String[EncodeConstants.GROUPS_IN_IPV6_ADDRESS];
            Arrays.fill(address, "0");
        } else {
            address = parseIpv6Address(textAddress.split(":"));
        }

        writeOxmFieldAndLength(out, fieldValue, entry.isHasMask(),
                EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
        for (int i = 0; i < address.length; i++) {
            out.writeShort(Integer.parseInt(address[i], 16));
        }
        writeMask(entry, out, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
    }

    private static String[] parseIpv6Address(String[] addressGroups) {
        int countEmpty = 0;
        for (int i = 0; i < addressGroups.length; i++) {
            if (addressGroups[i].equals("")){
                countEmpty++;
            }
        }
        String[] ready = new String[EncodeConstants.GROUPS_IN_IPV6_ADDRESS];
        switch (countEmpty) {
        case 0:
            ready = addressGroups;
            break;
        case 1:
            int zerosToBePushed = EncodeConstants.GROUPS_IN_IPV6_ADDRESS - addressGroups.length + 1;
            int index = 0;
            for (int i = 0; i < addressGroups.length; i++) {
                if (addressGroups[i].equals("")) {
                    for (int j = 0; j < zerosToBePushed; j++) {
                        ready[index] = "0";
                        index++;
                    }
                } else {
                    ready[index] = addressGroups[i];
                    index++;
                }
            }
            break;
        case 2:
            Arrays.fill(ready, "0");
            ready[ready.length - 1] = addressGroups[addressGroups.length - 1];
            break;
        default:
            throw new IllegalStateException("Incorrect ipv6 address");
        }
        return ready;
    }

    /**
     * Computes length of match (in bytes)
     * @param match
     * @return length of ofp_match (excluding padding)
     */
    public static int computeMatchLengthInternal(Match match) {
        int length = 0;
        if (match != null) {
            length += MATCH_TYPE_AND_LENGTH_SIZE + computeMatchEntriesLength(match.getMatchEntries());
        }
        return length;
    }

    /**
     * Computes length of match (in bytes)
     * @param match
     * @return length of ofp_match (excluding padding)
     */
    public static int computeMatchLength(Match match) {
        int length = computeMatchLengthInternal(match);
        int paddingRemainder = length % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            length += EncodeConstants.PADDING - paddingRemainder;
        }
        return length;
    }

    /**
     * Computes length of MatchEntries (in bytes)
     * @param matchEntries list of match entries (oxm_fields)
     * @return length of MatchEntries
     */
    public static int computeMatchEntriesLength(List<MatchEntries> matchEntries) {
        int length = 0;
        if (matchEntries != null) {
            for (MatchEntries entry : matchEntries) {
                length += MATCH_ENTRY_HEADER_LENGTH;
                Class<? extends MatchField> field = entry.getOxmMatchField();
                if (field.isAssignableFrom(InPort.class)) {
                    length += EncodeConstants.SIZE_OF_INT_IN_BYTES;
                } else if (field.isAssignableFrom(InPhyPort.class)) {
                    length += EncodeConstants.SIZE_OF_INT_IN_BYTES;
                } else if (field.isAssignableFrom(Metadata.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_LONG_IN_BYTES);
                } else if (field.isAssignableFrom(EthDst.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(EthSrc.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(EthType.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(VlanVid.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                } else if (field.isAssignableFrom(VlanPcp.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(IpDscp.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(IpEcn.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(IpProto.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(Ipv4Src.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                } else if (field.isAssignableFrom(Ipv4Dst.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                } else if (field.isAssignableFrom(TcpSrc.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(TcpDst.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(UdpSrc.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(UdpDst.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(SctpSrc.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(SctpDst.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(Icmpv4Type.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(Icmpv4Code.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(ArpOp.class)) {
                    length += EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
                } else if (field.isAssignableFrom(ArpSpa.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                } else if (field.isAssignableFrom(ArpTpa.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                } else if (field.isAssignableFrom(ArpSha.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(ArpTha.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(Ipv6Src.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
                } else if (field.isAssignableFrom(Ipv6Dst.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
                } else if (field.isAssignableFrom(Ipv6Flabel.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_INT_IN_BYTES);
                } else if (field.isAssignableFrom(Icmpv6Type.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(Icmpv6Code.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(Ipv6NdTarget.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
                } else if (field.isAssignableFrom(Ipv6NdSll.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(Ipv6NdTll.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.MAC_ADDRESS_LENGTH);
                } else if (field.isAssignableFrom(MplsLabel.class)) {
                    length += EncodeConstants.SIZE_OF_INT_IN_BYTES;
                } else if (field.isAssignableFrom(MplsTc.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(MplsBos.class)) {
                    length += EncodeConstants.SIZE_OF_BYTE_IN_BYTES;
                } else if (field.isAssignableFrom(PbbIsid.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_3_BYTES);
                } else if (field.isAssignableFrom(TunnelId.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_LONG_IN_BYTES);
                } else if (field.isAssignableFrom(Ipv6Exthdr.class)) {
                    length += computePossibleMaskEntryLength(entry, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
                }
            }
        }
        return length;
    }

    private static int computePossibleMaskEntryLength(MatchEntries entry, int length) {
        int entryLength = length;
        if (entry.isHasMask()) {
            entryLength *= 2;
        }
        return entryLength;
    }

}
