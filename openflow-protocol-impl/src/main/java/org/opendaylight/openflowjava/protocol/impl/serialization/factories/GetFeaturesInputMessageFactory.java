/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;

/**
 * @author michal.polkorab
 *
 */
public class GetFeaturesInputMessageFactory implements OFSerializer<GetFeaturesInput>{

    /** Code type of FeaturesRequest message */
    public static final byte MESSAGE_TYPE = 5;
    private static GetFeaturesInputMessageFactory instance;
    
    private GetFeaturesInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static GetFeaturesInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetFeaturesInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GetFeaturesInput message) {
        out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());
    }

}
