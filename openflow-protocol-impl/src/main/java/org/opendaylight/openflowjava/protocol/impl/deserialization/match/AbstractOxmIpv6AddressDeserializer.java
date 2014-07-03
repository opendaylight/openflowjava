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
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
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

    private static void addIpv6AddressAugmentation(final MatchEntriesBuilder builder, final ByteBuf input) {
        Ipv6AddressMatchEntryBuilder ipv6AddressBuilder = new Ipv6AddressMatchEntryBuilder();
        ipv6AddressBuilder.setIpv6Address(new Ipv6Address(ByteBufUtils.readIpv6Address(input)));
        builder.addAugmentation(Ipv6AddressMatchEntry.class, ipv6AddressBuilder.build());
    }
}
