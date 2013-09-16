/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoReplyInputMessageFactoryTest {

    private static final byte ECHO_REPLY_MESSAGE_CODE_TYPE = 3;
    
    /**
     * Testing of {@link EchoReplyInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void test() {
        EchoReplyInputBuilder erib = new EchoReplyInputBuilder();
        erib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        erib.setXid(16909060L);
        EchoReplyInput eri = erib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoReplyInputMessageFactory eimf = EchoReplyInputMessageFactory.getInstance();
        eimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, eri);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == ECHO_REPLY_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OFFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
