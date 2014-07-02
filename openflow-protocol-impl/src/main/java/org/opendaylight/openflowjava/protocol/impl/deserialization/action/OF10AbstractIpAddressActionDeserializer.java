/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yangtools.yang.binding.Augmentation;

/**
 * @author michal.polkorab
 *
 */
public abstract class OF10AbstractIpAddressActionDeserializer extends AbstractActionDeserializer {

    @Override
    public Action deserialize(final ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(getType());
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.addAugmentation(IpAddressAction.class,
                createNwAddressAugmentationAndPad(input));
        return builder.build();
    }

    private static Augmentation<Action> createNwAddressAugmentationAndPad(final ByteBuf input) {
        IpAddressActionBuilder ipBuilder = new IpAddressActionBuilder();
        ipBuilder.setIpAddress(new Ipv4Address(ByteBufUtils.readIpv4Address(input)));
        return ipBuilder.build();
    }

}
