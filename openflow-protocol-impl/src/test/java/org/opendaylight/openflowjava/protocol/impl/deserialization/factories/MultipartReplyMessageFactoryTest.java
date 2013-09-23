/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactoryTest {

    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 07 00 01 00 00 00 00 01 02 03 04");
        MultipartReplyMessage builtByFactory = BufferHelper.decodeV13(MultipartReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertEquals("Wrong type", 0x07, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());
        //Assert.assertArrayEquals("Wrong body", new byte[]{0x01, 0x02, 0x03, 0x04}, builtByFactory.getBody());
    }
    
}
