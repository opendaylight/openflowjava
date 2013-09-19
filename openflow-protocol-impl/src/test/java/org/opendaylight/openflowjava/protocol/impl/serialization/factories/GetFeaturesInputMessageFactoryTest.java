/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class GetFeaturesInputMessageFactoryTest {

    private static final byte FEATURES_REQUEST_MESSAGE_CODE_TYPE = GetFeaturesInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link GetFeaturesInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        GetFeaturesInputBuilder gfib = new GetFeaturesInputBuilder();
        BufferHelper.setupHeader(gfib);
        GetFeaturesInput gfi = gfib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetFeaturesInputMessageFactory gfimf = GetFeaturesInputMessageFactory.getInstance();
        gfimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, gfi);
        
        BufferHelper.checkHeaderV13(out, FEATURES_REQUEST_MESSAGE_CODE_TYPE, 8);
    }

}
