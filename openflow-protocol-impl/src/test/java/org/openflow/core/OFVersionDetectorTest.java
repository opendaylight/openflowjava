/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openflow.core.TCPHandler.COMPONENT_NAMES;

/**
 *
 * @author michal.polkorab
 */
public class OFVersionDetectorTest {

    private EmbeddedChannel embch;

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        embch = new EmbeddedChannel(new OFVersionDetector());
    }

    /**
     * Test of decode {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     * @throws Exception 
     */
    @Test
    public void testDecode13ProtocolMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertEquals(7, inObj.readableBytes());
        Assert.assertNotNull(embch.pipeline().get(COMPONENT_NAMES.OF_CODEC.name()));
    }
    
    /**
     * Test of decode {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     * @throws Exception 
     */
    @Test
    public void testDecodeNotSupportedVersionProtocolMessage() throws Exception {
        byte[] msgs = new byte[]{0x01, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertNull(inObj);
        Assert.assertNull(embch.pipeline().get(COMPONENT_NAMES.OF_CODEC.name()));
    }
}