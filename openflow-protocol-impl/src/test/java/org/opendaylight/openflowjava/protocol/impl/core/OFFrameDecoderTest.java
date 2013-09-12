/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;

/**
 * Testing class of {@link OFFrameDecoder}
 * @author michal.polkorab
 */
public class OFFrameDecoderTest {

    private EmbeddedChannel embch;

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        embch = new EmbeddedChannel(new OFFrameDecoder());
    }

    /**
     * Test of decoding {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * @throws Exception 
     */
    @Test
    public void testDecode8BMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertEquals(8, inObj.readableBytes());
    }

    /**
     * Test of decoding {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * @throws Exception 
     */
    @Test
    public void testDecode16BMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x01, 0x04, 0x00,
            0x00, 0x08, 0x00, 0x00, 0x00, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertEquals(16, inObj.readableBytes());
    }

    /**
     * Test of decoding {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * @throws Exception 
     */
    @Test
    public void testDecodeIncompleteMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertNull(inObj);
    }

    /**
     * Test of decoding {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * @throws Exception 
     */
    @Test
    public void testDecodeCompleteAndPartialMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x01,
            0x04, 0x00, 0x00, 0x08, 0x00};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        ByteBuf inObj = (ByteBuf) embch.readInbound();
        Assert.assertEquals(8, inObj.readableBytes());
        inObj = (ByteBuf) embch.readInbound();
        Assert.assertNull(inObj);
    }
}