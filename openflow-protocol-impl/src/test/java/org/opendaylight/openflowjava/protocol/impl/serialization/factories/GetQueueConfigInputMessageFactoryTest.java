/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import static org.junit.Assert.*;
import junit.framework.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
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
        BufferHelper.setupHeader(builder);
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
