/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.ext.util.ExtBufferUtils;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * @author michal.polkorab
 *
 */
public class OF13TableFeatExpSerializer implements OFSerializer<TableFeatureProperties> {

    private static final int EXPERIMENTER_CODE = 65534; // 0xFFFE
    private static final int EXPERIMENTER_MISS_CODE = 65535; // 0xFFFF
    
    @Override
    public void serialize(TableFeatureProperties property, ByteBuf outBuffer) {
        int startIndex = outBuffer.writerIndex();
        if (property.getType().equals(TableFeaturesPropType.OFPTFPTEXPERIMENTER)) {
            outBuffer.writeShort(EXPERIMENTER_CODE);
        } else {
            outBuffer.writeShort(EXPERIMENTER_MISS_CODE);
        }
        int lengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(ExtConstants.EMPTY_LENGTH);
        ExperimenterRelatedTableFeatureProperty exp = property.
                getAugmentation(ExperimenterRelatedTableFeatureProperty.class);
        outBuffer.writeInt(exp.getExperimenter().intValue());
        outBuffer.writeInt(exp.getExpType().intValue());
        byte[] data = exp.getData();
        if (data != null) {
            outBuffer.writeBytes(data);
        }
        int paddingRemainder = (outBuffer.writerIndex() - startIndex) % ExtConstants.PADDING;
        if (paddingRemainder != 0) {
            int padding = ExtConstants.PADDING - paddingRemainder;
            ExtBufferUtils.padBuffer(padding, outBuffer);
        }
        outBuffer.setShort(lengthIndex, outBuffer.writerIndex() - startIndex);
    }

}
