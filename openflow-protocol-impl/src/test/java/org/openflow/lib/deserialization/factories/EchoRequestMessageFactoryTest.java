/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.openflow.deserialization.factories.EchoRequestMessageFactory;
import org.openflow.lib.util.BufferHelper;

/**
 * @author michal.polkorab
 *
 */
public class EchoRequestMessageFactoryTest {

    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataField() {
        ByteBuf bb = BufferHelper.buildBuffer(new byte[0]);
        EchoRequestMessage builtByFactory = EchoRequestMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }
    
    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSet() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoRequestMessage builtByFactory = EchoRequestMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }

}
