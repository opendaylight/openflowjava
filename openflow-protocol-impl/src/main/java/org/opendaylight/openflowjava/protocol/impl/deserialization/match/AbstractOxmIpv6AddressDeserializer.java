/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractOxmIpv6AddressDeserializer extends AbstractOxmMatchEntryDeserializer
        implements OFDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserialize(final ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addIpv6AddressAugmentation(builder, input);
        if (builder.isHasMask()) {
            OxmMaskDeserializer.addMaskAugmentation(builder, input,
                    EncodeConstants.SIZE_OF_IPV6_ADDRESS_IN_BYTES);
        }
        return builder.build();
    }

    private static void appendHexShort(final StringBuilder sb, final int val) {
        sb.append(ByteBufUtils.HEX_CHARS[val >> 12]);
        sb.append(ByteBufUtils.HEX_CHARS[val >>  8]);
        sb.append(ByteBufUtils.HEX_CHARS[val >>  4]);
        sb.append(ByteBufUtils.HEX_CHARS[val &  15]);
    }

    private static void addIpv6AddressAugmentation(final MatchEntriesBuilder builder, final ByteBuf input) {
        final StringBuilder sb = new StringBuilder(EncodeConstants.GROUPS_IN_IPV6_ADDRESS * 5 - 1);

        appendHexShort(sb, input.readUnsignedShort());
        for (int i = 1; i < EncodeConstants.GROUPS_IN_IPV6_ADDRESS; i++) {
            sb.append(':');
            appendHexShort(sb, input.readUnsignedShort());
        }

        Ipv6AddressMatchEntryBuilder ipv6AddressBuilder = new Ipv6AddressMatchEntryBuilder();
        ipv6AddressBuilder.setIpv6Address(new Ipv6Address(sb.toString()));
        builder.addAugmentation(Ipv6AddressMatchEntry.class, ipv6AddressBuilder.build());
    }
}
