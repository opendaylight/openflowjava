/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.OF10ActionsSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;

/**
 * @author michal.polkorab
 *
 */
public class OF10PacketOutInputMessageFactory implements OFSerializer<PacketOutInput> {

    private static final byte MESSAGE_TYPE = 13;
    private static final int MESSAGE_LENGTH = 16;
    
    private static OF10PacketOutInputMessageFactory instance;
    
    private OF10PacketOutInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10PacketOutInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10PacketOutInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            PacketOutInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getBufferId().intValue());
        out.writeShort(message.getInPort().getValue().intValue());
        out.writeShort(OF10ActionsSerializer.computeActionsLength(message.getActionsList()));
        OF10ActionsSerializer.encodeActionsV10(out, message.getActionsList());
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(PacketOutInput message) {
        int length = MESSAGE_LENGTH + OF10ActionsSerializer.computeActionsLength(message.getActionsList());
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
