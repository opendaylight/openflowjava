/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;

/**
 * @author michal.polkorab
 */
public class OF10HelloMessageFactoryTest {

	/**
     * Testing {@link OF10HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithoutElements() {
        ByteBuf bb = BufferHelper.buildBuffer();
        HelloMessage builtByFactory = BufferHelper.decodeV10(
                OF10HelloMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertNull("Wrong elements", builtByFactory.getElements());
    }
	
	/**
     * Testing {@link OF10HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithElements() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 " // type
                                            + "00 0c " // length
                                            + "00 00 00 11 " // bitmap 1
                                            + "00 00 00 00 " // bitmap 2
                                            + "00 00 00 00"  // padding
                );
        HelloMessage builtByFactory = BufferHelper.decodeV10(
                OF10HelloMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertNull("Wrong elements", builtByFactory.getElements());
    }

}
