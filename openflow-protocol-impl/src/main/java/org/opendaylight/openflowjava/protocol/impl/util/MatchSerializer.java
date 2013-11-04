/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.BosMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthTypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv6TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MacAddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MetadataMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsLabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OpCodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.TcMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntry;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.flow.mod.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class MatchSerializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchSerializer.class);

    /**
     * Encodes OF match
     * @param match ofp_match object
     * @param out output ByteBuf
     */
    public static void encodeMatch(Match match, ByteBuf out) {
        final byte PADDING_IN_OFP_MATCH = 4;
        encodeType(match, out);
        //TODO - compute length
        encodeMatchEntries(match.getMatchEntries(), out);
        ByteBufUtils.padBuffer(PADDING_IN_OFP_MATCH, out);
        
    }

    private static void encodeType(Match match, ByteBuf out) {
        final byte STANDARD_MATCH_TYPE_CODE = 0;
        final byte OXM_MATCH_TYPE_CODE = 1;
        if (match.getType().equals(StandardMatchType.class)) {
            out.writeShort(STANDARD_MATCH_TYPE_CODE);
        } else if (match.getType().equals(OxmMatchType.class)) {
            out.writeShort(OXM_MATCH_TYPE_CODE);
        }
    }

    private static void encodeMatchEntries(List<MatchEntries> matchEntries, ByteBuf out) {
        if (matchEntries == null) {
            LOGGER.warn("Match entry is null");
            return;
        }
        for (MatchEntries entry : matchEntries) {
            encodeClass(entry.getOxmClass(), out);
            encodeRest(entry, null);
        }
    }

    private static void encodeClass(Class<? extends Clazz> clazz, ByteBuf out) {
        final int NXM0_CLASS_CODE = 0x0000;
        final int NXM1_CLASS_CODE = 0x0001;
        final int OPENFLOW_BASIC_CLASS_CODE = 0x8000;
        final int EXPERIMENTER_CLASS_CODE = 0xFFFF;
        if (Nxm0Class.class.equals(clazz)) {
            out.writeShort(NXM0_CLASS_CODE);
        } else if (Nxm1Class.class.equals(clazz)) {
            out.writeShort(NXM1_CLASS_CODE);
        } else if (OpenflowBasicClass.class.equals(clazz)) {
            out.writeShort(OPENFLOW_BASIC_CLASS_CODE);
        } else if (ExperimenterClass.class.equals(clazz)) {
            out.writeShort(EXPERIMENTER_CLASS_CODE);
        }
    }
    
    private static void encodeRest(MatchEntries entry, ByteBuf out) {
        int fieldValue = 0;
        Class<? extends MatchField> field = entry.getOxmMatchField();
        if (field.equals(InPort.class)) {
            fieldValue = 0;
            writeOxmFieldAndLength(out, fieldValue, Integer.SIZE / Byte.SIZE);
            out.writeInt(entry.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        } else if (field.equals(InPhyPort.class)) {
            fieldValue = 1;
            writeOxmFieldAndLength(out, fieldValue, Integer.SIZE / Byte.SIZE);
            out.writeInt(entry.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        } else if (field.equals(Metadata.class)) {
            fieldValue = 2;
            fieldValue = fieldValue << 1;
            if (entry.isHasMask()) {
                fieldValue = fieldValue | 1;
                out.writeByte(fieldValue);
                byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
                out.writeByte(Long.SIZE / Byte.SIZE + mask.length);
                out.writeBytes(entry.getAugmentation(MetadataMatchEntry.class).getMetadata());
                out.writeBytes(mask);
            } else {
                out.writeByte(fieldValue);
                out.writeByte(Long.SIZE / Byte.SIZE);
                out.writeBytes(entry.getAugmentation(MetadataMatchEntry.class).getMetadata());
            }
        } else if (field.equals(EthDst.class)) {
            fieldValue = 3;
            fieldValue = fieldValue << 1;
            if (entry.isHasMask()) {
                fieldValue = fieldValue | 1;
                out.writeByte(fieldValue);
                byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
                out.writeByte((Integer.SIZE + Short.SIZE) / Byte.SIZE + mask.length);
                out.writeBytes(entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue().getBytes());
                out.writeBytes(entry.getAugmentation(MaskMatchEntry.class).getMask());
            } else {
                out.writeByte(fieldValue);
                out.writeByte(Long.SIZE / Byte.SIZE);
                out.writeBytes(entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue().getBytes());
            }
        } else if (field.equals(EthSrc.class)) {
            fieldValue = 4;
            fieldValue = fieldValue << 1;
            if (entry.isHasMask()) {
                fieldValue = fieldValue | 1;
                out.writeByte(fieldValue);
                byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
                out.writeByte(Long.SIZE / Byte.SIZE + mask.length);
                out.writeBytes(entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue().getBytes());
                out.writeBytes(mask);
            } else {
                out.writeByte(fieldValue);
                out.writeByte(Long.SIZE / Byte.SIZE);
                out.writeBytes(entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue().getBytes());
            }
        } else if (field.equals(EthType.class)) {
            fieldValue = 5;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(EthTypeMatchEntry.class).getEthType().getValue().shortValue());
        } else if (field.equals(VlanVid.class)) {
            fieldValue = 6;
            fieldValue = fieldValue << 1;
            if (entry.isHasMask()) {
                fieldValue = fieldValue | 1;
                out.writeByte(fieldValue);
                byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
                out.writeByte(Short.SIZE / Byte.SIZE + mask.length);
                out.writeShort(entry.getAugmentation(VlanVidMatchEntry.class).getVlanVid());
                out.writeBytes(mask);
            } else {
                writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
                out.writeShort(entry.getAugmentation(VlanVidMatchEntry.class).getVlanVid());
            }
        } else if (field.equals(VlanPcp.class)) {
            fieldValue = 7;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(VlanPcpMatchEntry.class).getVlanPcp().byteValue());
        } else if (field.equals(IpDscp.class)) {
            fieldValue = 8;
        } else if (field.equals(IpEcn.class)) {
            fieldValue = 9;
        } else if (field.equals(IpProto.class)) {
            fieldValue = 10;
        } else if (field.equals(Ipv4Src.class)) {
            fieldValue = 11;
        } else if (field.equals(Ipv4Dst.class)) {
            fieldValue = 12;
        } else if (field.equals(TcpSrc.class)) {
            fieldValue = 13;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(TcpDst.class)) {
            fieldValue = 14;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(UdpSrc.class)) {
            fieldValue = 15;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(UdpDst.class)) {
            fieldValue = 16;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(SctpSrc.class)) {
            fieldValue = 17;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(SctpDst.class)) {
            fieldValue = 18;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        } else if (field.equals(Icmpv4Type.class)) {
            fieldValue = 19;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(Icmpv4TypeMatchEntry.class).getIcmpv4Type());
        } else if (field.equals(Icmpv4Code.class)) {
            fieldValue = 20;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(Icmpv4CodeMatchEntry.class).getIcmpv4Code());
        } else if (field.equals(ArpOp.class)) {
            fieldValue = 21;
            writeOxmFieldAndLength(out, fieldValue, Short.SIZE / Byte.SIZE);
            out.writeShort(entry.getAugmentation(OpCodeMatchEntry.class).getOpCode());
        } else if (field.equals(ArpSpa.class)) {
            fieldValue = 22;
        } else if (field.equals(ArpTpa.class)) {
            fieldValue = 23;
        } else if (field.equals(ArpSha.class)) {
            fieldValue = 24;
        } else if (field.equals(ArpTha.class)) {
            fieldValue = 25;
        } else if (field.equals(Ipv6Src.class)) {
            fieldValue = 26;
        } else if (field.equals(Ipv6Dst.class)) {
            fieldValue = 27;
        } else if (field.equals(Ipv6Flabel.class)) {
            fieldValue = 28;
        } else if (field.equals(Icmpv6Type.class)) {
            fieldValue = 29;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(Icmpv6TypeMatchEntry.class).getIcmpv6Type());
        } else if (field.equals(Icmpv6Code.class)) {
            fieldValue = 30;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(Icmpv6CodeMatchEntry.class).getIcmpv6Code());
        } else if (field.equals(Ipv6NdTarget.class)) {
            fieldValue = 31;
        } else if (field.equals(Ipv6NdSll.class)) {
            fieldValue = 32;
        } else if (field.equals(Ipv6NdTll.class)) {
            fieldValue = 33;
        } else if (field.equals(MplsLabel.class)) {
            fieldValue = 34;
            writeOxmFieldAndLength(out, fieldValue, Integer.SIZE / Byte.SIZE);
            out.writeInt(entry.getAugmentation(MplsLabelMatchEntry.class).getMplsLabel().intValue());
        } else if (field.equals(MplsTc.class)) {
            fieldValue = 35;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeByte(entry.getAugmentation(TcMatchEntry.class).getTc());
        } else if (field.equals(MplsBos.class)) {
            fieldValue = 36;
            writeOxmFieldAndLength(out, fieldValue, Byte.SIZE / Byte.SIZE);
            out.writeBoolean(entry.getAugmentation(BosMatchEntry.class).isBos().booleanValue());
        } else if (field.equals(PbbIsid.class)) {
            fieldValue = 37;
        } else if (field.equals(TunnelId.class)) {
            fieldValue = 38;
        } else if (field.equals(Ipv6Exthdr.class)) {
            fieldValue = 39;
        }
    }

    private static void writeOxmFieldAndLength(ByteBuf out, int fieldValue, int length) {
        int fieldAndMask = fieldValue << 1;
        out.writeByte(fieldAndMask);
        out.writeByte(length);
    }
    
}
