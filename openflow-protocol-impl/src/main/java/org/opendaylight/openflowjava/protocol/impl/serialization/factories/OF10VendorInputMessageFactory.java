/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * Translates Vendor messages
 * @author michal.polkorab
 */
public class OF10VendorInputMessageFactory implements OFSerializer<ExperimenterInput> {

    private static final byte MESSAGE_TYPE = 4;
    private static final byte MESSAGE_LENGTH = 8;
    
    private static OF10VendorInputMessageFactory instance;
    
    private OF10VendorInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10VendorInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10VendorInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            ExperimenterInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getExperimenter().intValue());
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(ExperimenterInput message) {
        int length = MESSAGE_LENGTH + Integer.SIZE / Byte.SIZE;
        byte[] data = message.getData();
        if (data != null) {
            length += data.length;
        }
        return length;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
}
