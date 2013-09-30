/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;

/**
 * @author michal.polkorab
 *
 */
public class PacketOutInputMessageFactory implements OFSerializer<PacketOutInput>{

    /** Code type of PacketOut message */
    public static final byte MESSAGE_TYPE = 13;
    private static final int MESSAGE_LENGTH = 30;
    private static final byte PADDING_IN_PACKET_OUT_MESSAGE = 6;
    private static PacketOutInputMessageFactory instance;
    
    private PacketOutInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static PacketOutInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new PacketOutInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            PacketOutInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getBufferId().intValue());
        out.writeInt(message.getInPort().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_PACKET_OUT_MESSAGE, out);
        // TODO - finish implementation after Action serialization is done
        //out.writeShort(message.getActions().size());
        // TODO - data field is not clearly defined
        
       
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
