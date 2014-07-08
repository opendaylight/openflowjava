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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmit;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitSerializer implements OFSerializer<Action>,
        HeaderSerializer<Action> {

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        int actionStartIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        int actionLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        NxActionResubmit expAction = action.getAugmentation(NxActionResubmit.class);
        outBuffer.writeInt(expAction.getVendor().intValue());
        outBuffer.writeShort(expAction.getSubtype());
        outBuffer.writeShort(expAction.getInPort());
        outBuffer.writeByte(expAction.getTable());
        outBuffer.writeZero(3);
        outBuffer.setShort(actionLengthIndex, outBuffer.writerIndex() - actionStartIndex);
    }

    @Override
    public void serializeHeader(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_IDS_LENGTH);
        NxActionResubmit expAction = action.getAugmentation(NxActionResubmit.class);
        outBuffer.writeInt(expAction.getVendor().intValue());
    }
}