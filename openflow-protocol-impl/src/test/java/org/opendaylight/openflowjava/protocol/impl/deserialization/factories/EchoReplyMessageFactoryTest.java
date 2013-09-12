/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;

/**
 * @author michal.polkorab
 *
 */
public class EchoReplyMessageFactoryTest {

    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataField() {
        ByteBuf bb = BufferHelper.buildBuffer(new byte[0]);
        EchoOutput builtByFactory = EchoReplyMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }
    
    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSet() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoOutput builtByFactory = EchoReplyMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
        Assert.assertArrayEquals(builtByFactory.getData(), data);
    }

}
