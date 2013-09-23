/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class GetConfigReplyMessageFactoryTest {

    /**
     * Testing {@link GetConfigReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 00 03");
        GetConfigOutput builtByFactory = BufferHelper.decodeV13(
                GetConfigReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong switchConfigFlag", 0x01, builtByFactory.getFlags().getIntValue()); 
        Assert.assertEquals("Wrong missSendLen", 0x03, builtByFactory.getMissSendLen().intValue());
    }
    
}
