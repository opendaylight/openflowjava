/*
 * Copyright (c) 2016 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmExperimenterIds;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entries.grouping.MatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.TcpFlagsCase;

/**
 * Created by Anil Vishnoi (avishnoi@Brocade.com) on 7/25/16.
 */
public class OnfOxmTcpFlagsSerializer extends AbstractOxmExperimenterMatchEntrySerializer{

    @Override
    public void serialize(MatchEntry entry, ByteBuf outBuffer) {
        super.serialize(entry, outBuffer);
        TcpFlagsCase tcpFlagsCase = (TcpFlagsCase)entry.getMatchEntryValue();
        outBuffer.writeShort(tcpFlagsCase.getTcpFlags().getFlags());
        if (entry.isHasMask()) {
            outBuffer.writeBytes(tcpFlagsCase.getTcpFlags().getMask());
        }
    }

    /**
     * @return Experimenter match entry ID
     */
    @Override
    protected long getExperimenterId() {
        return OxmExperimenterIds.getExperimenterId(OxmExperimenterIds.TCP_FLAGS);
    }

    /**
     * @return numeric representation of oxm_field
     */
    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.ONFOXM_ET_TCP_FLAGS;
    }

    /**
     * @return numeric representation of oxm_class
     */
    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.EXPERIMENTER_CLASS;
    }

    /**
     * @return match entry value length (without mask length)
     */
    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_SHORT_IN_BYTES;
    }
}
