/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.api.util;

/**
 * @author michal.polkorab
 *
 */
public abstract class BinContent {

    /**
     * @param value
     * @return int part wrapped in long
     */
    public static long intToUnsignedLong(int value) {
        return value & 0x00000000ffffffffL;
    }
    
    /**
     * @param value
     * @return long cut into int
     */
    public static int longToSignedInt(long value) {
        return (int) value;
    }
}
