/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;

/**
 * @author michal.polkorab
 * 
 */
public class HelloMessageFactoryTest {

    /** Number of currently supported version / codec */
    public static final Short VERSION_YET_SUPPORTED = 0x04;

    /**
     * Testing {@link HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer();
        HelloMessage builtByFactory = BufferHelper.decodeV13(
                HelloMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
    }
}
