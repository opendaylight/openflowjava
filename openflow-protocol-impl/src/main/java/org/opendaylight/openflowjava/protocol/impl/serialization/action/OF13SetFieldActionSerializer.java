/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.ActionConstants;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.ExperimenterClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public class OF13SetFieldActionSerializer implements OFSerializer<Action>,
        HeaderSerializer<Action>, SerializerRegistryInjector {

    private SerializerRegistry registry;

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        int startIndex = outBuffer.writerIndex();
        outBuffer.writeShort(ActionConstants.SET_FIELD_CODE);
        int lengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        OxmFieldsAction oxmField = action.getAugmentation(OxmFieldsAction.class);
        MatchEntries entry = oxmField.getMatchEntries().get(0);
        MatchEntrySerializerKey<?, ?> key = new MatchEntrySerializerKey<>(
                EncodeConstants.OF13_VERSION_ID, entry.getOxmClass(), entry.getOxmMatchField());
        if (entry.getOxmClass().equals(ExperimenterClass.class)) {
            key.setExperimenterId(entry.getAugmentation(ExperimenterMatchEntry.class).getExperimenter());
        } else {
            key.setExperimenterId(null);
        }
        OFSerializer<MatchEntries> serializer = registry.getSerializer(key);
        serializer.serialize(entry, outBuffer);
        int paddingRemainder = (outBuffer.writerIndex() - startIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            ByteBufUtils.padBuffer(EncodeConstants.PADDING - paddingRemainder, outBuffer);
        }
        outBuffer.setShort(lengthIndex, outBuffer.writerIndex() - startIndex);
    }

    @Override
    public void serializeHeader(Action input, ByteBuf outBuffer) {
        outBuffer.writeShort(ActionConstants.SET_FIELD_CODE);
        outBuffer.writeShort(ActionConstants.ACTION_IDS_LENGTH);
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        registry = serializerRegistry;
    }

}
