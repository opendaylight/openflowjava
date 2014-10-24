/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 *
 */
public abstract class AbstractOxmMatchEntryDeserializer implements HeaderDeserializer<MatchEntries> {

    @Override
    public MatchEntries deserializeHeader(ByteBuf input) {
        MatchEntriesBuilder builder = processHeader(getOxmClass(), getOxmField(), input);
        return builder.build();
    }

    /**
     * @return oxm_field class
     */
    protected abstract Class<? extends MatchField> getOxmField();

    /**
     * @return oxm_class class
     */
    protected abstract Class<? extends OxmClassBase> getOxmClass();

    /**
     * Prepares match entry header - sets oxm_class, oxm_field, hasMask
     *  + sets the buffer.readerIndex() to the end of match entry 
     *  - where augmentation starts
     * @param oxmClass oxm class type
     * @param oxmField oxm field type
     * @param input input bytebuf
     * @return MatchEntriesBuilder which can be filled with MatchEntry augmentation
     */
    protected MatchEntriesBuilder processHeader(Class<? extends OxmClassBase> oxmClass,
            Class<? extends MatchField> oxmField, ByteBuf input) {
        MatchEntriesBuilder builder = new MatchEntriesBuilder();
        builder.setOxmClass(oxmClass);
        // skip oxm_class (provided)
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setOxmMatchField(oxmField);
        boolean hasMask = (input.readUnsignedByte() & 1) != 0;
        builder.setHasMask(hasMask);
        // skip match entry length - not needed
        input.skipBytes(EncodeConstants.SIZE_OF_BYTE_IN_BYTES);
        return builder;
    }
    
}
