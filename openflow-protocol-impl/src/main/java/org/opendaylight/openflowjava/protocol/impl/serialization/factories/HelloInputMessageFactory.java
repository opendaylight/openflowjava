/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;

/**
 * Translates Hello messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class HelloInputMessageFactory implements OFSerializer<HelloInput>{

    /** Code type of Hello message */
    private static final byte MESSAGE_TYPE = 0;
    /** Size of hello element header (in bytes) */
    public static final byte HELLO_ELEMENT_HEADER_SIZE = 4;

    private static void encodeElementsList(HelloInput message, ByteBuf output) {
        int[] versionBitmap;
        if (message.getElements() != null) {
            for (Elements currElement : message.getElements()) {
                output.writeShort(currElement.getType().getIntValue());
                if (currElement.getType().equals(HelloElementType.VERSIONBITMAP)) {
                    short bitmapLength = computeVersionBitmapLength(currElement);
                    output.writeShort(bitmapLength);
                    versionBitmap = ByteBufUtils.fillBitMaskFromList(currElement.getVersionBitmap());
                    for (int i = 0; i < versionBitmap.length; i++) {
                        output.writeInt(versionBitmap[i]);
                    }
                    int padding = bitmapLength - versionBitmap.length * 4 - HELLO_ELEMENT_HEADER_SIZE;
                    ByteBufUtils.padBuffer(padding , output);
                }
            } 
        }
    }

    private static short computeVersionBitmapLength(Elements element) {
        short elementlength = HELLO_ELEMENT_HEADER_SIZE;
        if (!element.getVersionBitmap().isEmpty()) {
            elementlength += ((element.getVersionBitmap().size() - 1) / Integer.SIZE + 1)
                    * (EncodeConstants.SIZE_OF_INT_IN_BYTES);
        }
        return elementlength;
    }

    @Override
    public void serialize(HelloInput object, ByteBuf outBuffer) {
        int startWriterIndex = outBuffer.writerIndex();
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        encodeElementsList(object, outBuffer);
        int endWriterIndex = outBuffer.writerIndex();
        int paddingRemainder = (endWriterIndex - startWriterIndex) % EncodeConstants.PADDING;
        if (paddingRemainder != 0) {
            ByteBufUtils.padBuffer(EncodeConstants.PADDING - paddingRemainder, outBuffer);
        }
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        // do nothing - no need for table in this factory
    }

}
