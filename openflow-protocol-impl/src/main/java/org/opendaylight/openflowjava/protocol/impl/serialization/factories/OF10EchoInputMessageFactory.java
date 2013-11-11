/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;

/**
 * @author michal.polkorab
 *
 */
public class OF10EchoInputMessageFactory implements OFSerializer<EchoInput> {

    private static final byte MESSAGE_TYPE = 2;
    private static final int MESSAGE_LENGTH = 8;

    private static OF10EchoInputMessageFactory instance;
    
    private OF10EchoInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10EchoInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10EchoInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out, EchoInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(EchoInput message) {
        int length = MESSAGE_LENGTH;
        byte[] data = message.getData();
        if (data != null) {
            length += data.length;
        }
        return length;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
}
