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
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloInputMessageFactoryTest.class);
    
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
        
        BufferHelper.checkHeaderV13(out, himf.getMessageType(), himf.computeLength(hi));
    }
    
    /**
     * Testing of {@link HelloInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithElementsSet() throws Exception {
        HelloInputBuilder builder = new HelloInputBuilder();
        BufferHelper.setupHeader(builder);
        List<Elements> expectedElement = createElement();
        builder.setElements(expectedElement);
        HelloInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory factory = HelloInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        LOGGER.debug("bytebuf: " + ByteBufUtils.byteBufToHexString(out));
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Elements element = readElement(out).get(0);
        Assert.assertEquals("Wrong element type", expectedElement.get(0).getType(), element.getType());
        LOGGER.debug(expectedElement.get(0).getVersionBitmap().toString());
        LOGGER.debug(element.getVersionBitmap().toString());
        Assert.assertArrayEquals("Wrong element bitmap", expectedElement.get(0).getVersionBitmap().toArray(), element.getVersionBitmap().toArray());
    }
    
    private static List<Elements> createElement() {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<>();
        List<Boolean> booleanList = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            booleanList.add(true);
        }

        elementsBuilder.setType(HelloElementType.forValue(1));
        elementsBuilder.setVersionBitmap(booleanList);
        elementsList.add(elementsBuilder.build());
        
        return elementsList;
    }
    
    private static List<Elements> readElement(ByteBuf input) {
        List<Elements> elementsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            ElementsBuilder elementsBuilder = new ElementsBuilder();
            int type = input.readUnsignedShort();
            int elementLength = input.readUnsignedShort();
            if (type == HelloElementType.VERSIONBITMAP.getIntValue()) {
                elementsBuilder.setType(HelloElementType.forValue(type));
                int[] versionBitmap = new int[(elementLength - HelloInputMessageFactory.HELLO_ELEMENT_HEADER_SIZE) / 4];
                for (int i = 0; i < versionBitmap.length; i++) {
                    versionBitmap[i] = (int) input.readUnsignedInt();
                }
                elementsBuilder.setVersionBitmap(readVersionBitmap(versionBitmap));
                int paddingRemainder = elementLength % EncodeConstants.PADDING;
                if (paddingRemainder != 0) {
                    input.readBytes(EncodeConstants.PADDING - paddingRemainder);
                }
            }
            elementsList.add(elementsBuilder.build());
        }
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
