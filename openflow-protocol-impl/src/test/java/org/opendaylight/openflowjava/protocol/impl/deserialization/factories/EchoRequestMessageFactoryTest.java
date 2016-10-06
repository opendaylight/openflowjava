/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoRequestMessageFactory}.
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class EchoRequestMessageFactoryTest {

    private OFDeserializer<EchoRequestMessage> echoFactory;
    private DeserializerRegistry registry;

    /**
     * Initializes deserializer registry.
     */
    @Before
    public void startUp() {
        registry = new DeserializerRegistryImpl();
        registry.init();
    }

    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO + correct version.
     */
    @Test
    public void testVersionAndEmptyDataField() {
        testVersionAndEmptyDataField(EncodeConstants.OF13_VERSION_ID);
        testVersionAndEmptyDataField(EncodeConstants.OF14_VERSION_ID);
        testVersionAndEmptyDataField(EncodeConstants.OF15_VERSION_ID);
    }

    private void testVersionAndEmptyDataField (short version) {
        echoFactory = registry.getDeserializer(new MessageCodeKey(version, 2, EchoRequestMessage.class));
        byte[] data = new byte[]{};
        ByteBuf bb = BufferHelper.buildBuffer();
        EchoRequestMessage builtByFactory = BufferHelper.deserialize(echoFactory, bb);
        BufferHelper.checkHeader(builtByFactory, version);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }

    /**
     * Testing {@link EchoRequestMessageFactory} for correct translation into POJO.
     */
    @Test
    public void testWithDataFieldSet() {
        echoFactory = registry.getDeserializer(
                new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 2, EchoRequestMessage.class));
        byte[] data = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteBuf bb = BufferHelper.buildBuffer(data);
        EchoRequestMessage builtByFactory = BufferHelper.deserialize(echoFactory, bb);
        Assert.assertArrayEquals("Wrong data", data, builtByFactory.getData());
    }
}
