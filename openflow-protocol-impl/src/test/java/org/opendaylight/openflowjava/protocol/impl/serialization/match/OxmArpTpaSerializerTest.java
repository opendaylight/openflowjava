/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ArpTpa;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmArpTpaSerializerTest {

    OxmArpTpaSerializer serializer = new OxmArpTpaSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithoutMask() {
        MatchEntriesBuilder builder = prepareMatchEntry(false, "10.0.0.1");
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        byte[] address = new byte[4];
        buffer.readBytes(address);
        Assert.assertArrayEquals("Wrong address", new byte[]{10, 0, 0, 1}, address);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithMask() {
        MatchEntriesBuilder builder = prepareMatchEntry(true, "120.121.122.0");
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, true);
        
        byte[] address = new byte[4];
        buffer.readBytes(address);
        Assert.assertArrayEquals("Wrong address", new byte[]{120, 121, 122, 0}, address);
        byte[] tmp = new byte[4];
        buffer.readBytes(tmp);
        Assert.assertArrayEquals("Wrong mask", new byte[]{15, 15, 0, 0}, tmp);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeaderWithoutMask() {
        MatchEntriesBuilder builder = prepareHeader(false);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serializeHeader(builder.build(), buffer);

        checkHeader(buffer, false);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeaderWithMask() {
        MatchEntriesBuilder builder = prepareHeader(true);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serializeHeader(builder.build(), buffer);

        checkHeader(buffer, true);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct oxm-class return value
     */
    @Test
    public void testGetOxmClassCode() {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, serializer.getOxmClassCode());
    }

    /**
     * Test correct oxm-field return value
     */
    @Test
    public void getOxmFieldCode() {
        assertEquals("Wrong oxm-class", OxmMatchConstants.ARP_TPA, serializer.getOxmFieldCode());
    }

    /**
     * Test correct value length return value
     */
    @Test
    public void testGetValueLength() {
        assertEquals("Wrong value length", EncodeConstants.SIZE_OF_INT_IN_BYTES, serializer.getValueLength());
    }

    private static MatchEntriesBuilder prepareMatchEntry(boolean hasMask, String value) {
        MatchEntriesBuilder builder = prepareHeader(hasMask);
        if (hasMask) {
            MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
            maskBuilder.setMask(new byte[]{15, 15, 0, 0});
            builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        }
        Ipv4AddressMatchEntryBuilder addressBuilder = new Ipv4AddressMatchEntryBuilder();
        addressBuilder.setIpv4Address(new Ipv4Address(value));
        builder.addAugmentation(Ipv4AddressMatchEntry.class, addressBuilder.build());
        return builder;
    }

    private static MatchEntriesBuilder prepareHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(ArpTpa.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.ARP_TPA, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        if (hasMask) {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_LONG_IN_BYTES, buffer.readUnsignedByte());
        } else {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_INT_IN_BYTES, buffer.readUnsignedByte());
        }
    }
}