/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class BarrierInputMessageFactoryTest {

    private static final byte BARRIER_REQUEST_MESSAGE_CODE_TYPE = 20;
    
    /**
     * Testing of {@link BarrierInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void test() {
        BarrierInputBuilder bib = new BarrierInputBuilder();
        bib.setVersion(HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        bib.setXid(16909060L);
        BarrierInput bi = bib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        BarrierInputMessageFactory bimf = BarrierInputMessageFactory.getInstance();
        bimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, bi);
        
        Assert.assertTrue(out.readByte() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertTrue(out.readByte() == BARRIER_REQUEST_MESSAGE_CODE_TYPE);
        Assert.assertTrue(out.readUnsignedShort() == OFFrameDecoder.LENGTH_OF_HEADER);
        Assert.assertTrue(out.readUnsignedInt() == 16909060L);
    }

}
