/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class EchoRequestMessageFactoryTest {

    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataField() {
        ByteBuf bb = BufferHelper.buildBuffer();
        EchoRequestMessage builtByFactory = BufferHelper.decodeV13(
                EchoRequestMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
    }
    
    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSet() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoRequestMessage builtByFactory = BufferHelper.decodeV13(
                EchoRequestMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }
    
    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataFieldV10() {
        ByteBuf bb = BufferHelper.buildBuffer();
        EchoRequestMessage builtByFactory = BufferHelper.decodeV10(
                EchoRequestMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
    }
    
    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSetV10() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoRequestMessage builtByFactory = BufferHelper.decodeV10(
                EchoRequestMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }

}
