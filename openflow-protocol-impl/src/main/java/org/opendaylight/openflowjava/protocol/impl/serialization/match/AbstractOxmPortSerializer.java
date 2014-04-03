/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Parent for port(16-bit) based match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmPortSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        outBuffer.writeShort(entry.getAugmentation(PortMatchEntry.class).getPort().getValue().intValue());
    }
}
