/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10VendorInputMessageFactoryTest {

    /**
     * Testing of {@link OF10VendorInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setExperimenter(0x0001020304L);
        builder.setData(new byte[] {0x01, 0x02, 0x03, 0x04});
        ExperimenterInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10VendorInputMessageFactory factory = OF10VendorInputMessageFactory.getInstance();
        factory.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, message);
        
        BufferHelper.checkHeaderV10(out, (byte) 4, factory.computeLength(message));
        Assert.assertEquals("Wrong experimenter", 0x0001020304L, out.readUnsignedInt());
        byte[] data = new byte[4];
        out.readBytes(data);
        Assert.assertArrayEquals("Wrong data", message.getData(), data);
    }

}
