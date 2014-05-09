/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.util;

/**
 * @author michal.polkorab
 *
 */
public class ExtConstants {

    /** Default OF padding (in bytes) */
    public static final byte PADDING = 8;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF10_VERSION_ID = 0x01;
    /** OpenFlow v1.0 wire protocol number */
    public static final byte OF13_VERSION_ID = 0x04;
    /** Index of length in Openflow header */
    public static final int OFHEADER_LENGTH_INDEX = 2;
    /** Zero length - used when the length is updated later */
    public static final int EMPTY_LENGTH = 0;

    /** Length of long in bytes */
    public static final byte SIZE_OF_LONG_IN_BYTES = Long.SIZE / Byte.SIZE;
    /** Length of int in bytes */
    public static final byte SIZE_OF_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    /** Length of short in bytes */
    public static final byte SIZE_OF_SHORT_IN_BYTES = Short.SIZE / Byte.SIZE;
    /** Length of byte in bytes */
    public static final byte SIZE_OF_BYTE_IN_BYTES = Byte.SIZE / Byte.SIZE;
    /** Length of 3 bytes */
    public static final byte SIZE_OF_3_BYTES = 3;

    /** Common experimenter value */
    public static final int EXPERIMENTER_VALUE = 0xFFFF;
    /** OF v1.3 lenght of experimenter_ids - see Multipart TableFeatures (properties) message */
    public static final byte EXPERIMENTER_IDS_LENGTH = 8;
}
