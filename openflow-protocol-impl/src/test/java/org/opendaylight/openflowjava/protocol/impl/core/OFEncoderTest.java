/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 *
 * @author jameshall
 */
public class OFEncoderTest {

    @Mock ChannelHandlerContext mockChHndlrCtx ;
    @Mock SerializationFactory mockSerializationFactory ;
    @Mock OfHeader mockMsg ;
    @Mock ByteBuf mockOut ;

    OFEncoder ofEncoder = new OFEncoder() ;

    /**
     * Sets up test environment
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ofEncoder = new OFEncoder() ;
        ofEncoder.setSerializationFactory( mockSerializationFactory ) ;
    }

    /**
     * Test successful write (no clear)
     */
    @Test
    public void testEncodeSuccess() {
        when(mockOut.readableBytes()).thenReturn(1);
        try {
            ofEncoder.encode(mockChHndlrCtx, mockMsg, mockOut);
        } catch (Exception e) {
            Assert.fail();
        }

        // Verify that the channel was flushed after the ByteBuf was retained.
        verify(mockOut, times(0)).clear();
    }

    /**
     * Test Bytebuf clearing after serialization failure
     */
    @Test
    public void testEncodeSerializationException() {
        doThrow(new IllegalArgumentException()).when(mockSerializationFactory).messageToBuffer(anyShort(),any(ByteBuf.class), any(DataObject.class));
        try {
            ofEncoder.encode(mockChHndlrCtx, mockMsg, mockOut);
        } catch (Exception e) {
            Assert.fail();
        }

        // Verify that the output message buf was cleared...
        verify(mockOut, times(1)).clear();
    }

    /**
     * Test no action on empty bytebuf
     */
    @Test
    public void testEncodeSerializesNoBytes() {
        when(mockOut.readableBytes()).thenReturn(0);
        try {
            ofEncoder.encode(mockChHndlrCtx, mockMsg, mockOut);
        } catch (Exception e) {
            Assert.fail();
        }

        // Verify that the output message buf was cleared...
        verify(mockOut, times(0)).clear();
        verify(mockChHndlrCtx, times(0)).writeAndFlush(mockOut);
        verify(mockOut, times(0)).retain();
    }
}