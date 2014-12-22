/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import src.main.java.org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;

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
