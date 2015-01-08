/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionFacade;
import org.opendaylight.openflowjava.util.ByteBufUtils;

/**
 * Testing class of {@link OFFrameDecoder}
 *
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class OFFrameDecoderTest {


    @Mock
    private List<Object> list = new ArrayList<>();

    /**
     * Sets up tests
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        list.clear();

    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     */
    @Test
    public void testDecode8BMessage() {
        OFFrameDecoder decoder = new OFFrameDecoder();
        EmbeddedChannel Channel = new EmbeddedChannel(decoder);
        ByteBuf byteBuffer = ByteBufUtils.hexStringToByteBuf("04 00 00 08 00 00 00 01");
        try {
            Assert.assertTrue(Channel.writeInbound(byteBuffer));
            Assert.assertTrue(Channel.finish());
        } catch (Exception e) {
            Assert.fail();
        }
        byteBuffer = (ByteBuf) (Channel.readInbound());
        assertNotNull(byteBuffer);
        assertEquals(8, byteBuffer.readableBytes());
    }

    /**
     * Test of decoding
     * {@link OFFrameDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)}
     */
    @Test
    public void testDecode16BMessage() {
        OFFrameDecoder decoder = new OFFrameDecoder();
        EmbeddedChannel Channel = new  EmbeddedChannel(decoder);
        ByteBuf byteBuffer = ByteBufUtils
                .hexStringToByteBuf("04 00 00 10 00 00 00 00 00 00 00 00 00 00 00 42");
        try {
            Assert.assertTrue(Channel.writeInbound(byteBuffer));
            Assert.assertTrue(Channel.finish());
        } catch (Exception e) {
            Assert.fail();
        }
        byteBuffer = (ByteBuf) (Channel.readInbound());
        assertNotNull(byteBuffer);
        assertEquals(16, byteBuffer.readableBytes());
    }

    /**
     * There is no benefit in testing partial frames, corrupt frames, etc - this is all caught by
     * netty core tests for the LengthFieldBasedFrameDecoder.
     */
}
