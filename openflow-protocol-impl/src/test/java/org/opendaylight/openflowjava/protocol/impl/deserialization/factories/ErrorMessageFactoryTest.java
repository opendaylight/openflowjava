/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class ErrorMessageFactoryTest {

    /**
     * Test of {@link ErrorMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 04 00 03 01 02 03 04");
        ErrorMessage builtByFactory = BufferHelper.decodeV13(ErrorMessageFactory.getInstance(), bb);
        BufferHelper.checkHeaderV13(builtByFactory);
        
        Assert.assertEquals("Wrong reason", 0x04, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong code", 3, builtByFactory.getCode().intValue());
        Assert.assertArrayEquals("Wrong body", new byte[]{0x01, 0x02, 0x03, 0x04}, builtByFactory.getData());
    }
}
