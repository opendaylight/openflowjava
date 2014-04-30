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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeaturePropertyBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13TableFeaturesExperimenterDeserializer
        implements OFDeserializer<ExperimenterRelatedTableFeatureProperty>{

    @Override
    public ExperimenterRelatedTableFeatureProperty deserialize(ByteBuf input) {
        ExperimenterRelatedTableFeaturePropertyBuilder expBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        expBuilder.setExpType(input.readUnsignedInt());
        if (input.readableBytes() > 0) {
            byte[] data = new byte[input.readableBytes()];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        return expBuilder.build();
    }

}
