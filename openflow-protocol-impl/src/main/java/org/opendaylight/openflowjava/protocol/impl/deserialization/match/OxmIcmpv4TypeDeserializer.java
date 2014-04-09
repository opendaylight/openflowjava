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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Icmpv4TypeMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Clazz;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Icmpv4Type;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OxmIcmpv4TypeDeserializer extends AbstractOxmMatchEntryDeserializer
        implements OFDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserialize(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        addIcmpv4TypeAugmentation(input, builder);
        return builder.build();
    }

    private static void addIcmpv4TypeAugmentation(ByteBuf input,
            MatchEntriesBuilder builder) {
        Icmpv4TypeMatchEntryBuilder icmpv4TypeBuilder = new Icmpv4TypeMatchEntryBuilder();
        icmpv4TypeBuilder.setIcmpv4Type(input.readUnsignedByte());
        builder.addAugmentation(Icmpv4TypeMatchEntry.class, icmpv4TypeBuilder.build());
    }

    @Override
    protected Class<? extends MatchField> getOxmField() {
        return Icmpv4Type.class;
    }

    @Override
    protected Class<? extends Clazz> getOxmClass() {
        return OpenflowBasicClass.class;
    }
}
