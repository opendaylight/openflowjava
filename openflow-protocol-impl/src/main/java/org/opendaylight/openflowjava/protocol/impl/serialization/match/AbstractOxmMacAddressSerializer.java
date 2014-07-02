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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MacAddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Parent for MAC address based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmMacAddressSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        String macAddress = entry.getAugmentation(MacAddressMatchEntry.class).getMacAddress().getValue();
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(macAddress)); // 48 b + mask [OF 1.3.2 spec]
        writeMask(entry, outBuffer, getValueLength());
    }
}
