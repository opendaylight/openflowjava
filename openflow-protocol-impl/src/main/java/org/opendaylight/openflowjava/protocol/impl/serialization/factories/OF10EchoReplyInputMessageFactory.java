/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;

/**
 * Translates EchoReply messages
 * @author michal.polkorab
 */
public class OF10EchoReplyInputMessageFactory implements OFSerializer<EchoReplyInput> {

    private static final byte MESSAGE_TYPE = 3;
    private static final int MESSAGE_LENGTH = 8;
    
    private static OF10EchoReplyInputMessageFactory instance;
    
    private OF10EchoReplyInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10EchoReplyInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10EchoReplyInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out,
            EchoReplyInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(EchoReplyInput message) {
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
