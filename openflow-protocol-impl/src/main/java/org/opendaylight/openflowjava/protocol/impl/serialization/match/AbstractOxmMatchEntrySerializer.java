/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaskMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * Parent for all match entry serializers
 * @author michal.polkorab
 */
public abstract class AbstractOxmMatchEntrySerializer
    implements OFSerializer<MatchEntries>, HeaderSerializer<MatchEntries>{

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        serializeHeader(entry, outBuffer);
    }

    @Override
    public void serializeHeader(MatchEntries entry, ByteBuf outBuffer) {
        outBuffer.writeShort(getOxmClassCode());
        writeOxmFieldAndLength(outBuffer, getOxmFieldCode(), entry.isHasMask(),
                getValueLength());
    }

    protected static void writeMask(MatchEntries entry, ByteBuf out, int length) {
        if (entry.isHasMask()) {
            byte[] mask = entry.getAugmentation(MaskMatchEntry.class).getMask();
            if (mask != null && mask.length != length) {
                throw new IllegalArgumentException("incorrect length of mask: "+
                        mask.length + ", expected: " + length);
            }
            out.writeBytes(mask);
        }
    }

    protected static void writeOxmFieldAndLength(ByteBuf out, int fieldValue, boolean hasMask, int lengthArg) {
        int fieldAndMask = fieldValue << 1;
        int length = lengthArg;
        if (hasMask) {
            fieldAndMask |= 1;
            length *= 2;
        }
        out.writeByte(fieldAndMask);
        out.writeByte(length);
    }

    /**
     * @return numeric representation of oxm_field
     */
    protected abstract int getOxmFieldCode();

    /**
     * @return numeric representation of oxm_class
     */
    protected abstract int getOxmClassCode();

    /**
     * @return match entry value length (without mask length)
     */
    protected abstract int getValueLength();

}
