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
public class TLSDetectorTest {
    
    private EmbeddedChannel embch;

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        TLSDetector tlsDetector = new TLSDetector();
        embch = new EmbeddedChannel(new DummyDecoder());
        embch.pipeline().addFirst(TCPHandler.COMPONENT_NAMES.TLS_DETECTOR.name(), tlsDetector);
    }

    /**
     * Test of decode {@link TLSDetector#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List) }
     * @throws Exception 
     */
    @Test
    public void testDecodeNotEncryptedMessage() throws Exception {
        byte[] msgs = new byte[]{0x04, 0x0, 0x0, 0x08, 0x0, 0x0, 0x0, 0x01};
        ByteBuf writeObj = embch.alloc().buffer(64);
        writeObj.writeBytes(msgs);
        embch.writeInbound(writeObj);

        Assert.assertNull(embch.pipeline().get(COMPONENT_NAMES.TLS_DETECTOR.name()));
        Assert.assertNull(embch.pipeline().get(COMPONENT_NAMES.SSL_HANDLER.name()));
    }
}