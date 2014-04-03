/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public class OxmIpv6ExtHdrSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(MatchEntries entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        Ipv6ExthdrFlags pseudoField = entry.getAugmentation(PseudoFieldMatchEntry.class).getPseudoField();
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(0, pseudoField.isNonext());
        map.put(1, pseudoField.isEsp());
        map.put(2, pseudoField.isAuth());
        map.put(3, pseudoField.isDest());
        map.put(4, pseudoField.isFrag());
        map.put(5, pseudoField.isRouter());
        map.put(6, pseudoField.isHop());
        map.put(7, pseudoField.isUnrep());
        map.put(8, pseudoField.isUnseq());
        int bitmap = ByteBufUtils.fillBitMaskFromMap(map);
        outBuffer.writeShort(bitmap);
        writeMask(entry, outBuffer, getValueLength());
    }

    @Override
    public void serializeHeader(MatchEntries entry, ByteBuf outBuffer) {
        super.serializeHeader(entry, outBuffer);
    }

    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.OPENFLOW_BASIC_CLASS;
    }

    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.IPV6_EXTHDR;
    }

    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        // TODO Auto-generated method stub
    }
}
