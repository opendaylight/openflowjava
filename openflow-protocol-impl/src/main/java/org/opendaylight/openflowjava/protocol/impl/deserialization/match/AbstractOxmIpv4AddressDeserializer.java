/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

import com.google.common.base.Joiner;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractOxmIpv4AddressDeserializer extends AbstractOxmMatchEntryDeserializer 
        implements OFDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserialize(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addIpv4AddressAugmentation(builder, input);
        if (builder.isHasMask()) {
            OxmMaskDeserializer.addMaskAugmentation(builder, input, EncodeConstants.SIZE_OF_INT_IN_BYTES);
        }
        return builder.build();
    }

    private static void addIpv4AddressAugmentation(MatchEntriesBuilder builder, ByteBuf input) {
        Ipv4AddressMatchEntryBuilder ipv4AddressBuilder = new Ipv4AddressMatchEntryBuilder();
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < EncodeConstants.GROUPS_IN_IPV4_ADDRESS; i++) {
            groups.add(Short.toString(input.readUnsignedByte()));
        }
        Joiner joiner = Joiner.on(".");
        ipv4AddressBuilder.setIpv4Address(new Ipv4Address(joiner.join(groups)));
        builder.addAugmentation(Ipv4AddressMatchEntry.class, ipv4AddressBuilder.build());
    }
}
