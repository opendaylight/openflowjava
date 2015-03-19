/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmIpv6SrcSerializerTest {

    OxmIpv6SrcSerializer serializer = new OxmIpv6SrcSerializer();

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithoutMask() {
        MatchEntriesBuilder builder = prepareMatchEntry(false, "aaaa:bbbb:1111:2222::");

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        Assert.assertEquals("Wrong ipv6 address", 43690, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 48059, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 4369, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 8738, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    /**
     * Test correct serialization
     */
    @Test
    public void testSerializeWithLeadingZeros() {
        MatchEntriesBuilder builder = prepareMatchEntry(false, "::aaaa:bbbb:1111:2222");

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(builder.build(), buffer);

        checkHeader(buffer, false);
        Assert.assertEquals("Wrong ipv6 address", 43690, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 48059, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 4369, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 8738, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong ipv6 address", 0, buffer.readUnsignedShort());
        assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

    private static MatchEntriesBuilder prepareMatchEntry(boolean hasMask, String value) {
        MatchEntriesBuilder builder = prepareHeader(hasMask);
        if (hasMask) {
            MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
            maskBuilder.setMask(new byte[]{15, 15, 0, 0});
            builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
        }
        Ipv6AddressMatchEntryBuilder addressBuilder = new Ipv6AddressMatchEntryBuilder();
        addressBuilder.setIpv6Address(new Ipv6Address(value));
        builder.addAugmentation(Ipv6AddressMatchEntry.class, addressBuilder.build());
        return builder;
    }

    private static MatchEntriesBuilder prepareHeader(boolean hasMask) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(OpenflowBasicClass.class);
        builder.setOxmMatchField(Ipv6Src.class);
        builder.setHasMask(hasMask);
        return builder;
    }

    private static void checkHeader(ByteBuf buffer, boolean hasMask) {
        assertEquals("Wrong oxm-class", OxmMatchConstants.OPENFLOW_BASIC_CLASS, buffer.readUnsignedShort());
        short fieldAndMask = buffer.readUnsignedByte();
        assertEquals("Wrong oxm-field", OxmMatchConstants.IPV6_SRC, fieldAndMask >>> 1);
        assertEquals("Wrong hasMask", hasMask, (fieldAndMask & 1) != 0);
        if (hasMask) {
            assertEquals("Wrong length", 2 * EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES,
                    buffer.readUnsignedByte());
        } else {
            assertEquals("Wrong length", EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES, buffer.readUnsignedByte());
        }
    }
}
