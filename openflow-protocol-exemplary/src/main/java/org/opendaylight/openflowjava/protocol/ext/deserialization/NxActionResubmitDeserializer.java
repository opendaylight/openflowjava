/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.exaction.rev130731.NxActionResubmitAugment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.exaction.rev130731.NxActionResubmitAugmentBuilder;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitDeserializer implements OFDeserializer<Action>,
        HeaderDeserializer<Action> {

    @Override
    public Action deserializeHeader(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        NxActionResubmitAugmentBuilder expBuilder = new NxActionResubmitAugmentBuilder();
        expBuilder.setVendor(input.readUnsignedInt());
        builder.addAugmentation(NxActionResubmitAugment.class, expBuilder.build());
        return builder.build();
    }

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        input.skipBytes(ExtConstants.SIZE_OF_SHORT_IN_BYTES);
        NxActionResubmitAugmentBuilder expBuilder = new NxActionResubmitAugmentBuilder();
        expBuilder.setVendor(input.readUnsignedInt());
        expBuilder.setSubtype(input.readUnsignedShort());
        expBuilder.setInPort(input.readUnsignedShort());
        expBuilder.setTable(input.readUnsignedByte());
        input.skipBytes(3);
        return builder.build();
    }

}
