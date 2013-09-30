/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInputBuilder;

/**
 * @author timotej.kubas
 *
 */
public class PacketOutInputMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 13;
    private static final int MESSAGE_LENGTH = 30;
    private static final byte PADDING_IN_PACKET_OUT_MESSAGE = 6;
       
    /**
     * Testing of {@link PacketOutInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testPacketOutInputMessage() throws Exception {
        PacketOutInputBuilder builder = new PacketOutInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setBufferId(256L);
        builder.setInPort(new PortNumber(256L));
        PacketOutInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        PacketOutInputMessageFactory factory = PacketOutInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
        Assert.assertEquals("Wrong BufferId", message.getBufferId().longValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong PortNumber", message.getInPort().getValue().longValue(), out.readUnsignedInt());
        // TODO make test for actions after its implementation in factory 
        // TODO data
        out.skipBytes(PADDING_IN_PACKET_OUT_MESSAGE);
    }
}
