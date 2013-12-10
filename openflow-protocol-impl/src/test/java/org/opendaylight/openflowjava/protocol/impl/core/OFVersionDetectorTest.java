/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

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
 * 
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class OFVersionDetectorTest {

    @Mock
    ChannelHandlerContext channelHandlerContext;

    private OFVersionDetector detector;
    private List<Object> list = new ArrayList<>();

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        list.clear();
        detector = new OFVersionDetector();
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