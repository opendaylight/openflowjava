/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import junit.framework.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessageBuilder;

/**
 * @author timotej.kubas
 *
 */
public class MultipartRequestMessageFactoryTest {
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(5));
        builder.setFlags(new MultipartRequestFlags(true));
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength());
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        // TODO multipart body
    }
    
    private static MultipartRequestFlags decodeMultipartRequestFlags(short input){
        final Boolean _oFPMPFREQMORE = (input & (1 << 0)) > 0;
        return new MultipartRequestFlags(_oFPMPFREQMORE);
    }
}
