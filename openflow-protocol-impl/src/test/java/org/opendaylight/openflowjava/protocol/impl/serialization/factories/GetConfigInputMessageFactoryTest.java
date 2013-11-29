/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class GetConfigInputMessageFactoryTest {

    private static final byte GET_CONFIG_REQUEST_MESSAGE_CODE_TYPE = GetConfigInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link GetConfigInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV13() throws Exception {
        GetConfigInputBuilder gcib = new GetConfigInputBuilder();
        BufferHelper.setupHeader(gcib, EncodeConstants.OF13_VERSION_ID);
        GetConfigInput gci = gcib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetConfigInputMessageFactory gcimf = GetConfigInputMessageFactory.getInstance();
        gcimf.messageToBuffer(EncodeConstants.OF13_VERSION_ID, out, gci);
        
        BufferHelper.checkHeaderV13(out, GET_CONFIG_REQUEST_MESSAGE_CODE_TYPE, 8);
    }
    
    /**
     * Testing of {@link GetConfigInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV10() throws Exception {
        GetConfigInputBuilder gcib = new GetConfigInputBuilder();
        BufferHelper.setupHeader(gcib, EncodeConstants.OF10_VERSION_ID);
        GetConfigInput gci = gcib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetConfigInputMessageFactory gcimf = GetConfigInputMessageFactory.getInstance();
        gcimf.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, gci);
        
        BufferHelper.checkHeaderV10(out, GET_CONFIG_REQUEST_MESSAGE_CODE_TYPE, 8);
    }

}
