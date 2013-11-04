/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;

/**
 * @author michal.polkorab
 *
 */
public class GetFeaturesInputMessageFactory implements OFSerializer<GetFeaturesInput>{

    /** Code type of FeaturesRequest message */
    public static final byte MESSAGE_TYPE = 5;
    private static final int MESSAGE_LENGTH = 8;
    private static GetFeaturesInputMessageFactory instance;
    
    private GetFeaturesInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized GetFeaturesInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetFeaturesInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GetFeaturesInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
    }

    @Override
    public int computeLength(GetFeaturesInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
