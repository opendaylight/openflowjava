/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;

/**
 * Testing class of {@link OFFrameDecoder}
 * 
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class OFFrameDecoderTest {

    @Mock
    ChannelHandlerContext channelHandlerContext;

    private OFFrameDecoder decoder;
    private List<Object> list = new ArrayList<>();

    /**
     * Sets up tests
     */
    //@Before
    public void setUp() {
        list.clear();
        //decoder = new OFFrameDecoder();
    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * 
     * @throws Exception
     */
    @Test
    public void testDecode8BMessage() throws Exception {
        decoder.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("04 00 00 08 00 00 00 01"),
                list);

        Assert.assertEquals(8, ((ByteBuf) list.get(0)).readableBytes());
    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * 
     * @throws Exception
     */
    @Test
    public void testDecode16BMessage() throws Exception {
        decoder.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("04 00 00 10 00 00 00 00 00 00 00 00 00 00 00 42"),
                list);

        Assert.assertEquals(16, ((ByteBuf) list.get(0)).readableBytes());
    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * 
     * @throws Exception
     */
    @Test
    public void testDecodeIncompleteMessage() throws Exception {
        decoder.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("04 00 00 08 00"),
                list);

        Assert.assertEquals("List is not empty", 0, list.size());
    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     * 
     * @throws Exception
     */
    @Test
    public void testDecodeCompleteAndPartialMessage() throws Exception {
        decoder.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("04 00 00 08 00 00 00 01 04 00 00 08 00"),
                list);

        Assert.assertEquals(8, ((ByteBuf) list.get(0)).readableBytes());
        Assert.assertEquals(1, list.size());
    }
    
}