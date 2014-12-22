/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import src.main.java.org.opendaylight.openflowjava.protocol.impl.core.OFVersionDetector;
import src.main.java.org.opendaylight.openflowjava.protocol.impl.core.VersionMessageWrapper;
import src.main.java.org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionFacade;

/**
 *
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class OFVersionDetectorTest {

    @Mock
    ChannelHandlerContext channelHandlerContext;
    ConnectionFacade connectionFacade;

    private OFVersionDetector detector;
    private List<Object> list = new ArrayList<>();

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        list.clear();
        detector = new OFVersionDetector(connectionFacade, false);
    }

    /**
     * Test of decode
     * {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     *
     * @throws Exception
     */
    @Test
    public void testDecode13ProtocolMessage() throws Exception {
        detector.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("04 00 00 08 00 00 00 01"),
                list);

        Assert.assertEquals(7, ((VersionMessageWrapper) list.get(0))
                .getMessageBuffer().readableBytes());
    }

    /**
     * Test of decode
     * {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     * @throws Exception
     */
    @Test
    public void testDecode10ProtocolMessage() throws Exception {
        detector.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("01 00 00 08 00 00 00 01"),
                list);

        Assert.assertEquals(7, ((VersionMessageWrapper) list.get(0))
                .getMessageBuffer().readableBytes());
    }

    /**
     * Test of decode
     * {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     * @throws Exception
     */
    @Test
    public void testDecodeEmptyProtocolMessage() throws Exception {
        ByteBuf byteBuffer = ByteBufUtils.hexStringToByteBuf("01 00 00 08 00 00 00 01").skipBytes(8);
        detector.decode(channelHandlerContext,
                byteBuffer,
                list);

        assertEquals( 0, byteBuffer.refCnt() ) ;

    }

    /**
     * Test of decode
     * {@link OFVersionDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     * }
     *
     * @throws Exception
     */
    @Test
    public void testDecodeNotSupportedVersionProtocolMessage() throws Exception {
        detector.decode(channelHandlerContext,
                ByteBufUtils.hexStringToByteBuf("02 00 00 08 00 00 00 01"),
                list);

        Assert.assertEquals("List is not empty", 0, list.size());
    }

}