/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class GetConfigInputMessageFactoryTest {

    private static final byte GET_CONFIG_REQUEST_MESSAGE_CODE_TYPE = 7;
    
    /**
     * Testing of {@link GetConfigInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void test() {
        GetConfigInputBuilder gcib = new GetConfigInputBuilder();
        gcib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        gcib.setXid(16909060L);
        GetConfigInput gci = gcib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetConfigInputMessageFactory gcimf = GetConfigInputMessageFactory.getInstance();
        gcimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, gci);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == GET_CONFIG_REQUEST_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OFFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
