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
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;

/**
 * Translates QueueGetConfigRequest messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetQueueConfigInputMessageFactory implements OFSerializer<GetQueueConfigInput> {

    private static final byte MESSAGE_TYPE = 22;
    private static final byte PADDING_IN_GET_QUEUE_CONFIG_MESSAGE = 4;
    private static final int MESSAGE_LENGTH = 16;
    
    private static GetQueueConfigInputMessageFactory instance;
    
 
    private GetQueueConfigInputMessageFactory() {
        // singleton
    }
    
    
    /**
     * @return singleton factory
     */
    public static synchronized GetQueueConfigInputMessageFactory getInstance(){
        if(instance == null){
            instance = new GetQueueConfigInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, GetQueueConfigInput message){
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getPort().getValue().intValue());
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
