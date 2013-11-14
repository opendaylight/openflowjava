/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;

/**
 * Translates GetConfigRequest messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class GetConfigInputMessageFactory implements OFSerializer<GetConfigInput> {

    /** Code type of GetConfigRequest message */
    public static final byte MESSAGE_TYPE = 7;
    private static final int MESSAGE_LENGTH = 8;
    private static GetConfigInputMessageFactory instance;
    
    private GetConfigInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized GetConfigInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetConfigInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GetConfigInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
    }

    @Override
    public int computeLength(GetConfigInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
