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
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Ipv6Exthdr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;
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
        final Boolean nonExt = ((bitmap) & (1<<0)) != 0;
        final Boolean esp = ((bitmap) & (1<<1)) != 0;
        final Boolean auth = ((bitmap) & (1<<2)) != 0;
        final Boolean dest = ((bitmap) & (1<<3)) != 0;
        final Boolean frag = ((bitmap) & (1<<4)) != 0;
        final Boolean router = ((bitmap) & (1<<5)) != 0;
        final Boolean hop = ((bitmap) & (1<<6)) != 0;
        final Boolean unRep = ((bitmap) & (1<<7)) != 0;
        final Boolean unSeq = ((bitmap) & (1<<8)) != 0;
        pseudoBuilder.setPseudoField(new Ipv6ExthdrFlags(auth, dest, esp, frag, hop, nonExt, router, unRep, unSeq));
        builder.addAugmentation(PseudoFieldMatchEntry.class, pseudoBuilder.build());
    }

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return Ipv6Exthdr.class;
    }

    @Override
    protected Class<? extends OxmClassBase> getOxmClass() {
        return OpenflowBasicClass.class;
    }
}