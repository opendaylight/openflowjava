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

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerTableImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GetaAsyncRequestMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 26;
    private static final int MESSAGE_LENGTH = 8;
    private SerializerTable table;
    private OFSerializer<GetAsyncInput> getAsyncFactory;

    /**
     * Initializes serializer table and stores correct factory in field
     */
    @Before
    public void startUp() {
        table = new SerializerTableImpl();
        table.init();
        getAsyncFactory = table.getSerializer(
                new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, GetAsyncInput.class));
    }

    /**
     * Testing of {@link GetAsyncRequestMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testGetAsyncReques() throws Exception {
        GetAsyncInputBuilder builder = new GetAsyncInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        GetAsyncInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        getAsyncFactory.serialize(message, out);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
    }
}
