/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timotej.kubas
 *
 */
public class PacketInMessageFactoryTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PacketInMessageFactoryTest.class);
    
    /**
     * Testing {@link PacketInMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 01 02 01 04 00 01 02 03 04 05 06 07 00 01 00 0C"
                + " 80 00 02 04 00 00 00 01 00 00 00 00 00 00 01 02 03 04");
        PacketInMessage builtByFactory = BufferHelper.decodeV13(PacketInMessageFactory.getInstance(), bb); 
        
        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertEquals("Wrong bufferID", 0x00010203L, builtByFactory.getBufferId().longValue());
        Assert.assertEquals("Wrong totalLength", 0x0102, builtByFactory.getTotalLen().intValue());
        Assert.assertEquals("Wrong reason", PacketInReason.OFPRACTION, builtByFactory.getReason());
        Assert.assertEquals("Wrong tableID", new TableId(4L), builtByFactory.getTableId());
        Assert.assertEquals("Wrong cookie", 0x0001020304050607L, builtByFactory.getCookie().longValue());
        Assert.assertArrayEquals("Wrong data", ByteBufUtils.hexStringToBytes("01 02 03 04"), builtByFactory.getData());
    }
}
