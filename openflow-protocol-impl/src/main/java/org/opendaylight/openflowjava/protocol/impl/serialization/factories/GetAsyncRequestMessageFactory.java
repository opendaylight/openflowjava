/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;

/**
 * Translates GetAsyncRequest messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetAsyncRequestMessageFactory implements OFSerializer<GetAsyncInput> {
    private static final byte MESSAGE_TYPE = 26;
    private static final int MESSAGE_LENGTH = 8;
    private static GetAsyncRequestMessageFactory instance;
    
    private GetAsyncRequestMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized GetAsyncRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new GetAsyncRequestMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GetAsyncInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
    }
    
    @Override
    public int computeLength(GetAsyncInput message) {
        return MESSAGE_LENGTH;
    }
    
    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
