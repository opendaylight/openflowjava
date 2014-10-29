/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Metadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsBos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsLabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MplsTc;
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

/**
 * @author michal.polkorab
 *
 */
public class MatchDeserializerTest {

    private OFDeserializer<Match> matchDeserializer;
    private DeserializerRegistry registry;

    /**
     * Initializes deserializer registry and lookups correct deserializer
     */
    @Before
    public void startUp() {
        registry = new DeserializerRegistryImpl();
        registry.init();
        matchDeserializer = registry.getDeserializer(
                new MessageCodeKey(EncodeConstants.OF13_VERSION_ID,
                        EncodeConstants.EMPTY_VALUE, Match.class));
    }

    /**
     * Testing Ipv4 address deserialization
     */
    @Test
    public void testIpv4Address() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("80 00 18 04 00 01 02 03");

        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                0x8000, 12);
        key.setExperimenterId(null);
        OFDeserializer<MatchEntries> entryDeserializer = registry.getDeserializer(key);
        MatchEntries entry = entryDeserializer.deserialize(buffer);
        Assert.assertEquals("Wrong Ipv4 address format", new Ipv4Address("0.1.2.3"),
                entry.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
    }
    
    /**
     * Testing Ipv6 address deserialization
     */
    @Test
    public void testIpv6Address() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("80 00 34 10 00 00 00 01 00 02 00 03 00 04 00 05 00 06 0F 07");
        
        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                0x8000, 26);
        key.setExperimenterId(null);
        OFDeserializer<MatchEntries> entryDeserializer = registry.getDeserializer(key);
        MatchEntries entry = entryDeserializer.deserialize(buffer);
        Assert.assertEquals("Wrong Ipv6 address format", new Ipv6Address("0000:0001:0002:0003:0004:0005:0006:0F07"),
                entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address());
    }
    
    /**
     * Testing match deserialization
     */
    @Test
    public void testMatch() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("00 01 01 AC "
                + "80 00 00 04 00 00 00 01 "
                + "80 00 02 04 00 00 00 02 "
                + "80 00 05 10 00 00 00 00 00 00 00 03 00 00 00 00 00 00 00 04 "
                + "80 00 07 0C 00 00 00 00 00 05 00 00 00 00 00 06 "
                + "80 00 09 0C 00 00 00 00 00 07 00 00 00 00 00 08 "
                + "80 00 0A 02 00 09 "
                + "80 00 0D 04 00 0A 00 0B "
                + "80 00 0E 01 0C "
                + "80 00 10 01 0D "
                + "80 00 12 01 0E "
                + "80 00 14 01 0F "
                + "80 00 17 08 0A 00 00 01 00 00 FF 00 "
                + "80 00 19 08 0A 00 00 02 00 00 00 FF "
                + "80 00 1A 02 00 03 "
                + "80 00 1C 02 00 04 "
                + "80 00 1E 02 00 05 "
                + "80 00 20 02 00 06 "
                + "80 00 22 02 00 07 "
                + "80 00 24 02 00 08 "
                + "80 00 26 01 05 "
                + "80 00 28 01 07 "
                + "80 00 2A 02 00 10 "
                + "80 00 2D 08 0A 00 00 09 00 00 FF 00 "
                + "80 00 2F 08 0A 00 00 0A 00 00 00 FF "
                + "80 00 31 0C 00 00 00 00 00 01 00 00 00 00 00 03 "
                + "80 00 33 0C 00 00 00 00 00 02 00 00 00 00 00 04 "
                + "80 00 35 20 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 15 "
                +             "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 16 "
                + "80 00 37 20 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 17 "
                +             "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18 "
                + "80 00 39 08 00 00 00 02 00 00 00 03 "
                + "80 00 3A 01 15 "
                + "80 00 3C 01 17 "
                + "80 00 3E 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 20 " //ipv6ndtarget
                + "80 00 40 06 00 05 00 00 00 01 "
                + "80 00 42 06 00 05 00 00 00 02 "
                + "80 00 44 04 00 00 02 03 "
                + "80 00 46 01 03 "
                + "80 00 48 01 01 "
                + "80 00 4B 06 00 00 02 00 00 01 "
                + "80 00 4D 10 00 00 00 00 00 00 00 07 00 00 00 00 00 00 00 FF "
                + "80 00 4F 04 00 00 03 04 "
                + "00 00 00 00");

        Match match = matchDeserializer.deserialize(buffer);
        Assert.assertEquals("Wrong match type", OxmMatchType.class, match.getType());
        Assert.assertEquals("Wrong match entries size", 40, match.getMatchEntries().size());
        List<MatchEntries> entries = match.getMatchEntries();
        MatchEntries entry0 = entries.get(0);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry0.getOxmClass());
        Assert.assertEquals("Wrong entry field", InPort.class, entry0.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry0.isHasMask());
        Assert.assertEquals("Wrong entry value", 1,
                entry0.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        MatchEntries entry1 = entries.get(1);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry1.getOxmClass());
        Assert.assertEquals("Wrong entry field", InPhyPort.class, entry1.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry1.isHasMask());
        Assert.assertEquals("Wrong entry value", 2,
                entry1.getAugmentation(PortNumberMatchEntry.class).getPortNumber().getValue().intValue());
        MatchEntries entry2 = entries.get(2);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry2.getOxmClass());
        Assert.assertEquals("Wrong entry field", Metadata.class, entry2.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry2.isHasMask());
        Assert.assertArrayEquals("Wrong entry value", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 03"), 
                entry2.getAugmentation(MetadataMatchEntry.class).getMetadata());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 04"), 
                entry2.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry3 = entries.get(3);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry3.getOxmClass());
        Assert.assertEquals("Wrong entry field", EthDst.class, entry3.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry3.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:00:00:00:00:05"), 
                entry3.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 06"), 
                entry3.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry4 = entries.get(4);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry4.getOxmClass());
        Assert.assertEquals("Wrong entry field", EthSrc.class, entry4.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry4.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:00:00:00:00:07"), 
                entry4.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 08"), 
                entry4.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry5 = entries.get(5);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry5.getOxmClass());
        Assert.assertEquals("Wrong entry field", EthType.class, entry5.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry5.isHasMask());
        Assert.assertEquals("Wrong entry value", 9,
                entry5.getAugmentation(EthTypeMatchEntry.class).getEthType().getValue().intValue());
        MatchEntries entry6 = entries.get(6);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry6.getOxmClass());
        Assert.assertEquals("Wrong entry field", VlanVid.class, entry6.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry6.isHasMask());
        Assert.assertEquals("Wrong entry value", 10,
                entry6.getAugmentation(VlanVidMatchEntry.class).getVlanVid().intValue());
        Assert.assertEquals("Wrong entry value", false, 
                entry6.getAugmentation(VlanVidMatchEntry.class).isCfiBit());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 0B"), 
                entry6.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry7 = entries.get(7);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry7.getOxmClass());
        Assert.assertEquals("Wrong entry field", VlanPcp.class, entry7.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry7.isHasMask());
        Assert.assertEquals("Wrong entry value", 12,
                entry7.getAugmentation(VlanPcpMatchEntry.class).getVlanPcp().intValue());
        MatchEntries entry8 = entries.get(8);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry8.getOxmClass());
        Assert.assertEquals("Wrong entry field", IpDscp.class, entry8.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry8.isHasMask());
        Assert.assertEquals("Wrong entry value", 13,
                entry8.getAugmentation(DscpMatchEntry.class).getDscp().getValue().intValue());
        MatchEntries entry9 = entries.get(9);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry9.getOxmClass());
        Assert.assertEquals("Wrong entry field", IpEcn.class, entry9.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry9.isHasMask());
        Assert.assertEquals("Wrong entry value", 14,
                entry9.getAugmentation(EcnMatchEntry.class).getEcn().intValue());
        MatchEntries entry10 = entries.get(10);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry10.getOxmClass());
        Assert.assertEquals("Wrong entry field", IpProto.class, entry10.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry10.isHasMask());
        Assert.assertEquals("Wrong entry value", 15,
                entry10.getAugmentation(ProtocolNumberMatchEntry.class).getProtocolNumber().intValue());
        MatchEntries entry11 = entries.get(11);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry11.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv4Src.class, entry11.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry11.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv4Address("10.0.0.1"),
                entry11.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 FF 00"), 
                entry11.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry12 = entries.get(12);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry12.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv4Dst.class, entry12.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry12.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv4Address("10.0.0.2"),
                entry12.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 FF"), 
                entry12.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry13 = entries.get(13);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry13.getOxmClass());
        Assert.assertEquals("Wrong entry field", TcpSrc.class, entry13.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry13.isHasMask());
        Assert.assertEquals("Wrong entry value", 3,
                entry13.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry14 = entries.get(14);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry14.getOxmClass());
        Assert.assertEquals("Wrong entry field", TcpDst.class, entry14.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry14.isHasMask());
        Assert.assertEquals("Wrong entry value", 4,
                entry14.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry15 = entries.get(15);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry15.getOxmClass());
        Assert.assertEquals("Wrong entry field", UdpSrc.class, entry15.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry15.isHasMask());
        Assert.assertEquals("Wrong entry value", 5,
                entry15.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry16 = entries.get(16);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry16.getOxmClass());
        Assert.assertEquals("Wrong entry field", UdpDst.class, entry16.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry16.isHasMask());
        Assert.assertEquals("Wrong entry value", 6,
                entry16.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry17 = entries.get(17);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry17.getOxmClass());
        Assert.assertEquals("Wrong entry field", SctpSrc.class, entry17.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry17.isHasMask());
        Assert.assertEquals("Wrong entry value", 7,
                entry17.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry18 = entries.get(18);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry18.getOxmClass());
        Assert.assertEquals("Wrong entry field", SctpDst.class, entry18.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry18.isHasMask());
        Assert.assertEquals("Wrong entry value", 8,
                entry18.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
        MatchEntries entry19 = entries.get(19);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry19.getOxmClass());
        Assert.assertEquals("Wrong entry field", Icmpv4Type.class, entry19.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry19.isHasMask());
        Assert.assertEquals("Wrong entry value", 5,
                entry19.getAugmentation(Icmpv4TypeMatchEntry.class).getIcmpv4Type().intValue());
        MatchEntries entry20 = entries.get(20);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry20.getOxmClass());
        Assert.assertEquals("Wrong entry field", Icmpv4Code.class, entry20.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry20.isHasMask());
        Assert.assertEquals("Wrong entry value", 7,
                entry20.getAugmentation(Icmpv4CodeMatchEntry.class).getIcmpv4Code().intValue());
        MatchEntries entry21 = entries.get(21);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry21.getOxmClass());
        Assert.assertEquals("Wrong entry field", ArpOp.class, entry21.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry21.isHasMask());
        Assert.assertEquals("Wrong entry value", 16,
                entry21.getAugmentation(OpCodeMatchEntry.class).getOpCode().intValue());
        MatchEntries entry22 = entries.get(22);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry22.getOxmClass());
        Assert.assertEquals("Wrong entry field", ArpSpa.class, entry22.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry22.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv4Address("10.0.0.9"),
                entry22.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 FF 00"), 
                entry22.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry23 = entries.get(23);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry23.getOxmClass());
        Assert.assertEquals("Wrong entry field", ArpTpa.class, entry23.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry23.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv4Address("10.0.0.10"),
                entry23.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 FF"), 
                entry23.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry24 = entries.get(24);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry24.getOxmClass());
        Assert.assertEquals("Wrong entry field", ArpSha.class, entry24.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry24.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:00:00:00:00:01"), 
                entry24.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 03"), 
                entry24.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry25 = entries.get(25);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry25.getOxmClass());
        Assert.assertEquals("Wrong entry field", ArpTha.class, entry25.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry25.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:00:00:00:00:02"), 
                entry25.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 04"), 
                entry25.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry26 = entries.get(26);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry26.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6Src.class, entry26.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry26.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv6Address("0000:0000:0000:0000:0000:0000:0000:0015"), 
                entry26.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address());
        Assert.assertArrayEquals("Wrong entry mask",
                ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 16"), 
                entry26.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry27 = entries.get(27);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry27.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6Dst.class, entry27.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry27.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv6Address("0000:0000:0000:0000:0000:0000:0000:0017"), 
                entry27.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address());
        Assert.assertArrayEquals("Wrong entry mask",
                ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18"), 
                entry27.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry28 = entries.get(28);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry28.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6Flabel.class, entry28.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry28.isHasMask());
        Assert.assertEquals("Wrong entry value", 2, 
                entry28.getAugmentation(Ipv6FlabelMatchEntry.class).getIpv6Flabel().getValue().intValue());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 03"),
                entry28.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry29 = entries.get(29);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry29.getOxmClass());
        Assert.assertEquals("Wrong entry field", Icmpv6Type.class, entry29.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry29.isHasMask());
        Assert.assertEquals("Wrong entry value", 21, 
                entry29.getAugmentation(Icmpv6TypeMatchEntry.class).getIcmpv6Type().intValue());
        MatchEntries entry30 = entries.get(30);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry30.getOxmClass());
        Assert.assertEquals("Wrong entry field", Icmpv6Code.class, entry30.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry30.isHasMask());
        Assert.assertEquals("Wrong entry value", 23, 
                entry30.getAugmentation(Icmpv6CodeMatchEntry.class).getIcmpv6Code().intValue());
        MatchEntries entry31 = entries.get(31);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry31.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6NdTarget.class, entry31.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry31.isHasMask());
        Assert.assertEquals("Wrong entry value", new Ipv6Address("0000:0000:0000:0000:0000:0000:0000:0020"), 
                entry31.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address());
        MatchEntries entry32 = entries.get(32);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry32.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6NdSll.class, entry32.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry32.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:05:00:00:00:01"), 
                entry32.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        MatchEntries entry33 = entries.get(33);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry33.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6NdTll.class, entry33.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry33.isHasMask());
        Assert.assertEquals("Wrong entry value", new MacAddress("00:05:00:00:00:02"),
                entry33.getAugmentation(MacAddressMatchEntry.class).getMacAddress());
        MatchEntries entry34 = entries.get(34);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry34.getOxmClass());
        Assert.assertEquals("Wrong entry field", MplsLabel.class, entry34.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry34.isHasMask());
        Assert.assertEquals("Wrong entry value", 515,
                entry34.getAugmentation(MplsLabelMatchEntry.class).getMplsLabel().intValue());
        MatchEntries entry35 = entries.get(35);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry35.getOxmClass());
        Assert.assertEquals("Wrong entry field", MplsTc.class, entry35.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry35.isHasMask());
        Assert.assertEquals("Wrong entry value", 3,
                entry35.getAugmentation(TcMatchEntry.class).getTc().intValue());
        MatchEntries entry36 = entries.get(36);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry36.getOxmClass());
        Assert.assertEquals("Wrong entry field", MplsBos.class, entry36.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry36.isHasMask());
        Assert.assertEquals("Wrong entry value", true,
                entry36.getAugmentation(BosMatchEntry.class).isBos());
        MatchEntries entry37 = entries.get(37);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry37.getOxmClass());
        Assert.assertEquals("Wrong entry field", PbbIsid.class, entry37.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry37.isHasMask());
        Assert.assertEquals("Wrong entry value", 2,
                entry37.getAugmentation(IsidMatchEntry.class).getIsid().intValue());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 01"),
                entry37.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry38 = entries.get(38);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry38.getOxmClass());
        Assert.assertEquals("Wrong entry field", TunnelId.class, entry38.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry38.isHasMask());
        Assert.assertArrayEquals("Wrong entry value", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 07"),
                entry38.getAugmentation(MetadataMatchEntry.class).getMetadata());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("00 00 00 00 00 00 00 FF"),
                entry38.getAugmentation(MaskMatchEntry.class).getMask());
        MatchEntries entry39 = entries.get(39);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry39.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv6Exthdr.class, entry39.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", true, entry39.isHasMask());
        Assert.assertEquals("Wrong entry value",
                new Ipv6ExthdrFlags(false, false, false, false, false, false, false, false, false),
                entry39.getAugmentation(PseudoFieldMatchEntry.class).getPseudoField());
        Assert.assertArrayEquals("Wrong entry mask", ByteBufUtils.hexStringToBytes("03 04"),
                entry39.getAugmentation(MaskMatchEntry.class).getMask());
        Assert.assertTrue("Unread data", buffer.readableBytes() == 0);
    }

    /**
     * Testing header deserialization
     */
    @Test
    public void testHeaders() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("80 00 18 04 00 01 02 03");

        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                0x8000, 12);
        key.setExperimenterId(null);
        HeaderDeserializer<MatchEntries> entryDeserializer = registry.getDeserializer(key);
        MatchEntries entry = entryDeserializer.deserializeHeader(buffer);
        Assert.assertEquals("Wrong entry class", OpenflowBasicClass.class, entry.getOxmClass());
        Assert.assertEquals("Wrong entry field", Ipv4Dst.class, entry.getOxmMatchField());
        Assert.assertEquals("Wrong entry hasMask", false, entry.isHasMask());
        Assert.assertEquals("Wrong Ipv4 address", null, entry.getAugmentation(Ipv4AddressMatchEntry.class));
    }

    /**
     * Testing standard match type
     */
    @Test
    public void testStandardMatch() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("00 00 00 10 80 00 04 08 00 00 00 00 00 00 00 01");

        Match match = matchDeserializer.deserialize(buffer);

        Assert.assertEquals("Wrong match type", StandardMatchType.class, match.getType());
        Assert.assertEquals("Wrong match entries size", 1, match.getMatchEntries().size());
    }
}