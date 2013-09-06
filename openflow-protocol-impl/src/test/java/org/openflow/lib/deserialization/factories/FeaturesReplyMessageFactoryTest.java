/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.openflow.deserialization.factories.FeaturesReplyMessageFactory;
import org.openflow.lib.util.BufferHelper;

/**
 * @author michal.polkorab
 *
 */
public class FeaturesReplyMessageFactoryTest {

    /**
     * Testing {@link FeaturesReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        byte[] data = new byte[]{ 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                                  0x00, 0x01, 0x02, 0x03, 0x01, 0x01, 0x00, 0x00,
                                  0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03 };
        ByteBuf bb = BufferHelper.buildBuffer(data);
        GetFeaturesOutput builtByFactory = FeaturesReplyMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
        Assert.assertTrue(builtByFactory.getTables() == 1);
        Assert.assertTrue(builtByFactory.getAuxiliaryId() == 1);
        Assert.assertEquals(66051L, builtByFactory.getBuffers().longValue());
        Assert.assertEquals(66051L, builtByFactory.getCapabilities().longValue());
        Assert.assertEquals(66051L, builtByFactory.getReserved().longValue());
        Assert.assertTrue(builtByFactory.getDatapathId().longValue() == 283686952306183L);
    }

}
