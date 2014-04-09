/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmMaskDeserializer {

    /**
     * Appends mask to match entry (match entry builder)
     * @param builder builder which the mask will append to
     * @param input input ByteBuf
     * @param matchEntryLength mask length
     */
    public static void addMaskAugmentation(MatchEntriesBuilder builder, ByteBuf input,
            int matchEntryLength) {
        MaskMatchEntryBuilder maskBuilder = new MaskMatchEntryBuilder();
        byte[] mask = new byte[matchEntryLength];
        input.readBytes(mask);
        maskBuilder.setMask(mask);
        builder.addAugmentation(MaskMatchEntry.class, maskBuilder.build());
    }
}
