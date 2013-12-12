/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

import com.google.common.base.Joiner;

/** Class for common operations on ByteBuf
 * @author michal.polkorab
 * @author timotej.kubas
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
            sb.append(String.format(" %02x", bb.getUnsignedByte(i)));
        }
        return sb.toString().trim();
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
    public static <E extends OfHeader> void writeOFHeader(OFSerializer<E> factory, E message, ByteBuf out) { 
        out.writeByte(message.getVersion());
        out.writeByte(factory.getMessageType());
        out.writeShort(factory.computeLength(message));
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
    
    /**
     * Fills the bitmask from boolean list where key is bit position
     * @param booleanList bit to boolean mapping
     * @return bit mask
     */
    public static int[] fillBitMaskFromList(List<Boolean> booleanList) {
        int[] bitmask;
        int index = 0;
        int arrayIndex = 0;
        if ((booleanList.size() % Integer.SIZE) != 0) {
            bitmask = new int[booleanList.size() / Integer.SIZE + 1];
        } else {
            bitmask = new int[booleanList.size() / Integer.SIZE];
        }
        for (Boolean currElement : booleanList) {
            if (currElement != null && currElement.booleanValue()) {
                bitmask[arrayIndex] |= 1 << index;
            }
            index++;
            arrayIndex = index / Integer.SIZE;
        }
        return bitmask;
    }

    /**
     * Converts byte array into String
     * @param array input byte array
     * @return String
     */
    public static String bytesToHexString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(String.format(" %02x", array[i]));
        }
        return sb.toString().trim();
    }
    
    /**
     * Converts macAddress to byte array
     * @param macAddress
     * @return byte representation of mac address
     * @see {@link MacAddress}
     */
    public static byte[] macAddressToBytes(String macAddress) {
        String[] sequences = macAddress.split(":");
        byte[] result = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        for (int i = 0; i < sequences.length; i++) {
             result[i] = (byte) Short.parseShort(sequences[i], 16);
        }
        return result;
    }
    
    /**
     * Converts mac address represented in bytes to String
     * @param address
     * @return String representation of mac address
     * @see {@link MacAddress}
     */
    public static String macAddressToString(byte[] address) {
        List<String> groups = new ArrayList<>();
        for(int i=0; i < EncodeConstants.MAC_ADDRESS_LENGTH; i++){
            groups.add(String.format("%02X", address[i]));
        }
        Joiner joiner = Joiner.on(":");
        return joiner.join(groups); 
    }
    
    /**
     * Reads and parses port name from ByteBuf
     * @param rawMessage
     * @param length maximal length of String
     * @return String with name of port
     */
    public static String decodeNullTerminatedString(ByteBuf rawMessage, int length) {
        byte[] name = new byte[EncodeConstants.MAX_PORT_NAME_LENGTH];
        rawMessage.readBytes(name);
        int index = 0;
        for (int i = 0; i < name.length; i++) {
            if (name[i] != 0) {
                index++;
            } else {
                break;
            }
        }
        byte[] correctName = Arrays.copyOf(name, index);
        return new String(correctName);
    }
}
