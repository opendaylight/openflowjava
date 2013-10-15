/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;

/**
 * @author michal.polkorab
 *
 */
public class EchoInputMessageFactory implements OFSerializer<EchoInput> {

    /** Code type of EchoRequest message */
    public static final byte MESSAGE_TYPE = 2;
    private static EchoInputMessageFactory instance;
    private static final int MESSAGE_LENGTH = 8;
    
    private EchoInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized EchoInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out, EchoInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
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
