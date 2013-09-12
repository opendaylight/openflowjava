/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class GetFeaturesInputMessageFactoryTest {

    private static final byte FEATURES_REQUEST_MESSAGE_CODE_TYPE = 5;
    
    /**
     * Testing of {@link GetFeaturesInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void test() {
        GetFeaturesInputBuilder gfib = new GetFeaturesInputBuilder();
        gfib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        gfib.setXid(16909060L);
        GetFeaturesInput gfi = gfib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetFeaturesInputMessageFactory gfimf = GetFeaturesInputMessageFactory.getInstance();
        gfimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, gfi);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == FEATURES_REQUEST_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OFFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
