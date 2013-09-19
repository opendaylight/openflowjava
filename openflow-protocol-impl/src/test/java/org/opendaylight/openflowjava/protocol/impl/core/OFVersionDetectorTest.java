/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler.COMPONENT_NAMES;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;

/**
 * 
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class OFVersionDetectorTest {

    @Mock
    ChannelHandlerContext channelHandlerContext;

    @Mock
    ChannelPipeline channelPipeline;

    private OFVersionDetector detector;
    private List<Object> list = new ArrayList<>();

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        Mockito.when(channelHandlerContext.pipeline()).thenReturn(
                channelPipeline);
        Mockito.when(channelPipeline.get(Matchers.anyString()))
                .thenReturn(null);
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
        verifyMockCalls(1);
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
                ByteBufUtils.hexStringToByteBuf("01 00 00 08 00 00 00 01"),
                list);

        Assert.assertEquals("List is not empty", 0, list.size());
        verifyMockCalls(0);
    }

    private void verifyMockCalls(int numberOfCalls) {
        if (numberOfCalls > 0) {
            Mockito.verify(channelPipeline, Mockito.times(numberOfCalls)).get(
                    COMPONENT_NAMES.OF_DECODER.name());
            Mockito.verify(channelPipeline, Mockito.times(numberOfCalls))
                    .addBefore(
                            Matchers.eq(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER
                                    .name()),
                            Matchers.eq(COMPONENT_NAMES.OF_DECODER.name()),
                            Matchers.isA(OF13Decoder.class));
        } else {
            Mockito.verify(channelPipeline, Mockito.never()).get(
                    COMPONENT_NAMES.OF_DECODER.name());
            Mockito.verify(channelPipeline, Mockito.never()).addBefore(
                    Matchers.eq(COMPONENT_NAMES.DELEGATING_INBOUND_HANDLER
                            .name()),
                    Matchers.eq(COMPONENT_NAMES.OF_DECODER.name()),
                    Matchers.isA(OF13Decoder.class));
        }
    }
}