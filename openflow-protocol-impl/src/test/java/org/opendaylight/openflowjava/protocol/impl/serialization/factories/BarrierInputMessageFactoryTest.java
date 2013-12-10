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
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class BarrierInputMessageFactoryTest {

    private static final byte BARRIER_REQUEST_MESSAGE_CODE_TYPE = BarrierInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link BarrierInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        BarrierInputBuilder bib = new BarrierInputBuilder();
        BufferHelper.setupHeader(bib, EncodeConstants.OF13_VERSION_ID);
        BarrierInput bi = bib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        BarrierInputMessageFactory bimf = BarrierInputMessageFactory.getInstance();
        bimf.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, bi);
        
        BufferHelper.checkHeaderV13(out, BARRIER_REQUEST_MESSAGE_CODE_TYPE, 8);
    }

}
