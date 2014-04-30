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
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueuePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueuePropertyBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13QueueGetConfigReplyExperimenterDeserializer 
        implements OFDeserializer<QueueProperty> {

    private static final int PADDING_IN_QUEUE_PROPERTY_HEADER = 4;
    private static final int PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY = 4;

    @Override
    public QueueProperty deserialize(ByteBuf input) {
        QueuePropertyBuilder builder = new QueuePropertyBuilder();
        QueueProperties property = QueueProperties.forValue(input.readUnsignedShort());
        builder.setProperty(property);
        int length = input.readUnsignedShort();
        input.skipBytes(PADDING_IN_QUEUE_PROPERTY_HEADER);
        ExperimenterQueuePropertyBuilder expBuilder = new ExperimenterQueuePropertyBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        input.skipBytes(PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY);
        // extract experimenter_data length
        length = length - 2 * ExtConstants.SIZE_OF_SHORT_IN_BYTES - PADDING_IN_QUEUE_PROPERTY_HEADER
                - ExtConstants.SIZE_OF_INT_IN_BYTES - PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY;
        if (length > 0) {
            byte[] data = new byte[length];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        builder.addAugmentation(ExperimenterQueueProperty.class, expBuilder.build());
        return builder.build();
    }

}
