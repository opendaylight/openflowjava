/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;

/**
 * @author madamjak
 *
 */
public class OFDatagramPacketDecoderTest {
    @Mock DeserializationFactory deserializationFactory;
    @Mock ChannelHandlerContext ctx;
    @Mock ByteBuf messageBufferMock;

    private VersionMessageUdpWrapper msgWrapper;
    private List<Object> out;
    private ByteBuf messageBuffer;
    @Before
    public void startUp(){
        MockitoAnnotations.initMocks(this);
        out = new ArrayList<>();
        messageBuffer = BufferHelper.buildBuffer( "00 01 " // type
                                                + "00 0c " // length
                                                + "00 00 00 11 " // bitmap 1
                                                + "00 00 00 00 " // bitmap 2
                                                + "00 00 00 00"  // padding 
                                                );
    }
 
    @Test
    public void test01() {
        OFDatagramPacketDecoder decoder = new OFDatagramPacketDecoder();
        decoder.setDeserializationFactory(deserializationFactory);
        msgWrapper = new VersionMessageUdpWrapper(EncodeConstants.OF13_VERSION_ID, messageBuffer, new InetSocketAddress("10.0.0.1", 6653));
        try {
            decoder.decode(ctx, msgWrapper, out);
        } catch (Exception e) {
            Assert.fail("Exception occured");
        }
    }

    @Test
    public void test02() {
        OFDatagramPacketDecoder decoder = new OFDatagramPacketDecoder();
        decoder.setDeserializationFactory(deserializationFactory);
        msgWrapper = new VersionMessageUdpWrapper(EncodeConstants.OF13_VERSION_ID, messageBufferMock, new InetSocketAddress("10.0.0.1", 6653));
        try {
            decoder.decode(ctx, msgWrapper, out);
        } catch (Exception e) {
            Assert.fail("Exception occured");
        }
        verify(messageBufferMock, times(1)).release();
    }
}
