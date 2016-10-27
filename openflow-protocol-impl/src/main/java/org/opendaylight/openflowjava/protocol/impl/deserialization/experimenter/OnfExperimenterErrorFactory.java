/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.experimenter;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundleErrorCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdError;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev150225.ExperimenterIdErrorBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ErrorType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessageBuilder;

/**
 * Translates bundle errors (OpenFlow v1.3 extension #230).
 */
public class OnfExperimenterErrorFactory implements OFDeserializer<ErrorMessage> {
    @Override
    public ErrorMessage deserialize(ByteBuf message) {
        ErrorMessageBuilder builder = new ErrorMessageBuilder();
        builder.setVersion((short) EncodeConstants.OF13_VERSION_ID);
        builder.setXid(message.readUnsignedInt());
        int type = message.readUnsignedShort();
        int code = message.readUnsignedShort();
        builder.setType(type);
        builder.setTypeString(ErrorType.forValue(type).getName());
        builder.setCode(code);
        builder.setCodeString(BundleErrorCode.forValue(code).getName());
        builder.addAugmentation(ExperimenterIdError.class, new ExperimenterIdErrorBuilder()
                .setExperimenter(new ExperimenterId(message.readUnsignedInt()))
                .build());
        if (message.readableBytes() > 0) {
            byte[] data = new byte[message.readableBytes()];
            message.readBytes(data);
            builder.setData(data);
        }
        return builder.build();
    }
}
