/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;

/**
 * @author michal.polkorab
 *
 */
public class HelloMessageFactoryTest {
    
    /** Number of currently supported version / codec */
    public static final short VERSION_YET_SUPPORTED = 0x04;
    /** Index of Xid in OpenFlow 1.3 header */
    public static final byte INDEX_OF_XID_IN_HEADER = 4;

    /**
     * Testing {@link HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer(new byte[0]);
        HelloMessage builtByFactory = HelloMessageFactory.getInstance().bufferToMessage(bb, VERSION_YET_SUPPORTED);

        Assert.assertTrue(builtByFactory.getVersion() == VERSION_YET_SUPPORTED);
        Assert.assertEquals(builtByFactory.getXid().longValue(), 16909060L);
    }
}
