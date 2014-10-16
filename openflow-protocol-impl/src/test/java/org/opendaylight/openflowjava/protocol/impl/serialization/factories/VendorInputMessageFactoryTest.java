/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class VendorInputMessageFactoryTest {

    @Mock SerializerRegistry registry;
    @Mock ExperimenterInputMessageFactory serializer;

    /**
     * Tests {@link VendorInputMessageFactory#serialize(ExperimenterInput, ByteBuf)}
     */
    @Test
    public void test() {
        Mockito.when(registry.getSerializer((MessageTypeKey<?>) Matchers.any(MessageTypeKey.class)))
            .thenReturn(serializer);
        VendorInputMessageFactory factory = new VendorInputMessageFactory();
        factory.injectSerializerRegistry(registry);
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        builder.setVersion((short) EncodeConstants.OF10_VERSION_ID);
        builder.setXid(12345L);
        builder.setExperimenter(new ExperimenterId(42L));
        builder.setExpType(84L);
        ExperimenterInput experimenterInput = builder.build();

        factory.serialize(experimenterInput, buffer);
        Mockito.verify(serializer, Mockito.times(1)).serialize(experimenterInput, buffer);
    }
}