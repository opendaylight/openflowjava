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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeaturePropertiesBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13TableFeaturesExperimenterDeserializer
        implements OFDeserializer<TableFeatureProperties>{

    @Override
    public TableFeatureProperties deserialize(ByteBuf input) {
        TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
        TableFeaturesPropType type = TableFeaturesPropType.forValue(input.readUnsignedShort());
        builder.setType(type);
        int length = input.readUnsignedShort();
        ExperimenterRelatedTableFeaturePropertyBuilder expBuilder =
                new ExperimenterRelatedTableFeaturePropertyBuilder();
        expBuilder.setExperimenter(input.readUnsignedInt());
        expBuilder.setExpType(input.readUnsignedInt());
        // extract experimenter_data length
        length = length - 2 * ExtConstants.SIZE_OF_SHORT_IN_BYTES - 2 * ExtConstants.SIZE_OF_INT_IN_BYTES;
        if (length > 0) {
            byte[] data = new byte[length];
            input.readBytes(data);
            expBuilder.setData(data);
        }
        builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class, expBuilder.build());
        return builder.build();
    }

}
