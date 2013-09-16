/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;

/**
 * @author michal.polkorab
 * 
 */
public abstract class BufferHelper {

    /**
     * 
     */
    private static final byte[] XID = new byte[] { 0x01, 0x02, 0x03, 0x04 };

    /**
     * @param payload
     * @return ByteBuf filled with OpenFlow protocol message without first 2
     *         bytes
     */
    public static ByteBuf buildBuffer(byte[] payload) {
        ByteBuf bb = UnpooledByteBufAllocator.DEFAULT.buffer();
        bb.writeBytes(XID);
        bb.writeBytes(payload);
        return bb;
    }

    /**
     * @param input
     *            ByteBuf to be checked for correct OpenFlow Protocol header
     * @param msgType
     *            type of received message
     * @return true if ByteBuf contains
     */
    public static boolean testCorrectHeaderInByteBuf(ByteBuf input, byte msgType) {
        if (input.readByte() != HelloMessageFactoryTest.VERSION_YET_SUPPORTED) {
            return false;
        }
        if (input.readByte() != msgType) {
            return false;
        }
        if (input.readUnsignedShort() != OFFrameDecoder.LENGTH_OF_HEADER) {
            return false;
        }
        if (input.readUnsignedInt() != 16909060L) {
            return false;
        }
        return true;
    }
}
