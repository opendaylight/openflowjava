/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowRemovedMessageFactoryTest {

    /**
     * Testing {@link FlowRemovedMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 03 00 04 00 00 00 02"
                + " 00 00 00 05 00 01 00 03 00 01 02 03 04 05 06 07 00 01 02 03 04 05 06 07");
        FlowRemovedMessage builtByFactory = BufferHelper.decodeV13(FlowRemovedMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertTrue(builtByFactory.getCookie().longValue() == 0x0001020304050607L);
        Assert.assertTrue(builtByFactory.getPriority() == 0x03);

        //        TODO enum type!
        //        builtByFactory.getReason()
        Assert.assertEquals("Wrong tableId", new TableId((long) 4), builtByFactory.getTableId());
        Assert.assertEquals("Wrong durationSec", 0x02L, builtByFactory.getDurationSec().longValue());
        Assert.assertEquals("Wrong durationNsec", 0x05L, builtByFactory.getDurationNsec().longValue());
        Assert.assertEquals("Wrong idleTimeout", 0x01, builtByFactory.getIdleTimeout().intValue());
        Assert.assertEquals("Wrong hardTimeout", 0x03, builtByFactory.getHardTimeout().intValue());
        Assert.assertEquals("Wrong packetCount", 0x0001020304050607L, builtByFactory.getPacketCount().longValue());
        Assert.assertEquals("Wrong byteCount", 0x0001020304050607L, builtByFactory.getByteCount().longValue());
    }
    
}
