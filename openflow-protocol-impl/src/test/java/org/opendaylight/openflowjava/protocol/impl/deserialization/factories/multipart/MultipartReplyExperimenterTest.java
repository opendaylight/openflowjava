/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories.multipart;

import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.MultipartReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.DefaultDeserializerFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.deserialization.factories.MultipartReplyMessageFactory}.
 * @author michal.polkorab
 */
@RunWith(MockitoJUnitRunner.class)
public class MultipartReplyExperimenterTest extends DefaultDeserializerFactoryTest<MultipartReplyMessage> {

    @Mock private DeserializerRegistry registry;
    @Mock private OFDeserializer<ExperimenterDataOfChoice> vendorDeserializer;

    /**
     * Initializes deserializer registry and lookups OF 13 deserializer.
     */
    public MultipartReplyExperimenterTest() {
        super(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 19, MultipartReplyMessage.class));
    }

    /**
     * Testing {@link MultipartReplyMessageFactory} for correct translation into POJO.
     */
    @Test
    public void testMultipartReplyExperimenter() {
        Mockito.when(registry.getDeserializer(Matchers.<MessageCodeKey>any())).thenReturn(vendorDeserializer);
        ((DeserializerRegistryInjector)factory).injectDeserializerRegistry(registry);
        ByteBuf bb = BufferHelper.buildBuffer("FF FF 00 01 00 00 00 00 "
                                            + "00 00 00 01 00 00 00 02"); // expID, expType
        MultipartReplyMessage builtByFactory = BufferHelper.deserialize(factory, bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", 65535, builtByFactory.getType().getIntValue());
        Assert.assertEquals("Wrong flag", true, builtByFactory.getFlags().isOFPMPFREQMORE());

        Mockito.verify(vendorDeserializer).deserialize(bb);
    }
}