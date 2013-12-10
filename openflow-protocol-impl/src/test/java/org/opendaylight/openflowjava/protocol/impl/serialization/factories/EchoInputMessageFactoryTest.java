/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class EchoInputMessageFactoryTest {

    private static final byte ECHO_REQUEST_MESSAGE_CODE_TYPE = EchoInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link EchoInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV13() throws Exception {
        EchoInputBuilder eib = new EchoInputBuilder();
        BufferHelper.setupHeader(eib, EncodeConstants.OF13_VERSION_ID);
        EchoInput ei = eib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoInputMessageFactory eimf = EchoInputMessageFactory.getInstance();
        eimf.messageToBuffer(EncodeConstants.OF13_VERSION_ID, out, ei);
        
        BufferHelper.checkHeaderV13(out, ECHO_REQUEST_MESSAGE_CODE_TYPE, 8);
    }
    
    /**
     * Testing of {@link EchoInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testV10() throws Exception {
        EchoInputBuilder eib = new EchoInputBuilder();
        BufferHelper.setupHeader(eib, EncodeConstants.OF10_VERSION_ID);
        EchoInput ei = eib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        EchoInputMessageFactory eimf = EchoInputMessageFactory.getInstance();
        eimf.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, ei);
        
        BufferHelper.checkHeaderV10(out, ECHO_REQUEST_MESSAGE_CODE_TYPE, 8);
    }

}
