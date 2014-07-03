/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PseudoFieldMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Ipv6ExthdrFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public class OxmIpv6ExtHdrSerializer extends AbstractOxmMatchEntrySerializer {

    @Override
    public void serialize(final MatchEntries entry, final ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        Ipv6ExthdrFlags pseudoField = entry.getAugmentation(PseudoFieldMatchEntry.class).getPseudoField();
        int bitmap = ByteBufUtils.fillBitMask(0,
                pseudoField.isNonext(),
                pseudoField.isEsp(),
                pseudoField.isAuth(),
                pseudoField.isDest(),
                pseudoField.isFrag(),
                pseudoField.isRouter(),
                pseudoField.isHop(),
                pseudoField.isUnrep(),
                pseudoField.isUnseq());
        outBuffer.writeShort(bitmap);
        writeMask(entry, outBuffer, getValueLength());
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
}
