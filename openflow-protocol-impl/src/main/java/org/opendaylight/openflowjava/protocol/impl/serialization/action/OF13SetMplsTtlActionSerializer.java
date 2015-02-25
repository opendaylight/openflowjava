/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.util.ActionConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author michal.polkorab
 *
 */
public class OF13SetMplsTtlActionSerializer extends AbstractActionSerializer {



    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        super.serialize(action, outBuffer);
        MplsTtlAction mplsTtl = action.getAugmentation(MplsTtlAction.class);
        outBuffer.writeByte(mplsTtl.getMplsTtl());
        outBuffer.writeZero(ActionConstants.SET_MPLS_TTL_PADDING);
    }

    @Override
    protected int getType() {
        return ActionConstants.SET_MPLS_TTL_CODE;
    }

    @Override
    protected int getLength() {
        return ActionConstants.GENERAL_ACTION_LENGTH;
    }

}
