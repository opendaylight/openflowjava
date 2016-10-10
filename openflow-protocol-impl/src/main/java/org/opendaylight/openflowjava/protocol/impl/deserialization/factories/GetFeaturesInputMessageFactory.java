/*
 * Copyright (c) 2015 NetIDE Consortium and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.VersionAssignableFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInputBuilder;

/**
 * Translates FeaturesRequest messages.
 * OpenFlow protocol versions: 1.0, 1.3, 1.4, 1.5.
 */
public class GetFeaturesInputMessageFactory extends VersionAssignableFactory implements OFDeserializer<GetFeaturesInput> {

    @Override
    public GetFeaturesInput deserialize(ByteBuf rawMessage) {
        GetFeaturesInputBuilder builder = new GetFeaturesInputBuilder();
        builder.setVersion(getVersion());
        builder.setXid(rawMessage.readUnsignedInt());
        return builder.build();
    }
}