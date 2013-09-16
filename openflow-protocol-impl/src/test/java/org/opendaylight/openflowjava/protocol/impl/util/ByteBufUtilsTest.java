/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author michal.polkorab
 *
 */
public class ByteBufUtilsTest {

    private byte[] expected = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xff};
    
    /**
     * Test of {@link ByteBufUtils#hexStringToBytes(String)}
     */
    @Test
    public void hexStringToBytesTest() {
        byte[] data = ByteBufUtils.hexStringToBytes("01 02 03 04 05 ff");

        Assert.assertArrayEquals(expected, data);
    }
    
    /**
     * Test of {@link ByteBufUtils#hexStringToByteBuf(String)}
     */
    @Test
    public void hexStringToByteBufTest() {
        ByteBuf bb = ByteBufUtils.hexStringToByteBuf("01 02 03 04 05 ff");
        
        Assert.assertArrayEquals(expected, byteBufToByteArray(bb));
    }
    
    /**
     * Test of {@link ByteBufUtils#hexStringToByteBuf(String, ByteBuf)}
     */
    @Test
    public void hexStringToGivenByteBufTest() {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        ByteBufUtils.hexStringToByteBuf("01 02 03 04 05 ff", buffer);

        Assert.assertArrayEquals(expected, byteBufToByteArray(buffer));
    }
    
    private static byte[] byteBufToByteArray(ByteBuf bb) {
        byte[] result = new byte[bb.readableBytes()];
        bb.readBytes(result);
        return result;
    }

}
