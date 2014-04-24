/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10AbstractMacAddressActionSerializer extends OF10AbstractActionSerializer {

    private static final byte PADDING_IN_DL_ADDRESS_ACTION = 6;

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        super.serialize(action, outBuffer);
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(action
                .getAugmentation(DlAddressAction.class).getDlAddress().getValue()));
        ByteBufUtils.padBuffer(PADDING_IN_DL_ADDRESS_ACTION, outBuffer);
    }

}
