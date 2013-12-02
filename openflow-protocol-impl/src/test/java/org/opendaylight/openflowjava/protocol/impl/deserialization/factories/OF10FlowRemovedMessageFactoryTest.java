package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;

/**
 * @author michal.polkorab
 */
public class OF10FlowRemovedMessageFactoryTest {

	/**
     * Testing {@link OF10FlowRemovedMessageFactory} for correct translation into POJO
     */
    //@Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 03 01 04 00 00 00 02"
                + " 00 00 00 05 00 00 01 02 03 04 05 06 07 00 01 02 03 04 05 06 07");
        FlowRemovedMessage builtByFactory = BufferHelper.decodeV10(OF10FlowRemovedMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
        
        Assert.assertEquals("Wrong cookie", 0x0001020304050607L, builtByFactory.getCookie().longValue());
        Assert.assertEquals("Wrong priority", 0x03, builtByFactory.getPriority().intValue());
        Assert.assertEquals("Wrong reason", 0x01, builtByFactory.getReason().getIntValue());
        Assert.assertEquals("Wrong durationSec", 0x04000000L, builtByFactory.getDurationSec().longValue());
        Assert.assertEquals("Wrong durationNsec", 0x02000000L, builtByFactory.getDurationNsec().longValue());
        Assert.assertEquals("Wrong idleTimeout", 0x0500, builtByFactory.getIdleTimeout().intValue());
        Assert.assertEquals("Wrong packetCount", 0x0001020304050607L, builtByFactory.getPacketCount().longValue());
        Assert.assertEquals("Wrong byteCount", 0x0001020304050607L, builtByFactory.getByteCount().longValue());
    }

}
