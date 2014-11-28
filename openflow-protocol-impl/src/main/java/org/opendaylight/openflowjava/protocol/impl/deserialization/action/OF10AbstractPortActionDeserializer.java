/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.util.ActionConstants;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10AbstractPortActionDeserializer extends AbstractActionDeserializer {

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(getType());
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        createPortAugmentation(input, builder);
        input.skipBytes(ActionConstants.PADDING_IN_TP_PORT_ACTION);
        return builder.build();
    }

    protected static void createPortAugmentation(ByteBuf input, ActionBuilder actionBuilder) {
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber((long) input.readUnsignedShort()));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
    }
}
