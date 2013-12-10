/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class EchoReplyMessageFactoryTest {

    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataField() {
        ByteBuf bb = BufferHelper.buildBuffer();
        EchoOutput builtByFactory = BufferHelper.decodeV13(
                EchoReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
    }
    
    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSet() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoOutput builtByFactory = BufferHelper.decodeV13(
                EchoReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }
    
    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithEmptyDataFieldV10() {
        ByteBuf bb = BufferHelper.buildBuffer();
        EchoOutput builtByFactory = BufferHelper.decodeV10(
                EchoReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
    }
    
    /**
     * Testing {@link EchoReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testWithDataFieldSetV10() {
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoOutput builtByFactory = BufferHelper.decodeV10(
                EchoReplyMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }

}
