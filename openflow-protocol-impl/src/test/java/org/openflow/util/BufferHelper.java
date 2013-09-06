/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.util;

import org.openflow.core.OFFrameDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * @author michal.polkorab
 *
 */
public abstract class BufferHelper {

    /**
     * 
     */
    private static final byte[] XID = new byte[]{0x01, 0x02, 0x03, 0x04};

    /**
     * @param payload
     * @return ByteBuf filled with OpenFlow protocol message without first 2 bytes
     */
    public static ByteBuf buildBuffer(byte[] payload) {
        ByteBuf bb = UnpooledByteBufAllocator.DEFAULT.buffer();
        bb.writeShort(OFFrameDecoder.LENGTH_OF_HEADER + payload.length);
        bb.writeBytes(XID);
        bb.writeBytes(payload);
        return bb;
    }

}
