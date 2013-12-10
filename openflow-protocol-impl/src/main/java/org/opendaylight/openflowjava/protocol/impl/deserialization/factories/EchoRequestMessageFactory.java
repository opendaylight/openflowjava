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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessageBuilder;

/**
 * Translates EchoRequest messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class EchoRequestMessageFactory implements OFDeserializer<EchoRequestMessage>{

    private static EchoRequestMessageFactory instance;

    private EchoRequestMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized EchoRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoRequestMessageFactory();
        }
        return instance;
    }

    @Override
    public EchoRequestMessage bufferToMessage(ByteBuf rawMessage, short version) {
        EchoRequestMessageBuilder builder = new EchoRequestMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return builder.build();
    }
}
