/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

import com.google.common.net.InetAddresses;

/**
 * Parent for Ipv6 address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmIpv6AddressSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(final MatchEntries entry, final ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        String textAddress = entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address().getValue();
        if (InetAddresses.isInetAddress(textAddress)) {
            byte[] binaryAddress = InetAddresses.forString(textAddress).getAddress();
            outBuffer.writeBytes(binaryAddress);
        } else {
            throw new IllegalArgumentException("Invalid ipv6 address received: " + textAddress);
        }
        writeMask(entry, outBuffer, getValueLength());
    }
}
