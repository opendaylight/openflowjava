/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10VendorActionDeserializer implements OFDeserializer<Action> {

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        int length = input.readUnsignedShort();
        // subtract experimenter header length
        length -= EncodeConstants.EXPERIMENTER_IDS_LENGTH;
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        if (length > 0) {
            byte[] data = new byte[length];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        builder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        return builder.build();
    }
}