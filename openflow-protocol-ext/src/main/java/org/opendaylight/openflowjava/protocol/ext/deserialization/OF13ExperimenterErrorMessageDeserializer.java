/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterError;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterErrorBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13ExperimenterErrorMessageDeserializer implements OFDeserializer<ExperimenterError> {

    @Override
    public ExperimenterError deserialize(ByteBuf message) {
        ExperimenterErrorBuilder expBuilder = new ExperimenterErrorBuilder();
        expBuilder.setExpType(message.readUnsignedShort());
        expBuilder.setExperimenter(message.readUnsignedInt());
        if (message.readableBytes() > 0) {
            byte[] data = new byte[message.readableBytes()];
            message.readBytes(data);
            expBuilder.setExpData(data);
        }
        return expBuilder.build();
    }

}
