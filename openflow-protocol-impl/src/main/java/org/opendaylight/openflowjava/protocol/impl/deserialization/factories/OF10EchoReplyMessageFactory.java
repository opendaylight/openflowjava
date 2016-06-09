/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutputBuilder;

/**
 * Translates EchoReply messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF10EchoReplyMessageFactory implements OFDeserializer<EchoOutput> {

    @Override
    public EchoOutput deserialize(ByteBuf rawMessage) {
        EchoOutputBuilder builder = new EchoOutputBuilder();
        builder.setVersion((short) EncodeConstants.OF10_VERSION_ID);
        builder.setXid(rawMessage.readUnsignedInt());
        int remainingBytes = rawMessage.readableBytes();
        if (remainingBytes > 0) {
            byte[] data = new byte[rawMessage.readableBytes()];
            rawMessage.readBytes(data);
            builder.setData(data);
        }
        return builder.build();
    }

}
