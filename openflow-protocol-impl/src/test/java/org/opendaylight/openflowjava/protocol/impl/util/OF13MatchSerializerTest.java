/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6FlowLabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6FlabelMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6FlabelMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.StandardMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv4Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Flabel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6NdTarget;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmMatchType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class OF13MatchSerializerTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(OF13MatchSerializerTest.class);
    private SerializerRegistry registry;
    private OFSerializer<Match> matchSerializer;

    /**
     * Initializes serializer table and stores correct factory in field
     */
    @Before
    public void startUp() {
        registry = new SerializerRegistryImpl();
        registry.init();
        matchSerializer = registry.getSerializer(
                new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, Match.class));
    }

    /**
     * Test for correct serialization of Ipv4Address match entry
     */
    @Test
    public void testIpv4Src() {
        MatchBuilder builder = new MatchBuilder();
        builder.setType(OxmMatchType.class);
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv4Src.class);
        entriesBuilder.setHasMask(false);
        Ipv4AddressMatchEntryBuilder addressBuilder = new Ipv4AddressMatchEntryBuilder();
        addressBuilder.setIpv4Address(new Ipv4Address("1.2.3.4"));
        entriesBuilder.addAugmentation(Ipv4AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        builder.setMatchEntries(entries);
        Match match = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        matchSerializer.serialize(match, out);

        Assert.assertEquals("Wrong type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 22, out.readUnsignedByte());
        out.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        Assert.assertEquals("Wrong ip address (first number)", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip address (second number)", 2, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip address (third number)", 3, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip address (fourth number)", 4, out.readUnsignedByte());
    }

    /**
     * Test for correct serialization of Ipv6Address match entry
     */
    @Test
    public void testIpv6Various() {
        MatchBuilder builder = new MatchBuilder();
        builder.setType(OxmMatchType.class);
        List<MatchEntries> entries = new ArrayList<>();
        // ipv6 match entry with correct Ipv6 address
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Src.class);
        entriesBuilder.setHasMask(false);
        Ipv6AddressMatchEntryBuilder addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("1:2:3:4:5:6:7:8"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        // ipv6 match entry with abbreviated Ipv6 address
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6NdTarget.class);
        entriesBuilder.setHasMask(false);
        addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("1:2::6:7:8"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        // ipv6 match entry with abbreviated Ipv6 address
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Dst.class);
        entriesBuilder.setHasMask(false);
        addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("1::8"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        // ipv6 match entry with abbreviated Ipv6 address
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Dst.class);
        entriesBuilder.setHasMask(false);
        addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("::1"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        // ipv6 match entry with abbreviated Ipv6 address
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Dst.class);
        entriesBuilder.setHasMask(false);
        addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("::"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        builder.setMatchEntries(entries);
        Match match = builder.build();
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        matchSerializer.serialize(match, out);

        Assert.assertEquals("Wrong type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 52, out.readUnsignedByte());
        Assert.assertEquals("Wrong entry length", 16, out.readUnsignedByte());
        Assert.assertEquals("Wrong ipv6 address", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 7, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 62, out.readUnsignedByte());
        Assert.assertEquals("Wrong entry length", 16, out.readUnsignedByte());
        Assert.assertEquals("Wrong ipv6 address", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 7, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 54, out.readUnsignedByte());
        Assert.assertEquals("Wrong entry length", 16, out.readUnsignedByte());
        Assert.assertEquals("Wrong ipv6 address", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 54, out.readUnsignedByte());
        Assert.assertEquals("Wrong entry length", 16, out.readUnsignedByte());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 54, out.readUnsignedByte());
        Assert.assertEquals("Wrong entry length", 16, out.readUnsignedByte());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, out.readUnsignedShort());
    }

    /**
     * Test for correct serialization of incorrect Ipv6Address match entry
     */
    @Test(expected=IllegalArgumentException.class)
    public void testIpv6Incorrect() {
        MatchBuilder builder = new MatchBuilder();
        builder.setType(OxmMatchType.class);
        List<MatchEntries> entries = new ArrayList<>();
        // ipv6 match entry with incorrect Ipv6 address
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Src.class);
        entriesBuilder.setHasMask(false);
        Ipv6AddressMatchEntryBuilder addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address("1:2::::8"));
        entriesBuilder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        entries.add(entriesBuilder.build());
        builder.setMatchEntries(entries);
        Match match = builder.build();
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        matchSerializer.serialize(match, out);
    }

    /**
     * Test for correct serialization of Ipv4Address match entry
     */
    @Test
    public void testIpv6Flabel() {
        Match match = buildIpv6FLabelMatch(0x0f9e8dL, false, null);

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        matchSerializer.serialize(match, out);

        Assert.assertEquals("Wrong type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 28<<1, out.readUnsignedByte());
        out.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        byte[] label = new byte[4];
        out.readBytes(label);

        LOG.debug("label: "+ ByteBufUtils.bytesToHexString(label));
        Assert.assertArrayEquals("Wrong ipv6FLabel", new byte[]{0, 0x0f, (byte) 0x9e, (byte) 0x8d}, label);
    }

    /**
     * Test for correct serialization of Ipv4Address match entry with mask
     */
    @Test
    public void testIpv6FlabelWithMask() {
        Match match = buildIpv6FLabelMatch(0x0f9e8dL, true, new byte[]{0, 0x0c, 0x7b, 0x6a});

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        matchSerializer.serialize(match, out);

        Assert.assertEquals("Wrong type", 1, out.readUnsignedShort());
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        Assert.assertEquals("Wrong class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong field and mask", 28<<1 | 1, out.readUnsignedByte());
        out.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        byte[] labelAndMask = new byte[8];
        out.readBytes(labelAndMask);

        LOG.debug("label: "+ByteBufUtils.bytesToHexString(labelAndMask));
        Assert.assertArrayEquals("Wrong ipv6FLabel", new byte[]{0, 0x0f, (byte) 0x9e, (byte) 0x8d, 0, 0x0c, 0x7b, 0x6a}, labelAndMask);
    }

    /**
     * Test for correct serialization of Ipv4Address match entry with wrong mask
     */
    @Test
    public void testIpv6FlabelWithMaskBad() {
        Match match = buildIpv6FLabelMatch(0x0f9e8dL, true, new byte[]{0x0c, 0x7b, 0x6a});

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();

        try {
            matchSerializer.serialize(match, out);
            Assert.fail("incorrect length of mask ignored");
        } catch (IllegalArgumentException e) {
            //expected
        }
    }

    /**
     * @param labelValue ipv6 flow label
     * @param hasMask
     * @param mask ipv6 flow label mask
     * @return
     */
    private static Match buildIpv6FLabelMatch(long labelValue, boolean hasMask, byte[] mask) {
        MatchBuilder builder = new MatchBuilder();
        builder.setType(OxmMatchType.class);
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(Ipv6Flabel.class);
        entriesBuilder.setHasMask(hasMask);
        Ipv6FlabelMatchEntryBuilder ip6FLabelBuilder = new Ipv6FlabelMatchEntryBuilder();
        ip6FLabelBuilder.setIpv6Flabel(new Ipv6FlowLabel(labelValue));
        entriesBuilder.addAugmentation(Ipv6FlabelMatchEntry.class, ip6FLabelBuilder.build());
        MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
        maskBuilder.setMask(mask);
        entriesBuilder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        entries.add(entriesBuilder.build());
        builder.setMatchEntries(entries);
        Match match = builder.build();
        return match;
    }

    /**
     * Test Standard match type
     */
    @Test
    public void testStandardMatchType() {
        MatchBuilder builder = new MatchBuilder();
        builder.setType(StandardMatchType.class);
        Match match = builder.build();
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();

        matchSerializer.serialize(match, out);

        Assert.assertEquals("Wrong match type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong match length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong padding", 0, out.readUnsignedInt());
        Assert.assertEquals("Unexpected data", 0, out.readableBytes());
    }

    /**
     * Test serialize experimenter match entry - with no experimenter
     * match entry serializer registered
     */
    @Test(expected=IllegalStateException.class)
    public void testSerializeExperimenterMatchEntry() {
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(ExperimenterClass.class);
        builder.setOxmMatchField(OxmMatchFieldClass.class);
        builder.setHasMask(true);
        ExperimenterIdMatchEntryBuilder expIdBuilder = new ExperimenterIdMatchEntryBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(42L));
        builder.addAugmentation(ExperimenterIdMatchEntry.class, expIdBuilder.build());
        entries.add(builder.build());
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();

        ((OF13MatchSerializer) matchSerializer).serializeMatchEntries(entries, out);
    }

    private class OxmMatchFieldClass extends MatchField {
        // only for testing purposes
    }
}