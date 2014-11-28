/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterIdSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterInputMessageFactoryTest {

    @Mock SerializerRegistry registry;
    @Mock OFSerializer<ExperimenterInput> serializer;
    private OFSerializer<ExperimenterInput> expFactory;

    /**
     * Sets up ExperimenterInputMessageFactory
     * @param real true if setup should use real registry, false when mock is desired
     */
    public void startUp(boolean real) {
        MockitoAnnotations.initMocks(this);
        expFactory = new ExperimenterInputMessageFactory();
        if (real) {
            SerializerRegistry realRegistry = new SerializerRegistryImpl();
            realRegistry.init();
            ((SerializerRegistryInjector) expFactory).injectSerializerRegistry(realRegistry);
        } else {
            ((SerializerRegistryInjector) expFactory).injectSerializerRegistry(registry);
        }
    }

    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct serializer
     * lookup and serialization
     * @throws Exception
     */
    @Test(expected=IllegalStateException.class)
    public void testV10Real() throws Exception {
        startUp(true);
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setExperimenter(new ExperimenterId(42L));
        ExperimenterInput input = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        expFactory.serialize(input, out);
    }

    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct serializer
     * lookup and serialization
     * @throws Exception
     */
    @Test(expected=IllegalStateException.class)
    public void testV13Real() throws Exception {
        startUp(true);
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setExperimenter(new ExperimenterId(42L));
        ExperimenterInput input = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        expFactory.serialize(input, out);
    }

    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct serializer
     * lookup and serialization
     * @throws Exception
     */
    @Test
    public void testV10() throws Exception {
        startUp(false);
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF10_VERSION_ID);
        builder.setExperimenter(new ExperimenterId(42L));
        ExperimenterInput input = builder.build();

        Mockito.when(registry.getSerializer(
                (ExperimenterIdSerializerKey<?>) Matchers.any())).thenReturn(serializer);

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        expFactory.serialize(input, out);
    }

    /**
     * Testing of {@link ExperimenterInputMessageFactory} for correct serializer
     * lookup and serialization
     * @throws Exception
     */
    @Test
    public void testV13() throws Exception {
        startUp(false);
        ExperimenterInputBuilder builder = new ExperimenterInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setExperimenter(new ExperimenterId(42L));
        ExperimenterInput input = builder.build();

        Mockito.when(registry.getSerializer(
                (ExperimenterIdSerializerKey<?>) Matchers.any())).thenReturn(serializer);

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        expFactory.serialize(input, out);
    }
}