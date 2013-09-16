/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class HelloInputMessageFactoryTest {

    private static final byte HELLO_MESSAGE_CODE_TYPE = 0;
    
    /**
     * Testing of {@link HelloInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testWithoutElementsSet() {
        HelloInputBuilder hib = new HelloInputBuilder();
        hib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        hib.setXid(16909060L);
        HelloInput hi = hib.build();
        
        ByteBuf o = UnpooledByteBufAllocator.DEFAULT.buffer();
        o.writeBytes(ByteBufUtils.hexStringToBytes("01 02 03 04"));
        System.out.println(ByteBufUtils.byteBufToHexString(o));
        System.out.println("trala");
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory himf = HelloInputMessageFactory.getInstance();
        himf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, hi);
        
        Assert.assertTrue(BufferHelper.testCorrectHeaderInByteBuf(out, HELLO_MESSAGE_CODE_TYPE));
    }

}
