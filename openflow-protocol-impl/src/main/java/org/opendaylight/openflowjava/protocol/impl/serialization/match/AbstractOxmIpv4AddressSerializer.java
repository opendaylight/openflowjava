/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Parent for Ipv4 address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmIpv4AddressSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(final MatchEntries entry, final ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        writeIpv4Address(entry, outBuffer);
        writeMask(entry, outBuffer, getValueLength());
    }

    private static void writeIpv4Address(final MatchEntries entry, final ByteBuf out) {
        Iterable<String> addressGroups = ByteBufUtils.DOT_SPLITTER
                .split(entry.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address().getValue());
        for (String group : addressGroups) {
            out.writeByte(Short.parseShort(group));
        }
    }

}
