/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.openflow.util.BufferHelper;

/**
 * @author michal.polkorab
 *
 */
public class EchoMessageFactoryTest {

    /**
     * Testing {@link HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer(new byte[0]);
        EchoRequestMessage builtByFactory = EchoMessageFactory.getInstance().createMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }

}
