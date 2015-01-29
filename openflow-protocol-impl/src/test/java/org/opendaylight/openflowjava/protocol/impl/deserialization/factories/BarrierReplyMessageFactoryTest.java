/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class BarrierReplyMessageFactoryTest {

    private OFDeserializer<BarrierOutput> barrierFactory;

    /**
     * Initializes deserializer registry and lookups correct deserializer
     */
    @Before
    public void startUp() {
        DeserializerRegistry registry = new DeserializerRegistryImpl();
        registry.init();
        barrierFactory = registry.getDeserializer(
                new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 21, BarrierOutput.class));
    }

    /**
     * Testing of {@link BarrierReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer();
        BarrierOutput builtByFactory = BufferHelper.deserialize(
                barrierFactory, bb);

        BufferHelper.checkHeaderV13(builtByFactory);
    }
}
