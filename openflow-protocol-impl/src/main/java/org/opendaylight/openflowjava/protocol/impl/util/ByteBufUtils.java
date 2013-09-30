/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.Map;
import java.util.Map.Entry;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

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
        return hexStringToBytes(hexSrc, true);
    }
    
    /**
     * Converts String into byte[]
     * @param hexSrc input String
     * @param withSpaces if there are spaces in string 
     * @return byte[] filled with input data
     */
    public static byte[] hexStringToBytes(String hexSrc, boolean withSpaces ) {
        String splitPattern = "\\s+";
        if (!withSpaces) {
            splitPattern = "(?<=\\G.{2})";
        }
        
        String[] byteChips = hexSrc.split(splitPattern);
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
    
    /**
     * Create standard OF header
     * @param factory serialization factory 
     * @param message POJO
     * @param out writing buffer
     */
    public static void writeOFHeader(OFSerializer<?> factory, OfHeader message, ByteBuf out) { 
        out.writeByte(message.getVersion());
        out.writeByte(factory.getMessageType());
        out.writeShort(factory.computeLength());
        out.writeInt(message.getXid().intValue());

    }

    /**
     * Fills the bitmask from boolean map where key is bit position
     * @param booleanMap bit to boolean mapping
     * @return bit mask
     */
    public static int fillBitMaskFromMap(Map<Integer, Boolean> booleanMap) {
        int bitmask = 0;
        
        for (Entry<Integer, Boolean> iterator : booleanMap.entrySet()) {
            if (iterator.getValue() != null && iterator.getValue().booleanValue()) {
                bitmask |= 1 << iterator.getKey();
            }
        }
        return bitmask;
    }
}
