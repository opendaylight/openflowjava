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
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10AbstractMacAddressActionDeserializer extends AbstractActionDeserializer {

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(getType());
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.addAugmentation(DlAddressAction.class, createDlAugmentationAndPad(input));
        return builder.build();
    }

    private static DlAddressAction createDlAugmentationAndPad(ByteBuf input) {
        DlAddressActionBuilder dlBuilder = new DlAddressActionBuilder();
        byte[] address = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        input.readBytes(address);
        dlBuilder.setDlAddress(new MacAddress(ByteBufUtils.macAddressToString(address)));
        input.skipBytes(ActionConstants.PADDING_IN_DL_ADDRESS_ACTION);
        return dlBuilder.build();
    }
}
