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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Exthdr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmIpv6ExtHdrSerializerTest {

    OxmIpv6ExtHdrSerializer serializer = new OxmIpv6ExtHdrSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithMask() {
        MatchEntriesBuilder builder = prepareIpv6ExtHdrMatchEntry(false,
                new Ipv6ExthdrFlags(true, false, true, false, true, false, true, false, true));
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        assertEquals("Wrong value", 358, buffer.readUnsignedShort());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithoutMask() {
        MatchEntriesBuilder builder = prepareIpv6ExtHdrMatchEntry(true,
                new Ipv6ExthdrFlags(false, true, false, true, false, true, false, true, false));
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, true);
        assertEquals("Wrong value", 153, buffer.readUnsignedShort());
        byte[] tmp = new byte[2];
        buffer.readBytes(tmp);
        Assert.assertArrayEquals("Wrong mask", new byte[]{0, 15}, tmp);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeaderWithoutMask() {
        MatchEntriesBuilder builder = prepareIpv6ExtHdrHeader(false);
        
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
        MatchEntriesBuilder builder = prepareIpv6ExtHdrHeader(true);
        
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
        assertEquals("Wrong oxm-class", OxmMatchConstants.IPV6_EXTHDR, serializer.getOxmFieldCode());
    }

    /**
     * Test correct value length return value
     */
    @Test
    public void testGetValueLength() {
        assertEquals("Wrong value length", EncodeConstants.SIZE_OF_SHORT_IN_BYTES, serializer.getValueLength());
    }

    
    private static MatchEntriesBuilder prepareIpv6ExtHdrMatchEntry(boolean hasMask, Ipv6ExthdrFlags flags) {
        MatchEntriesBuilder builder = prepareIpv6ExtHdrHeader(hasMask);
        if (hasMask) {
            MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
            maskBuilder.setMask(new byte[]{0, 15});
            builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        }
        PseudoFieldMatchEntryBuilder pseudoBuilder = new PseudoFieldMatchEntryBuilder();
        pseudoBuilder.setPseudoField(flags);
        builder.addAugmentation(PseudoFieldMatchEntry.class, pseudoBuilder.build());
        return builder;
    }

    private static MatchEntriesBuilder prepareIpv6ExtHdrHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(Ipv6Exthdr.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.IPV6_EXTHDR, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        if (hasMask) {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_INT_IN_BYTES, buffer.readUnsignedByte());
        } else {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_SHORT_IN_BYTES, buffer.readUnsignedByte());
        }
    }
}