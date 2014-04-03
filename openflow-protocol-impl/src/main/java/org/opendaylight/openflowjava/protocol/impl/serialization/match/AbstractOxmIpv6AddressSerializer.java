/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractOxmIpv6AddressSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        String textAddress = entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address().getValue();
        String[] address;
        if (textAddress.equals("::")) {
            address = new String[EncodeConstants.GROUPS_IN_IPV6_ADDRESS];
            Arrays.fill(address, "0");
        } else {
            address = parseIpv6Address(textAddress.split(":"));
        }
        for (int i = 0; i < address.length; i++) {
            outBuffer.writeShort(Integer.parseInt(address[i], 16));
        }
        writeMask(entry, outBuffer, getValueLength());
    }

    @Override
    public void serializeHeader(MatchEntries entry, ByteBuf outBuffer) {
        super.serializeHeader(entry, outBuffer);
    }

    private static String[] parseIpv6Address(String[] addressGroups) {
        int countEmpty = 0;
        for (int i = 0; i < addressGroups.length; i++) {
            if (addressGroups[i].equals("")){
                countEmpty++;
            }
        }
        String[] ready = new String[EncodeConstants.GROUPS_IN_IPV6_ADDRESS];
        switch (countEmpty) {
        case 0:
            ready = addressGroups;
            break;
        case 1:
            int zerosToBePushed = EncodeConstants.GROUPS_IN_IPV6_ADDRESS - addressGroups.length + 1;
            int index = 0;
            for (int i = 0; i < addressGroups.length; i++) {
                if (addressGroups[i].equals("")) {
                    for (int j = 0; j < zerosToBePushed; j++) {
                        ready[index] = "0";
                        index++;
                    }
                } else {
                    ready[index] = addressGroups[i];
                    index++;
                }
            }
            break;
        case 2:
            Arrays.fill(ready, "0");
            ready[ready.length - 1] = addressGroups[addressGroups.length - 1];
            break;
        default:
            throw new IllegalStateException("Incorrect ipv6 address");
        }
        return ready;
    }
}
