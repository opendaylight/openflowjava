/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.nx.NxResubmitActionRegistrator;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmitAugment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitSerializer implements OFSerializer<Action>,
        HeaderSerializer<Action> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(NxActionResubmitSerializer.class);
    private static final byte NX_ACTION_RESUBMIT_LENGTH = 16;

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        LOGGER.error("SERIALIZING NX ACTION");
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(NX_ACTION_RESUBMIT_LENGTH);
        NxActionResubmitAugment expAction = action.getAugmentation(NxActionResubmitAugment.class);
        outBuffer.writeInt(NxResubmitActionRegistrator.NICIRA_EXPERIMENTER_ID.intValue());
        outBuffer.writeShort(expAction.getSubtype());
        outBuffer.writeShort(expAction.getInPort());
        outBuffer.writeByte(expAction.getTable());
        outBuffer.writeZero(3);
    }

    @Override
    public void serializeHeader(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_IDS_LENGTH);
        outBuffer.writeInt(NxResubmitActionRegistrator.NICIRA_EXPERIMENTER_ID.intValue());
    }
}