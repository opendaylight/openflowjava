/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
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
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        EchoReplyInputBuilder erib = new EchoReplyInputBuilder();
        BufferHelper.setupHeader(erib);
        EchoReplyInput eri = erib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoReplyInputMessageFactory eimf = EchoReplyInputMessageFactory.getInstance();
        eimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, eri);
        
        BufferHelper.checkHeaderV13(out, ECHO_REPLY_MESSAGE_CODE_TYPE, 8);
    }

}
