/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;

/**
 * Translates PacketOut messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PacketOutInputMessageFactory implements OFSerializer<PacketOutInput>{

    /** Code type of PacketOut message */
    public static final byte MESSAGE_TYPE = 13;
    private static final int MESSAGE_LENGTH = 24;
    private static final byte PADDING_IN_PACKET_OUT_MESSAGE = 6;
    private static PacketOutInputMessageFactory instance;
    
    private PacketOutInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized PacketOutInputMessageFactory getInstance() {
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
        out.writeShort(ActionsSerializer.computeLengthOfActions(message.getAction()));
        ByteBufUtils.padBuffer(PADDING_IN_PACKET_OUT_MESSAGE, out);
        ActionsSerializer.encodeActions(message.getAction(), out);
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(PacketOutInput message) {
        int length = MESSAGE_LENGTH;
        length += ActionsSerializer.computeLengthOfActions(message.getAction());
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
