/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactoryTest {

    private static final byte HELLO_MESSAGE_CODE_TYPE = HelloInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link HelloInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutElementsSet() throws Exception {
        HelloInputBuilder hib = new HelloInputBuilder();
        BufferHelper.setupHeader(hib);
        HelloInput hi = hib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory himf = HelloInputMessageFactory.getInstance();
        himf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, hi);
        
        BufferHelper.checkHeaderV13(out, HELLO_MESSAGE_CODE_TYPE, 8);
    }
    
    /**
     * Testing of {@link HelloInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithElementsSet() throws Exception {
        HelloInputBuilder builder = new HelloInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setElements(createElement());
        HelloInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory factory = HelloInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, HELLO_MESSAGE_CODE_TYPE, 8);
        Elements element = readElement(out).get(0);
        Assert.assertEquals("Wrong elements ", createElement().get(0).getType(), element.getType());
        Assert.assertArrayEquals("Wrong elements ", createElement().get(0).getVersionBitmap().toArray(), element.getVersionBitmap().toArray());
    }
    
    private static List<Elements> createElement() {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<Elements>();
        List<Boolean> booleanList = new ArrayList<Boolean>();

        for (int i = 0; i < 64; i++) {
            booleanList.add(true);
        }

        elementsBuilder.setType(HelloElementType.forValue(1));
        elementsBuilder.setVersionBitmap(booleanList);
        elementsList.add(elementsBuilder.build());
        
        return elementsList;
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
        System.out.println("input.length: "+input.length);
        for (int i = 0; i < input.length; i++) {
            int mask = input[i];
            for (int j = 0; j < Integer.SIZE; j++) {
                    versionBitmapList.add((mask & (1<<j)) != 0);
            }
        }
        return versionBitmapList;
    }

}
