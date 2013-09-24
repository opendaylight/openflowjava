/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterInputMessageFactory implements OFSerializer<ExperimenterInput>{

    /** Code type of Experimenter message */
    public static final byte MESSAGE_TYPE = 4;
    private static final int MESSAGE_LENGTH = 16;
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
        
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getExperimenter().intValue());
        out.writeInt(message.getExpType().intValue());
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
