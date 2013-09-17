/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.List;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;

import com.google.common.collect.Lists;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactoryTest {

    private static final byte HELLO_MESSAGE_CODE_TYPE = 0;
    
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
        HelloInputBuilder hib = new HelloInputBuilder();
        BufferHelper.setupHeader(hib);
        ElementsBuilder eb = new ElementsBuilder();
        eb.setType(HelloElementType.VERSIONBITMAP);
        eb.setData(new byte[]{0x01, 0x02, 0x42, 0x03});
        List<Elements> elementList = Lists.newArrayList(eb.build());
        hib.setElements(elementList);
        HelloInput hi = hib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory himf = HelloInputMessageFactory.getInstance();
        himf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, hi);
        
        BufferHelper.checkHeaderV13(out, HELLO_MESSAGE_CODE_TYPE, 8);
        // TODO - element list
    }

}
