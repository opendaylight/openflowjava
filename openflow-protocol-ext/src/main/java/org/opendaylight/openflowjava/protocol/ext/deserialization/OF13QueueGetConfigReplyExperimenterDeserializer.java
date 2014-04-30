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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueuePropertyBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13QueueGetConfigReplyExperimenterDeserializer 
        implements OFDeserializer<ExperimenterQueueProperty> {

    private static final int PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY = 4;

    @Override
    public ExperimenterQueueProperty deserialize(ByteBuf input) {
        ExperimenterQueuePropertyBuilder expBuilder = new ExperimenterQueuePropertyBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        input.skipBytes(PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY);
        if (input.readableBytes() > 0) {
            byte[] data = new byte[input.readableBytes()];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        return expBuilder.build();
    }

}
