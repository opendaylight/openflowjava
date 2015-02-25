/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.action;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.OxmFieldsActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;

/**
 * @author michal.polkorab
 *
 */
public class OF13SetFieldActionDeserializer extends AbstractActionDeserializer
        implements DeserializerRegistryInjector {

    private DeserializerRegistry registry;

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        builder.setType(getType());
        int startIndex = input.readerIndex();
        input.skipBytes(2 * EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        int oxmClass = input.getUnsignedShort(input.readerIndex());
        // get oxm_field & hasMask byte and extract the field value
        int oxmField = input.getUnsignedByte(input.readerIndex()
                + EncodeConstants.SIZE_OF_SHORT_IN_BYTES) >>> 1;
        MatchEntryDeserializerKey key = new MatchEntryDeserializerKey(EncodeConstants.OF13_VERSION_ID,
                oxmClass, oxmField);
        if (oxmClass == EncodeConstants.EXPERIMENTER_VALUE) {
            long expId = input.getUnsignedInt(input.readerIndex() + EncodeConstants.SIZE_OF_SHORT_IN_BYTES
                    + 2 * EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
            key.setExperimenterId(expId);
        }
        OFDeserializer<MatchEntry> matchDeserializer = registry.getDeserializer(key);
        List<MatchEntry> entry = new ArrayList<>();
        entry.add(matchDeserializer.deserialize(input));
        matchEntries.setMatchEntry(entry);
        builder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        int paddingRemainder = (input.readerIndex() - startIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            input.skipBytes(EncodeConstants.PADDING - paddingRemainder);
        }
        return builder.build();
    }

    @Override
    protected Class<? extends ActionBase> getType() {
        return SetField.class;
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        this.registry = deserializerRegistry;
    }

}
