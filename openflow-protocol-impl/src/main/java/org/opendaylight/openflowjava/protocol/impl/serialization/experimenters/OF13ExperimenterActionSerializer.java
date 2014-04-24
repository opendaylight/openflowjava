/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

public class OF13ExperimenterActionSerializer implements OFSerializer<Action>,
        HeaderSerializer<Action> {

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        int actionStartIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        int actionLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        ExperimenterAction expAction = action.getAugmentation(ExperimenterAction.class);
        outBuffer.writeInt(expAction.getExperimenter().intValue());
        if (expAction.getData() != null) {
            outBuffer.writeBytes(expAction.getData());
        }
        outBuffer.setShort(actionLengthIndex, outBuffer.writerIndex() - actionStartIndex);
    }

    @Override
    public void serializeHeader(Action action, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_IDS_LENGTH);
        ExperimenterAction expAction = action.getAugmentation(ExperimenterAction.class);
        outBuffer.writeInt(expAction.getExperimenter().intValue());
    }

}
