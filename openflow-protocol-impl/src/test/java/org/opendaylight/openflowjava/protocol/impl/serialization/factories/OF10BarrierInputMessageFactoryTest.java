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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10BarrierInputMessageFactoryTest {

    /**
     * Testing of {@link OF10BarrierInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        BarrierInputBuilder bib = new BarrierInputBuilder();
        BufferHelper.setupHeader(bib, EncodeConstants.OF10_VERSION_ID);
        BarrierInput bi = bib.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10BarrierInputMessageFactory bimf = OF10BarrierInputMessageFactory.getInstance();
        bimf.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, bi);
        
        BufferHelper.checkHeaderV10(out, (byte) 18, 8);
    }

}
