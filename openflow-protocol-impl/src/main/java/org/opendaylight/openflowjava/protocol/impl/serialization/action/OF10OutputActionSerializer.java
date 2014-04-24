/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

public class OF10OutputActionSerializer extends OF10AbstractActionSerializer {

    private static final byte OUTPUT_CODE = 0;
    private static final byte OUTPUT_LENGTH = 8;

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        super.serialize(action, outBuffer);
        PortAction port = action.getAugmentation(PortAction.class);
        outBuffer.writeShort(port.getPort().getValue().intValue());
        MaxLengthAction maxlength = action.getAugmentation(MaxLengthAction.class);
        outBuffer.writeShort(maxlength.getMaxLength());
    }

    @Override
    protected int getType() {
        return OUTPUT_CODE;
    }

    @Override
    protected int getLength() {
        return OUTPUT_LENGTH;
    }

}
