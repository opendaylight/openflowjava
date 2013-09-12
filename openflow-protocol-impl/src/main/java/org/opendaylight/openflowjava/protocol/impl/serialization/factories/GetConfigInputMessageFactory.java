/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;

/**
 * @author michal.polkorab
 *
 */
public class GetConfigInputMessageFactory implements OFSerializer<GetConfigInput> {

    /** Code type of GetConfigRequest message */
    public static final byte MESSAGE_TYPE = 7;
    private static GetConfigInputMessageFactory instance;
    
    private GetConfigInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static GetConfigInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetConfigInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GetConfigInput message) {
        out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());
        
    }

}
