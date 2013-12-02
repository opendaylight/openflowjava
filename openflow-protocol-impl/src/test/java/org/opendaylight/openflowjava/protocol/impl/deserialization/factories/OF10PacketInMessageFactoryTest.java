package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;

public class OF10PacketInMessageFactoryTest {

	/**
     * Testing {@link OF10PacketInMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 01 02 01 02 00 00 01 02 03 04");
        PacketInMessage builtByFactory = BufferHelper.decodeV10(OF10PacketInMessageFactory.getInstance(), bb); 
        
        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertEquals("Wrong bufferID", 0x00010203L, builtByFactory.getBufferId().longValue());
        Assert.assertEquals("Wrong totalLength", 0x0102, builtByFactory.getTotalLen().intValue());
        Assert.assertEquals("Wrong inPort", 0x0102, builtByFactory.getInPort().intValue());
        Assert.assertEquals("Wrong reason", PacketInReason.OFPRNOMATCH, builtByFactory.getReason());
        Assert.assertArrayEquals("Wrong data", ByteBufUtils.hexStringToBytes("01 02 03 04"), builtByFactory.getData());
    }

}
