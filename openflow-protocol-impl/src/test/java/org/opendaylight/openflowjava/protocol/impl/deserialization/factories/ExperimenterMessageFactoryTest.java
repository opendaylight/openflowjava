/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdDeserializerKey;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterMessageFactoryTest {

    @Mock DeserializerRegistry registry;
    @Mock OFDeserializer<ExperimenterMessage> deserializer;
    @Mock ExperimenterMessage message;

    /**
     * Initializes mocks
     */
    @Before
    public void startUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test deserializer lookup correctness
     */
    @Test
    public void test() {
        when(registry.getDeserializer(any(ExperimenterIdDeserializerKey.class))).thenReturn(deserializer);
        when(deserializer.deserialize(any(ByteBuf.class))).thenReturn(message);

        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("00 01 02 03 00 00 00 10");
        ExperimenterMessageFactory factory = new ExperimenterMessageFactory();
        factory.injectDeserializerRegistry(registry);
        ExperimenterMessage deserializedMessage = factory.deserialize(buffer);
        Assert.assertEquals("Wrong return value", message, deserializedMessage);
        Assert.assertEquals("ByteBuf index moved", 0, buffer.readerIndex());
    }
}