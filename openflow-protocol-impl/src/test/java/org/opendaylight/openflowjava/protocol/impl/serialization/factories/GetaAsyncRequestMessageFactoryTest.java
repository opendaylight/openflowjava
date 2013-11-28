/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetaAsyncRequestMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 26;
    private static final int MESSAGE_LENGTH = 8;
    
    /**
     * Testing of {@link GetAsyncRequestMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testGetAsyncReques() throws Exception {
        GetAsyncInputBuilder builder = new GetAsyncInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        GetAsyncInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetAsyncRequestMessageFactory factory = GetAsyncRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
    }
}
