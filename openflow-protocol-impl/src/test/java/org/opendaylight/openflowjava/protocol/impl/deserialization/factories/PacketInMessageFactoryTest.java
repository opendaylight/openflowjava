/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;

/**
 * @author timotej.kubas
 *
 */
public class PacketInMessageFactoryTest {

    /**
     * Testing {@link PacketInMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 01 04 00 01 02 03 04 05 06 07 00 00 01 02 03 04");
        PacketInMessage builtByFactory = BufferHelper.decodeV13(PacketInMessageFactory.getInstance(), bb); 
        
        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertEquals("Wrong bufferID", 0x00010203L, builtByFactory.getBufferId().longValue());
        Assert.assertEquals("Wrong reason", 0x01, builtByFactory.getReason().shortValue());
        Assert.assertEquals("Wrong tableID", new TableId((long) 4), builtByFactory.getTableId());
        Assert.assertEquals("Wrong cookie", 0x0001020304050607L, builtByFactory.getCookie().longValue());
        Assert.assertArrayEquals("Wrong data", ByteBufUtils.hexStringToBytes("01 02 03 04"), builtByFactory.getData());
    }
}
