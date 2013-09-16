/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class PortStatusMessageFactoryTest {

    /**
     * Testing {@link PortStatusMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("01 00 00 00 00 00 00 00");
        
        PortStatusMessage builtByFactory = BufferHelper.decodeV13(PortStatusMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        //Assert.assertEquals("Wrong reason", 0x01, builtByFactory.getReason());
    }
}
