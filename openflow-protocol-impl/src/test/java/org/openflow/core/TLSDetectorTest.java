/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openflow.core.TCPHandler.COMPONENT_NAMES;

/**
 *
 * @author michal.polkorab
 */
public class TLSDetectorTest {
    
    private EmbeddedChannel embch;

    @Before
    public void setUp() {
        embch = new EmbeddedChannel(new TLSDetector());
    }

    /**
     * Test of decode {@link TLSDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List) }
     */
    @Test
    public void testDecodeNotEncryptedMessage() throws Exception {
       byte[] msgs = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertEquals(8, inObj.readableBytes());
        Assert.assertNull(embch.pipeline().get(COMPONENT_NAMES.SSL_HANDLER.name()));
    }
    
    /**
     * Test of decode {@link TLSDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List) }
     */
    @Test
    public void testDecodeEncryptedMessage() throws Exception {
        // TODO - implement this test with correct message and asserts
        byte[] msgs = new byte[]{16, 03, 01, 00, 95, 01, 00, 00, 91, 03, 01, 52, 26};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
    }
}