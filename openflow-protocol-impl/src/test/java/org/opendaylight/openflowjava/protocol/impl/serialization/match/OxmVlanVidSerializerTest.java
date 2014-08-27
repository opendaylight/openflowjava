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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.VlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmVlanVidSerializerTest {

    OxmVlanVidSerializer serializer = new OxmVlanVidSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithCfiBitSet() {
        MatchEntriesBuilder builder = prepareVlanVidMatchEntry(false, true);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        assertEquals("Wrong value", 4596, buffer.readUnsignedShort());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithoutCfiBitSet() {
        MatchEntriesBuilder builder = prepareVlanVidMatchEntry(true, false);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, true);
        assertEquals("Wrong value", 500, buffer.readUnsignedShort());
        byte[] tmp = new byte[2];
        buffer.readBytes(tmp);
        Assert.assertArrayEquals("Wrong mask", new byte[]{15, 15}, tmp);
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeaderWithoutMask() {
        MatchEntriesBuilder builder = prepareVlanVidHeader(false);
        
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
        MatchEntriesBuilder builder = prepareVlanVidHeader(true);
        
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
        assertEquals("Wrong oxm-class", OxmMatchConstants.VLAN_VID, serializer.getOxmFieldCode());
    }

    /**
     * Test correct value length return value
     */
    @Test
    public void testGetValueLength() {
        assertEquals("Wrong value length", EncodeConstants.SIZE_OF_SHORT_IN_BYTES, serializer.getValueLength());
    }

    private static MatchEntriesBuilder prepareVlanVidMatchEntry(boolean hasMask, boolean cfiBit) {
        MatchEntriesBuilder builder = prepareVlanVidHeader(hasMask);
        if (hasMask) {
            MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
            maskBuilder.setMask(new byte[]{15, 15});
            builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        }
        VlanVidMatchEntryBuilder vlanBuilder = new VlanVidMatchEntryBuilder();
        vlanBuilder.setVlanVid(500);
        vlanBuilder.setCfiBit(cfiBit);
        builder.addAugmentation(VlanVidMatchEntry.class, vlanBuilder.build());
        return builder;
    }

    private static MatchEntriesBuilder prepareVlanVidHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(VlanVid.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.VLAN_VID, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        if (hasMask) {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_INT_IN_BYTES, buffer.readUnsignedByte());
        } else {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_SHORT_IN_BYTES, buffer.readUnsignedByte());
        }
    }
}