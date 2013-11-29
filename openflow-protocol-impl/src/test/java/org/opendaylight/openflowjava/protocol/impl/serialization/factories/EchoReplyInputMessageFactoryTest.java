/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoReplyInputMessageFactoryTest {

    private static final byte ECHO_REPLY_MESSAGE_CODE_TYPE = EchoReplyInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link EchoReplyInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV13() throws Exception {
        EchoReplyInputBuilder erib = new EchoReplyInputBuilder();
        BufferHelper.setupHeader(erib, EncodeConstants.OF13_VERSION_ID);
        EchoReplyInput eri = erib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoReplyInputMessageFactory eimf = EchoReplyInputMessageFactory.getInstance();
        eimf.messageToBuffer(EncodeConstants.OF13_VERSION_ID, out, eri);
        
        BufferHelper.checkHeaderV13(out, ECHO_REPLY_MESSAGE_CODE_TYPE, 8);
    }
    
    /**
     * Testing of {@link EchoReplyInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV10() throws Exception {
        EchoReplyInputBuilder erib = new EchoReplyInputBuilder();
        BufferHelper.setupHeader(erib, EncodeConstants.OF10_VERSION_ID);
        EchoReplyInput eri = erib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoReplyInputMessageFactory eimf = EchoReplyInputMessageFactory.getInstance();
        eimf.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, eri);
        
        BufferHelper.checkHeaderV10(out, ECHO_REPLY_MESSAGE_CODE_TYPE, 8);
    }

}
