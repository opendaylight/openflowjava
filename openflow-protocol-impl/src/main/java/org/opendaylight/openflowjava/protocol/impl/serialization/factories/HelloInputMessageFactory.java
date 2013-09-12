/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactory implements OFSerializer<HelloInput>{

    /** Code type of HelloMessage */
    public static final byte MESSAGE_TYPE = 0;
    private static HelloInputMessageFactory instance;
    
    private HelloInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static HelloInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new HelloInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out, HelloInput message) {
        out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());
        // TODO - fill list of elements into ByteBuf, check length too
    }
    
    

    
    
}
