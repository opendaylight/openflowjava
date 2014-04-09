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
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Exthdr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmIpv6ExtHdrDeserializer extends AbstractOxmMatchEntryDeserializer
        implements OFDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserialize(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addIpv6ExtHdrAugmentation(input, builder);
        if (builder.isHasMask()) {
            OxmMaskDeserializer.addMaskAugmentation(builder, input, EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        }
        return builder.build();
    }

    private static void addIpv6ExtHdrAugmentation(ByteBuf input,
            MatchEntriesBuilder builder) {
        PseudoFieldMatchEntryBuilder pseudoBuilder = new PseudoFieldMatchEntryBuilder();
        int bitmap = input.readUnsignedShort();
        final Boolean NONEXT = ((bitmap) & (1<<0)) != 0;
        final Boolean ESP = ((bitmap) & (1<<1)) != 0;
        final Boolean AUTH = ((bitmap) & (1<<2)) != 0;
        final Boolean DEST = ((bitmap) & (1<<3)) != 0;
        final Boolean FRAG = ((bitmap) & (1<<4)) != 0;
        final Boolean ROUTER = ((bitmap) & (1<<5)) != 0;
        final Boolean HOP = ((bitmap) & (1<<6)) != 0;
        final Boolean UNREP = ((bitmap) & (1<<7)) != 0;
        final Boolean UNSEQ = ((bitmap) & (1<<8)) != 0;
        pseudoBuilder.setPseudoField(new Ipv6ExthdrFlags(AUTH, DEST, ESP, FRAG, HOP, NONEXT, ROUTER, UNREP, UNSEQ));
        builder.addAugmentation(PseudoFieldMatchEntry.class, pseudoBuilder.build());
    }

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return Ipv6Exthdr.class;
    }

    @Override
    protected Class<? extends Clazz> getOxmClass() {
        return OpenflowBasicClass.class;
    }
}