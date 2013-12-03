/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10PortModInputMessageFactoryTest {

    /**
     * Testing of {@link OF10PortModInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testPortModInput() throws Exception {
        PortModInputBuilder builder = new PortModInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setPortNo(new PortNumber(6633L));
        builder.setHwAddress(new MacAddress("08:00:27:00:B0:EB"));
        builder.setConfigV10(new PortConfigV10(true, false, false, true, false, false, true));
        builder.setMaskV10(new PortConfigV10(false, true, true, false, false, true, false));
        builder.setAdvertiseV10(new PortFeaturesV10(true, true, false, false, false, false,
                false, true, true, false, false, false));
        PortModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF10PortModInputMessageFactory factory = OF10PortModInputMessageFactory.getInstance();
        factory.messageToBuffer(EncodeConstants.OF10_VERSION_ID, out, message);
        
        BufferHelper.checkHeaderV10(out, (byte) 15, 32);
        Assert.assertEquals("Wrong PortNo", message.getPortNo().getValue().longValue(), out.readUnsignedShort());
        byte[] address = new byte[6];
        out.readBytes(address);
        Assert.assertEquals("Wrong MacAddress", message.getHwAddress(),
                new MacAddress(ByteBufUtils.macAddressToString(address)));
        Assert.assertEquals("Wrong config", 21, out.readUnsignedInt());
        Assert.assertEquals("Wrong mask", 98, out.readUnsignedInt());
        Assert.assertEquals("Wrong advertise", 652, out.readUnsignedInt());
        out.skipBytes(4);
    }

}
