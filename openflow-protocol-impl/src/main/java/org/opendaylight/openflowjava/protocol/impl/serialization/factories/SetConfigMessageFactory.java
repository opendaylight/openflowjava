/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;

/**
 * @author michal.polkorab
 *
 */
public class SetConfigMessageFactory implements OFSerializer<SetConfigInput> {

    /** Code type of SetConfig message */
    public static final byte MESSAGE_TYPE = 9;
    private static final int MESSAGE_LENGTH = 8;
    private static SetConfigMessageFactory instance;
    
    private SetConfigMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static SetConfigMessageFactory getInstance() {
        if (instance == null) {
            instance = new SetConfigMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            SetConfigInput message) {
        /*out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());*/
        ByteBufUtils.writeOFHeader(instance, message, out);
        // TODO - finish implementation after enums provide needed funcionality
        out.writeShort(message.getFlags().getIntValue());
        out.writeShort(message.getMissSendLen());
    }

    @Override
    public int computeLength() {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
