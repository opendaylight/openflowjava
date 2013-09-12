/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoInputMessageFactoryTest {

    private static final byte ECHO_REQUEST_MESSAGE_CODE_TYPE = 2;
    
    /**
     * Testing of {@link EchoInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void test() {
        EchoInputBuilder eib = new EchoInputBuilder();
        eib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        eib.setXid(16909060L);
        EchoInput ei = eib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoInputMessageFactory eimf = EchoInputMessageFactory.getInstance();
        eimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, ei);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == ECHO_REQUEST_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OFFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
