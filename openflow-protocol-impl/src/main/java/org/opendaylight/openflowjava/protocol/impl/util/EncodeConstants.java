/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.util;

/**
 * Stores common constants
 * @author michal.polkorab
 */
public abstract class EncodeConstants {

    /** Default OF padding (in bytes) */
    public static final byte PADDING = Long.SIZE / Byte.SIZE;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF10_VERSION_ID = 0x01;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF13_VERSION_ID = 0x04;

}
