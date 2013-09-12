/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ExperimenterMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterMessageFactoryTest {

    /**
     * Testing {@link ExperimenterMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        ExperimenterMessage builtByFactory = ExperimenterMessageFactory.getInstance().bufferToMessage(bb, HelloMessageFactoryTest.VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == HelloMessageFactoryTest.VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
        Assert.assertEquals(builtByFactory.getExperimenter().longValue(), 16909060L);
        Assert.assertEquals(builtByFactory.getExpType().longValue(), 16909060L);
    }

}
