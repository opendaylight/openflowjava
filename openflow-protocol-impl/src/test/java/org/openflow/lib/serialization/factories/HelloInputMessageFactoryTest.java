/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.openflow.lib.OfFrameDecoder;
import org.openflow.lib.deserialization.factories.HelloMessageFactoryTest;

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
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        HelloInputMessageFactory himf = HelloInputMessageFactory.getInstance();
        himf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, hi);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == HELLO_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OfFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
