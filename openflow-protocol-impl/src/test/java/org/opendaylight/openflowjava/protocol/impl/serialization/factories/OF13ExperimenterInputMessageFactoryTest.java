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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF13ExperimenterInputMessageFactoryTest {

    private static final byte EXPERIMENTER_REQUEST_MESSAGE_CODE_TYPE = 4;
    private SerializerRegistry registry;
    private OFSerializer<ExperimenterInput> expFactory;

    /**
     * Initializes serializer registry and stores correct factory in field
     */
    @Before
    public void startUp() {
        registry = new SerializerRegistryImpl();
        registry.init();
        expFactory = registry.getSerializer(
                new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, ExperimenterInput.class));
    }

    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setExperimenter(0x0001020304L);
        builder.setExpType(0x0001020304L);
        byte[] expData = new byte[] {0, 1, 2, 3, 4, 5, 6, 7};
        builder.setData(expData);
        ExperimenterInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        expFactory.serialize(message, out);
        
        BufferHelper.checkHeaderV13(out, EXPERIMENTER_REQUEST_MESSAGE_CODE_TYPE, 24);
        Assert.assertEquals("Wrong experimenter", 0x0001020304L, out.readUnsignedInt());
        Assert.assertEquals("Wrong expType", 0x0001020304L, out.readUnsignedInt());
        byte[] tmp = new byte[8];
        out.readBytes(tmp);
        Assert.assertArrayEquals("Wrong data", expData, tmp);
    }
    
}