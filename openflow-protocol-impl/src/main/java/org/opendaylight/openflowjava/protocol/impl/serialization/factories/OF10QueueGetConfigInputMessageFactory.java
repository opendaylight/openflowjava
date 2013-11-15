/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;

/**
 * Translates QueueGetConfigRequest messages
 * @author michal.polkorab
 */
public class OF10QueueGetConfigInputMessageFactory implements OFSerializer<GetQueueConfigInput> {
    
    private static final byte MESSAGE_TYPE = 20;
    private static final byte PADDING_IN_GET_QUEUE_CONFIG_MESSAGE = 2;
    private static final int MESSAGE_LENGTH = 12;
    
    private static OF10QueueGetConfigInputMessageFactory instance;
    
    private OF10QueueGetConfigInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10QueueGetConfigInputMessageFactory getInstance(){
        if(instance == null){
            instance = new OF10QueueGetConfigInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, GetQueueConfigInput message){
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getPort().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_GET_QUEUE_CONFIG_MESSAGE, out);
    }

    @Override
    public int computeLength(GetQueueConfigInput message){
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
