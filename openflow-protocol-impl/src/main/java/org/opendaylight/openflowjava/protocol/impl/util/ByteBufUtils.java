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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedBytes;

/** Class for common operations on ByteBuf
 * @author michal.polkorab
 * @author timotej.kubas
 */
public abstract class ByteBufUtils {
    public static final Splitter DOT_SPLITTER = Splitter.on('.');
    public static final Joiner DOT_JOINER = Joiner.on(".");
    public static final Splitter COLON_SPLITTER = Splitter.on(':');
    public static final Joiner COLON_JOINER = Joiner.on(":");
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    /**
     * Converts ByteBuf into String
     * @param bb input ByteBuf
     * @return String
     */
    public static String byteBufToHexString(final ByteBuf bb) {
        StringBuffer sb = new StringBuffer();
        for (int i = bb.readerIndex(); i < (bb.readerIndex() + bb.readableBytes()); i++) {
            sb.append(String.format(" %02x", bb.getUnsignedByte(i)));
        }
        return sb.toString().trim();
    }

    /**
     * Converts String into byte[]
     * @param hexSrc input String
     * @return byte[] filled with input data
     */
    public static byte[] hexStringToBytes(final String hexSrc) {
        return hexStringToBytes(hexSrc, true);
    }

    /**
     * Converts String into byte[]
     * @param hexSrc input String
     * @param withSpaces if there are spaces in string
     * @return byte[] filled with input data
     */
    public static byte[] hexStringToBytes(final String hexSrc, final boolean withSpaces ) {
        String splitPattern = "\\s+";
        if (!withSpaces) {
            splitPattern = "(?<=\\G.{2})";
        }
        Iterable<String> tmp = Splitter.onPattern(splitPattern)
                .omitEmptyStrings().split(hexSrc);
        ArrayList<String> byteChips = Lists.newArrayList(tmp);
        byte[] result = new byte[byteChips.size()];
        int i = 0;
        for (String chip : byteChips) {
            result[i] = (byte) Short.parseShort(chip, 16);
            i++;
        }
        return result;
    }

    /**
     * Creates ByteBuf filled with specified data
     * @param hexSrc input String of bytes in hex format
     * @return ByteBuf with specified hexString converted
     */
    public static ByteBuf hexStringToByteBuf(final String hexSrc) {
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        hexStringToByteBuf(hexSrc, out);
        return out;
    }

    /**
     * Creates ByteBuf filled with specified data
     * @param hexSrc input String of bytes in hex format
     * @param out ByteBuf with specified hexString converted
     */
    public static void hexStringToByteBuf(final String hexSrc, final ByteBuf out) {
        out.writeBytes(hexStringToBytes(hexSrc));
    }

    /**
     * Fills specified ByteBuf with 0 (zeros) of desired length, used for padding
     * @param length
     * @param out ByteBuf to be padded
     * @deprecated Use {@link ByteBuf#writeZero(int)} directly.
     */
    @Deprecated
    public static void padBuffer(final int length, final ByteBuf out) {
        out.writeZero(length);
    }

    /**
     * Create standard OF header
     * @param msgType message code
     * @param message POJO
     * @param out writing buffer
     * @param length ofheader length
     */
    public static <E extends OfHeader> void writeOFHeader(final byte msgType, final E message, final ByteBuf out, final int length) {
        out.writeByte(message.getVersion());
        out.writeByte(msgType);
        out.writeShort(length);
        out.writeInt(message.getXid().intValue());
    }

    /**
     * Write length standard OF header
     * @param out writing buffer
     */
    public static void updateOFHeaderLength(final ByteBuf out) {
        out.setShort(EncodeConstants.OFHEADER_LENGTH_INDEX, out.readableBytes());
    }

    /**
     * Fills the bitmask from boolean map where key is bit position
     * @param booleanMap bit to boolean mapping
     * @return bit mask
     */
    public static int fillBitMaskFromMap(final Map<Integer, Boolean> booleanMap) {
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
    public static int[] fillBitMaskFromList(final List<Boolean> booleanList) {
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
    public static String bytesToHexString(final byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte element : array) {
            sb.append(String.format(" %02x", element));
        }
        return sb.toString().trim();
    }

    /**
     * Converts macAddress to byte array
     * @param macAddress
     * @return byte representation of mac address
     * @see {@link MacAddress}
     */
    public static byte[] macAddressToBytes(final String macAddress) {
        Iterable<String> addressGroups = COLON_SPLITTER.split(macAddress);
        byte[] result = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        int i = 0;
        for (String group : addressGroups) {
            result[i] = (byte) Short.parseShort(group, 16);
            i++;
        }
        return result;
    }

    private static final void appendByte(final StringBuilder sb, final byte b) {
        final int v = UnsignedBytes.toInt(b);
        sb.append(HEX_CHARS[v >> 4]);
        sb.append(HEX_CHARS[v & 15]);
    }

    /**
     * Converts a MAC address represented in bytes to String
     * @param address
     * @return String representation of a MAC address
     * @see {@link MacAddress}
     */
    public static String macAddressToString(final byte[] address) {
        Preconditions.checkArgument(address.length == EncodeConstants.MAC_ADDRESS_LENGTH);

        final StringBuilder sb = new StringBuilder(17);

        appendByte(sb, address[0]);
        for (int i = 1; i < EncodeConstants.MAC_ADDRESS_LENGTH; i++) {
            sb.append(':');
            appendByte(sb, address[i]);
        }

        return sb.toString();
    }

    /**
     * Reads and parses null-terminated string from ByteBuf
     * @param rawMessage
     * @param length maximal length of String
     * @return String with name of port
     */
    public static String decodeNullTerminatedString(final ByteBuf rawMessage, final int length) {
        byte[] name = new byte[length];
        rawMessage.readBytes(name);
        return new String(name).trim();
    }

}
