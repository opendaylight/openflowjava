/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10QueueGetConfigInputMessageFactoryTest {

    /**
     * Testing of {@link OF10QueueGetConfigInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        GetQueueConfigInputBuilder builder = new GetQueueConfigInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setPort(new PortNumber(6653L));
        GetQueueConfigInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10QueueGetConfigInputMessageFactory factory = OF10QueueGetConfigInputMessageFactory.getInstance();
        factory.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, message);
        
        BufferHelper.checkHeaderV10(out, (byte) 20, 12);
        Assert.assertEquals("Wrong port", 6653L, out.readUnsignedShort());
        out.skipBytes(2);
    }

}
