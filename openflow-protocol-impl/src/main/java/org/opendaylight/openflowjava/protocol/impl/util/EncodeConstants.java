/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

/**
 * Stores common constants
 * @author michal.polkorab
 */
public abstract class EncodeConstants {

    /** Default OF padding (in bytes) */
    public static final byte PADDING = EncodeConstants.SIZE_OF_LONG_IN_BYTES;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF10_VERSION_ID = 0x01;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF13_VERSION_ID = 0x04;
    
    /** Length of mac address */
    public static final byte MAC_ADDRESS_LENGTH = 6;
    /** Number of groups in ipv4 address */
    public static final byte GROUPS_IN_IPV4_ADDRESS = 4;
    /** Number of groups in ipv6 address */
    public static final byte GROUPS_IN_IPV6_ADDRESS = 8;
    /** Length of ipv6 address in bytes */
    public static final byte SIZE_OF_IPV6_ADDRESS_IN_BYTES = (8 * Short.SIZE) / Byte.SIZE;
    
    /** Length of long in bytes */
    public static final byte SIZE_OF_LONG_IN_BYTES = Long.SIZE / Byte.SIZE;
    /** Length of int in bytes */
    public static final byte SIZE_OF_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    /** Length of short in bytes */
    public static final byte SIZE_OF_SHORT_IN_BYTES = Short.SIZE / Byte.SIZE;
    /** Length of byte in bytes */
    public static final byte SIZE_OF_BYTE_IN_BYTES = Byte.SIZE / Byte.SIZE;

}
