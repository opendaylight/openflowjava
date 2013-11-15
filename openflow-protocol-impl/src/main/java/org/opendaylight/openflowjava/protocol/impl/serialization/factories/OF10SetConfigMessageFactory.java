/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;

/**
 * Translates SetConfig messages
 * @author michal.polkorab
 */
public class OF10SetConfigMessageFactory implements OFSerializer<SetConfigInput> {

    private static final byte MESSAGE_TYPE = 9;
    private static final int MESSAGE_LENGTH = 12;
    
    private static OF10SetConfigMessageFactory instance;
    
    private OF10SetConfigMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10SetConfigMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10SetConfigMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            SetConfigInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getFlags().getIntValue());
        out.writeShort(message.getMissSendLen());
    }

    @Override
    public int computeLength(SetConfigInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
}
