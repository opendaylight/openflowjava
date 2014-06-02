/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

import com.google.common.base.Splitter;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10AbstractIpAddressActionSerializer extends AbstractActionSerializer {

    @Override
    public void serialize(Action action, ByteBuf outBuffer) {
        super.serialize(action, outBuffer);
        Iterable<String> addressGroups = Splitter.on(".")
                .split(action.getAugmentation(IpAddressAction.class).getIpAddress().getValue());
        for (String group : addressGroups) {
            outBuffer.writeByte(Short.parseShort(group));
        }
    }
}
