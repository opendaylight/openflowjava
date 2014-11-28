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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IsidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IsidMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.PbbIsid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmPbbIsidSerializerTest {

    OxmPbbIsidSerializer serializer = new OxmPbbIsidSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithMask() {
        MatchEntriesBuilder builder = preparePbbIsidMatchEntry(false, 12345);

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        assertEquals("Wrong value", 12345, buffer.readUnsignedMedium());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithoutMask() {
        MatchEntriesBuilder builder = preparePbbIsidMatchEntry(true, 6789);

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, true);
        assertEquals("Wrong value", 6789, buffer.readUnsignedMedium());
        byte[] tmp = new byte[3];
        buffer.readBytes(tmp);
        Assert.assertArrayEquals("Wrong mask", new byte[]{0, 15, 10}, tmp);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeaderWithoutMask() {
        MatchEntriesBuilder builder = preparePbbIsidHeader(false);

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
        MatchEntriesBuilder builder = preparePbbIsidHeader(true);

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
        assertEquals("Wrong oxm-class", OxmMatchConstants.PBB_ISID, serializer.getOxmFieldCode());
    }

    /**
     * Test correct value length return value
     */
    @Test
    public void testGetValueLength() {
        assertEquals("Wrong value length", EncodeConstants.SIZE_OF_3_BYTES, serializer.getValueLength());
    }


    private static MatchEntriesBuilder preparePbbIsidMatchEntry(boolean hasMask, long value) {
        MatchEntriesBuilder builder = preparePbbIsidHeader(hasMask);
        if (hasMask) {
            MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
            maskBuilder.setMask(new byte[]{0, 15, 10});
            builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        }
        IsidMatchEntryBuilder isidBuilder = new IsidMatchEntryBuilder();
        isidBuilder.setIsid(value);
        builder.addAugmentation(IsidMatchEntry.class, isidBuilder.build());
        return builder;
    }

    private static MatchEntriesBuilder preparePbbIsidHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(PbbIsid.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.PBB_ISID, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        if (hasMask) {
            assertEquals("Wrong length", 2 * EncodeConstants.SIZE_OF_3_BYTES, buffer.readUnsignedByte());
        } else {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_3_BYTES, buffer.readUnsignedByte());
        }
    }
}