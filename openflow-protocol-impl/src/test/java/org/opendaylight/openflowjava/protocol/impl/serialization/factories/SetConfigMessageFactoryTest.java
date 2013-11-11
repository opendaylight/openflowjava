/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.SwitchConfigFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class SetConfigMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 9;
    private static final int MESSAGE_LENGTH = 12;
    
    /**
     * Testing of {@link SetConfigMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testSetConfigMessage() throws Exception {
        SetConfigInputBuilder builder = new SetConfigInputBuilder();
        BufferHelper.setupHeader(builder);
        SwitchConfigFlag flag = SwitchConfigFlag.FRAGNORMAL;
        builder.setFlags(flag);
        builder.setMissSendLen(10);
        SetConfigInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        SetConfigMessageFactory factory = SetConfigMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
        Assert.assertEquals("Wrong flags", message.getFlags().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong missSendLen", message.getMissSendLen().intValue(), out.readUnsignedShort());
    }
}
