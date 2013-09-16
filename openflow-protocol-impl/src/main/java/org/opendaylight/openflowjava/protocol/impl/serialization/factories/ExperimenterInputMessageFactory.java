/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterInputMessageFactory implements OFSerializer<ExperimenterInput>{

    /** Code type of Experimenter message */
    public static final byte MESSAGE_TYPE = 4;
    private static ExperimenterInputMessageFactory instance;
    
    private ExperimenterInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static ExperimenterInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new ExperimenterInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            ExperimenterInput message) {
        out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());
        out.writeInt(message.getExperimenter().intValue());
        out.writeInt(message.getExpType().intValue());
    }

}
