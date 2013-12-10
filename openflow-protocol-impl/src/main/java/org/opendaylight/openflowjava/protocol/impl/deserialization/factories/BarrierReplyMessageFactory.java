/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutputBuilder;

/**
 * Translates BarrierReply messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class BarrierReplyMessageFactory implements
        OFDeserializer<BarrierOutput> {

    private static BarrierReplyMessageFactory instance;

    private BarrierReplyMessageFactory() {
        // do nothing, just singleton
    }

    /**
     * @return singleton factory
     */
    public static synchronized BarrierReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new BarrierReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public BarrierOutput bufferToMessage(ByteBuf rawMessage, short version) {
        BarrierOutputBuilder builder = new BarrierOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        return builder.build();
    }

}
