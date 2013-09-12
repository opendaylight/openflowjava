/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;

/**
 * @author michal.polkorab
 *
 */
public class BarrierReplyMessageFactoryTest {

    /**
     * Testing of {@link BarrierReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer(new byte[0]);
        BarrierOutput builtByFactory = BarrierReplyMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }
}
