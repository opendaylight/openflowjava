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

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4CodeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Code;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmIcmpv4CodeSerializerTest {

    OxmIcmpv4CodeSerializer serializer = new OxmIcmpv4CodeSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerialize() {
        MatchEntriesBuilder builder = prepareIcmpv4CodeMatchEntry((short) 200);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        assertEquals("Wrong value", 200, buffer.readUnsignedByte());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct header serialization
     */
    @Test
    public void testSerializeHeader() {
        MatchEntriesBuilder builder = prepareIcmpv4CodeHeader(false);
        
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serializeHeader(builder.build(), buffer);

        checkHeader(buffer, false);
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
        assertEquals("Wrong oxm-class", OxmMatchConstants.ICMPV4_CODE, serializer.getOxmFieldCode());
    }

    /**
     * Test correct value length return value
     */
    @Test
    public void testGetValueLength() {
        assertEquals("Wrong value length", EncodeConstants.SIZE_OF_BYTE_IN_BYTES, serializer.getValueLength());
    }

    
    private static MatchEntriesBuilder prepareIcmpv4CodeMatchEntry(short value) {
        MatchEntriesBuilder builder = prepareIcmpv4CodeHeader(false);
        Icmpv4CodeMatchEntryBuilder icmpv4Builder = new Icmpv4CodeMatchEntryBuilder();
        icmpv4Builder.setIcmpv4Code(value);
        builder.addAugmentation(Icmpv4CodeMatchEntry.class, icmpv4Builder.build());
        return builder;
    }

    private static MatchEntriesBuilder prepareIcmpv4CodeHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(Icmpv4Code.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.ICMPV4_CODE, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        assertEquals("Wrong length", EncodeConstants.SIZE_OF_BYTE_IN_BYTES, buffer.readUnsignedByte());
    }
}