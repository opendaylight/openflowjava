/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.ExperimenterIdDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ExperimenterMessageFactory}.
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class ExperimenterMessageFactoryTest {

    @Mock DeserializerRegistry experimenterRegistry;
    @Mock OFDeserializer<ExperimenterDataOfChoice> experimenterDeserializer;
    @Mock ExperimenterDataOfChoice message;
    private ExperimenterMessageFactory factory;

    @Before
    public void startUp() {
        factory = new ExperimenterMessageFactory();
        factory.assignVersion((short)EncodeConstants.OF13_VERSION_ID);
    }

    /**
     * Testing {@link ExperimenterMessageFactory} deserializer lookup correctness.
     */
    @Test
    public void test() {
        Mockito.when(experimenterRegistry.getDeserializer(Matchers.any(ExperimenterIdDeserializerKey.class)))
                .thenReturn(experimenterDeserializer);
        Mockito.when(experimenterDeserializer.deserialize(Matchers.any(ByteBuf.class))).thenReturn(message);

        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("00 01 02 03 00 00 00 10 00 00 00 20");
        factory.injectDeserializerRegistry(experimenterRegistry);
        ExperimenterMessage deserializedMessage = factory.deserialize(buffer);
        Assert.assertEquals("Wrong return value", message, deserializedMessage.getExperimenterDataOfChoice());
        Assert.assertEquals("ByteBuf index moved", 0, buffer.readableBytes());
    }
}