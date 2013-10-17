/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactory implements OFSerializer<HelloInput>{

    /** Code type of Hello message */
    private static final byte MESSAGE_TYPE = 0;
    private static int MESSAGE_LENGTH = 0;
    private static HelloInputMessageFactory instance;
    
    private HelloInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized HelloInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new HelloInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out, HelloInput message) {
        computeElementsLength(message.getElements());
        ByteBufUtils.writeOFHeader(instance, message, out);
        encodeElementsList(message.getElements(), out);
    }

    @Override
    public int computeLength() {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
    private static int computeElementsLength(List<Elements> elements) {
        int versionBitmapSize = 0;
        final int ofHeaderSize = 8;
        int typeSize = 0;
        
        if (elements != null) {
            typeSize = 2;
            versionBitmapSize = elements.get(0).getVersionBitmap().size()/Byte.SIZE;
            } 
        MESSAGE_LENGTH = ofHeaderSize + versionBitmapSize + typeSize;
        return MESSAGE_LENGTH;
    }
 
    private static void encodeElementsList(List<Elements> elements, ByteBuf output) {
        int[] versionBitmap;
        int arraySize = 0;
        if (elements != null) {
            for (Iterator<Elements> iterator = elements.iterator(); iterator.hasNext();) {
                Elements currElement = iterator.next();
                output.writeShort(currElement.getType().getIntValue());
                versionBitmap = ByteBufUtils.fillBitMaskFromList(currElement.getVersionBitmap());
                arraySize = (versionBitmap.length/Integer.SIZE);
                for (int i = 0; i < arraySize; i++) {
                    output.writeInt(versionBitmap[i]);
                }
            } 
        }
    }
}
