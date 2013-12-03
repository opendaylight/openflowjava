/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10HelloInputMessageFactoryTest {

    /**
     * Testing of {@link OF10HelloInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testWithoutElementsSet() throws Exception {
        HelloInputBuilder hib = new HelloInputBuilder();
        BufferHelper.setupHeader(hib, EncodeConstants.OF10_VERSION_ID);
        HelloInput hi = hib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10HelloInputMessageFactory himf = OF10HelloInputMessageFactory.getInstance();
        himf.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, hi);
        
        BufferHelper.checkHeaderV10(out, (byte) 0, 8);
    }

}
