/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterInputMessageFactoryTest {

    private static final byte EXPERIMENTER_REQUEST_MESSAGE_CODE_TYPE = ExperimenterInputMessageFactory.MESSAGE_TYPE;
    
    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setExperimenter(0x0001020304L);
        builder.setExpType(0x0001020304L);
        builder.setData(new byte[] {0x01, 0x02, 0x03});
        ExperimenterInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        ExperimenterInputMessageFactory factory = ExperimenterInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, EXPERIMENTER_REQUEST_MESSAGE_CODE_TYPE, factory.computeLength());
        Assert.assertEquals("Wrong experimenter", 0x0001020304L, out.readUnsignedInt());
        Assert.assertEquals("Wrong expType", 0x0001020304L, out.readUnsignedInt());
        Assert.assertArrayEquals("Wrong data", message.getData(), readData(out));
    }
    
    private static byte[] readData(ByteBuf input) {
        byte[] data = new byte[input.readableBytes()]; 
        for(int i = 0;i <= data.length-1; i++) {
            data[i] = input.readByte();
        }
        return data;
    }
}
