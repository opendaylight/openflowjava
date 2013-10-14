/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;

/**
 * @author michal.polkorab
 *
 */
public class HelloMessageFactory implements OFDeserializer<HelloMessage> {

    private static HelloMessageFactory instance;
    
    private HelloMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static HelloMessageFactory getInstance() {
        if (instance == null) {
            instance = new HelloMessageFactory();
        }
        return instance;
    }
    
    @Override
    public HelloMessage bufferToMessage(ByteBuf rawMessage, short version) {
        HelloMessageBuilder builder = new HelloMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        if (rawMessage.readableBytes() > 0) {
            builder.setElements(readElement(rawMessage));
        }
        return builder.build();
    }
    
    private static List<Elements> readElement(ByteBuf input) {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<Elements>();
        elementsBuilder.setType(HelloElementType.forValue(input.readUnsignedShort()));
        int arrayLength = input.readableBytes()/4;
        int[] versionBitmap = new int[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            versionBitmap[i] = (int) input.readUnsignedInt();
        }
        elementsBuilder.setVersionBitmap(readVersionBitmap(versionBitmap));
        elementsList.add(elementsBuilder.build());
        return elementsList;
    }
    
    private static List<Boolean> readVersionBitmap(int[] input){
        List<Boolean> versionBitmapList = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            int mask = input[i];
            for (int j = 0; j < Integer.SIZE; j++) {
                    versionBitmapList.add((mask & (1<<j)) != 0);
            }
        }
        return versionBitmapList;
    }
}
