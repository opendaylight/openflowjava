/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.util;

import io.netty.buffer.ByteBuf;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

/**
 * @author michal.polkorab
 *
 */
public class ExtBufferUtils {

    /**
     * Create OF header
     * @param msgType message code
     * @param message POJO
     * @param out writing buffer
     * @param length ofheader length
     */
    public static <E extends OfHeader> void writeOFHeader(byte msgType, E message, ByteBuf out, int length) { 
        out.writeByte(message.getVersion());
        out.writeByte(msgType);
        out.writeShort(length);
        out.writeInt(message.getXid().intValue());
    }

    /**
     * Write length standard OF header
     * @param out writing buffer
     */
    public static void updateOFHeaderLength(ByteBuf out) { 
        out.setShort(ExtConstants.OFHEADER_LENGTH_INDEX, out.readableBytes());
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
