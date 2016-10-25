/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.experimenter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.util.ExperimenterSerializerKeyFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundleControlType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundleFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundleId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.BundlePropertyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.bundle.properties.bundle.property.bundle.property.entry.bundle.experimenter.property.BundleExperimenterPropertyData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.BundleControl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.approved.extensions.rev160802.experimenter.input.experimenter.data.of.choice.BundleControlBuilder;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.serialization.experimenter.BundleControlFactory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BundleControlFactoryTest {

    private OFSerializer<BundleControl> factory;
    @Mock
    SerializerRegistry registry;
    @Mock
    OFSerializer<BundleExperimenterPropertyData> serializer;

    @Before
    public void setUp() throws Exception {
        SerializerRegistry registry = new SerializerRegistryImpl();
        registry.init();
        factory = registry.getSerializer(ExperimenterSerializerKeyFactory.createExperimenterMessageSerializerKey(
                EncodeConstants.OF13_VERSION_ID, EncodeConstants.ONF_EXPERIMENTER_ID, EncodeConstants.ONF_ET_BUNDLE_CONTROL));
    }

    @Test
    public void testSerializeWithoutProperties() {
        BundleControlBuilder builder = new BundleControlBuilder();
        builder.setBundleId(new BundleId(1L));
        builder.setType(BundleControlType.ONFBCTOPENREQUEST);
        builder.setFlags(new BundleFlags(true, true));

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        factory.serialize(builder.build(), out);

        Assert.assertEquals("Wrong bundle ID", 1L, out.readUnsignedInt());
        Assert.assertEquals("Wrong type", BundleControlType.ONFBCTOPENREQUEST.getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", 3, out.readUnsignedShort());
        Assert.assertTrue("Unexpected data", out.readableBytes() == 0);
    }

    @Test
    public void testSerializeWithExperimenterProperty() {
        BundleControlBuilder builder = new BundleControlBuilder();
        builder.setBundleId(new BundleId(3L));
        builder.setType(BundleControlType.ONFBCTCOMMITREQUEST);
        builder.setFlags(new BundleFlags(false, true));

        BundleExperimenterPropertyData data = AbstractBundleMessageFactoryTest.createBundleExperimenterPropertyData();
        builder.setBundleProperty(AbstractBundleMessageFactoryTest.createListWithBundleExperimenterProperty(data));

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        Mockito.when(registry.getSerializer(Matchers.any(MessageTypeKey.class))).thenReturn(serializer);
        ((SerializerRegistryInjector) factory).injectSerializerRegistry(registry);
        factory.serialize(builder.build(), out);

        Assert.assertEquals("Wrong bundle ID", 3L, out.readUnsignedInt());
        Assert.assertEquals("Wrong type", BundleControlType.ONFBCTCOMMITREQUEST.getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong property type", BundlePropertyType.ONFETBPTEXPERIMENTER.getIntValue(), out.readUnsignedShort());
        int length = out.readUnsignedShort();
        Assert.assertEquals("Wrong experimenter ID", 1, out.readUnsignedInt());
        Assert.assertEquals("Wrong experimenter type", 2, out.readUnsignedInt());
        Mockito.verify(serializer, Mockito.times(1)).serialize(data, out);
    }

}