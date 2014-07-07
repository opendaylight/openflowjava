/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.experimenter._case.MultipartReplyExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.experimenter._case.MultipartReplyExperimenterBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10StatsReplyVendorDeserializer implements OFDeserializer<MultipartReplyExperimenter> {

    @Override
    public MultipartReplyExperimenter deserialize(ByteBuf message) {
        MultipartReplyExperimenterBuilder builder = new MultipartReplyExperimenterBuilder();
        builder.setExperimenter(message.readUnsignedInt());
        if (message.readableBytes() > 0) {
            byte[] data = new byte[message.readableBytes()];
            message.readBytes(data);
            builder.setData(data);
        }
        return builder.build();
    }
}