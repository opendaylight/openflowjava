/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.exaction.rev130731.NxActionResubmitAugment;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitSerializer implements OFSerializer<Action>,
        HeaderSerializer<Action> {

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        int actionStartIndex = outBuffer.writerIndex();
        outBuffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        int actionLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(ExtConstants.EMPTY_LENGTH);
        NxActionResubmitAugment expAction = action.getAugmentation(NxActionResubmitAugment.class);
        outBuffer.writeInt(expAction.getVendor().intValue());
        outBuffer.writeShort(expAction.getSubtype());
        outBuffer.writeShort(expAction.getInPort());
        outBuffer.writeByte(expAction.getTable());
        outBuffer.writeZero(3);
        outBuffer.setShort(actionLengthIndex, outBuffer.writerIndex() - actionStartIndex);
    }

    @Override
    public void serializeHeader(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(ExtConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(ExtConstants.EXPERIMENTER_IDS_LENGTH);
        NxActionResubmitAugment expAction = action.getAugmentation(NxActionResubmitAugment.class);
        outBuffer.writeInt(expAction.getVendor().intValue());
    }

}
