/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

/** Class for common operations on ByteBuf
 *
 * @author michal.polkorab
 */
public abstract class ByteBufUtils {

    /**
     * Converts ByteBuf into String
     * @param bb input ByteBuf
     * @return String
     */
    public static String byteBufToHexString(ByteBuf bb) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bb.readableBytes(); i++) {
            short b = bb.getUnsignedByte(i);
            sb.append(String.format("%02x ", b));
        }
        return sb.toString();
    }
    
    /**
     * Converts String into byte[]
     * @param hexSrc input String
     * @return byte[] filled with input data
     */
    public static byte[] hexStringToBytes(String hexSrc) {
        String[] byteChips = hexSrc.split("\\s+");
        byte[] result = new byte[byteChips.length];
        for (int i = 0; i < byteChips.length; i++) {
            result[i] = (byte) Short.parseShort(byteChips[i], 16);
        }
        return result;
    }
    
    /**
     * Creates ByteBuf filled with specified data
     * @param hexSrc input String of bytes in hex format
     * @return ByteBuf with specified hexString converted
     */
    public static ByteBuf hexStringToByteBuf(String hexSrc) {
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        hexStringToByteBuf(hexSrc, out);
        return out;
    }
    
    /**
     * Creates ByteBuf filled with specified data
     * @param hexSrc input String of bytes in hex format
     * @param out ByteBuf with specified hexString converted
     */
    public static void hexStringToByteBuf(String hexSrc, ByteBuf out) {
        out.writeBytes(hexStringToBytes(hexSrc));
    }
    
    /**
     * Fills specified ByteBuf with 0 (zeros) of desired length, used for padding
     * @param length
     * @param out ByteBuf to be padded
     */
    public static void padBuffer(int length, ByteBuf out) {
        for (int i = 0; i < length; i++) {
            out.writeByte(0);
        }
    }
}
