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

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ControllerRole;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class RoleRequestInputMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 24;
    private static final int MESSAGE_LENGTH = 24;
    private static final byte PADDING_IN_ROLE_REQUEST_MESSAGE = 4;
    
    /**
     * Testing of {@link RoleRequestInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testRoleRequestInputMessage() throws Exception {
        RoleRequestInputBuilder builder = new RoleRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setRole(ControllerRole.forValue(2));
        byte[] generationId = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setGenerationId(new BigInteger(1, generationId));
        RoleRequestInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        RoleRequestInputMessageFactory factory = RoleRequestInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
        Assert.assertEquals("Wrong role", message.getRole().getIntValue(), ControllerRole.forValue((int) out.readUnsignedInt()).getIntValue());
        out.skipBytes(PADDING_IN_ROLE_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong generation ID", message.getGenerationId().longValue(), out.readLong());
    }
}
