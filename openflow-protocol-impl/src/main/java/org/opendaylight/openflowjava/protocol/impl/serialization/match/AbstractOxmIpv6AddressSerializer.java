/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Parent for Ipv6 address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmIpv6AddressSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        String textAddress = entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address().getValue();
        List<String> address;
        if (textAddress.equals("::")) {
            String[] tmp = new String[EncodeConstants.GROUPS_IN_IPV6_ADDRESS];
            Arrays.fill(tmp, "0");
            address = Arrays.asList(tmp);
        } else {
            address = parseIpv6Address(Lists.newArrayList(Splitter.on(":").split(textAddress)));
        }
        for (String group : address) {
            outBuffer.writeShort(Integer.parseInt(group, 16));
        }
        writeMask(entry, outBuffer, getValueLength());
    }

    private static List<String> parseIpv6Address(ArrayList<String> addressGroups) {
        int countEmpty = 0;
        for (String group : addressGroups) {
            if (group.equals("")) {
                countEmpty++;
            }
        }
        List<String> ready = new ArrayList<>(EncodeConstants.GROUPS_IN_IPV6_ADDRESS);
        switch (countEmpty) {
        case 0:
            ready = addressGroups;
            break;
        case 1:
            int zerosToBePushed = EncodeConstants.GROUPS_IN_IPV6_ADDRESS - addressGroups.size() + 1;
            for (String group : addressGroups) {
                if (group.equals("")) {
                    for (int j = 0; j < zerosToBePushed; j++) {
                        ready.add("0");
                    }
                } else {
                    ready.add(group);
                }
            }
            break;
        case 2:
            ready = Arrays.asList("0", "0", "0", "0", "0", "0", "0",
                    addressGroups.get(addressGroups.size() - 1));
            break;
        default:
            throw new IllegalStateException("Incorrect ipv6 address");
        }
        return ready;
    }
}
