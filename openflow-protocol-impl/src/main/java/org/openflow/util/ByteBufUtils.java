/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openflow.util;

import io.netty.buffer.ByteBuf;

/**
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
}
