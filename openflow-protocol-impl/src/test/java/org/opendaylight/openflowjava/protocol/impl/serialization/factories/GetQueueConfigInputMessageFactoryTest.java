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
import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetQueueConfigInputMessageFactoryTest {
    private static final byte GET_QUEUE_CONFIG_INPUT_MESSAGE_CODE_TYPE = 22;
    private static final byte PADDING_IN_QUEUE_CONFIG_INPUT_MESSAGE = 4;
    
    /**
     * Testing of {@link GetQueueConfigInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testGetQueueConfigInputMessage() throws Exception {
        GetQueueConfigInputBuilder builder = new GetQueueConfigInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setPort(new PortNumber(0x00010203L));
        GetQueueConfigInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GetQueueConfigInputMessageFactory factory = GetQueueConfigInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, GET_QUEUE_CONFIG_INPUT_MESSAGE_CODE_TYPE, 16);
        Assert.assertEquals("Wrong port", 0x00010203, out.readUnsignedInt());
        out.skipBytes(PADDING_IN_QUEUE_CONFIG_INPUT_MESSAGE);
    }
}
