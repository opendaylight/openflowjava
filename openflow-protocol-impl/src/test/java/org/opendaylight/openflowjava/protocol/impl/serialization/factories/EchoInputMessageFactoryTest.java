/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoInputMessageFactoryTest {

    private static final byte ECHO_REQUEST_MESSAGE_CODE_TYPE = EchoInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link EchoInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        EchoInputBuilder eib = new EchoInputBuilder();
        BufferHelper.setupHeader(eib);
        EchoInput ei = eib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoInputMessageFactory eimf = EchoInputMessageFactory.getInstance();
        eimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, ei);
        
        BufferHelper.checkHeaderV13(out, ECHO_REQUEST_MESSAGE_CODE_TYPE, 8);
    }

}
