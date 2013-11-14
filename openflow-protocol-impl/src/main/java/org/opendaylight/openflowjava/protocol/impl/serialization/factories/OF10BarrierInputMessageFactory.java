/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;

/**
 * Translates BarrierRequest messages
 * @author michal.polkorab
 */
public class OF10BarrierInputMessageFactory implements OFSerializer<BarrierInput> {

    private static final byte MESSAGE_TYPE = 18;
    private static final int MESSAGE_LENGTH = 8;

    private static OF10BarrierInputMessageFactory instance;
    
    private OF10BarrierInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10BarrierInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10BarrierInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, BarrierInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
    }

    @Override
    public int computeLength(BarrierInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
}
