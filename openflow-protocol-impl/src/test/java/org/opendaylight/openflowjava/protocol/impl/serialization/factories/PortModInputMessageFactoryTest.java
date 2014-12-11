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
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.OpenflowUtils;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfigV13;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeaturesV13;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class PortModInputMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 16;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_01 = 4;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_02 = 2;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_03 = 4;
    private static final int MESSAGE_LENGTH = 40;
    private SerializerRegistry registry;
    private OFSerializer<PortModInput> portModFactory;

    /**
     * Initializes serializer registry and stores correct factory in field
     */
    @Before
    public void startUp() {
        registry = new SerializerRegistryImpl();
        registry.init();
        portModFactory = registry.getSerializer(
                new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, PortModInput.class));
    }

    /**
     * Testing of {@link PortModInputMessageFactory} for correct translation from POJO
     * @throws Exception
     */
    @Test
    public void testPortModInput() throws Exception {
        PortModInputBuilder builder = new PortModInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setPortNo(new PortNumber(9L));
        builder.setHwAddress(new MacAddress("08:00:27:00:B0:EB"));
        builder.setConfig(new PortConfig(new PortConfigV13(true, false, true, false)));
        builder.setMask(new PortConfig(new PortConfigV13(false, true, false, true)));
        builder.setAdvertise(new PortFeatures(new PortFeaturesV13(true, false, false, false,
                                              false, false, false, true,
                                              false, false, false, false,
                                              false, false, false, false)));
        PortModInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        portModFactory.serialize(message, out);

        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, MESSAGE_LENGTH);
        Assert.assertEquals("Wrong PortNo", message.getPortNo().getValue().longValue(), out.readUnsignedInt());
        out.skipBytes(PADDING_IN_PORT_MOD_MESSAGE_01);
        byte[] address = new byte[6];
        out.readBytes(address);
        Assert.assertEquals("Wrong MacAddress", message.getHwAddress().getValue(),
                new MacAddress(ByteBufUtils.macAddressToString(address)).getValue());
        out.skipBytes(PADDING_IN_PORT_MOD_MESSAGE_02);
        Assert.assertEquals("Wrong config", message.getConfig().getPortConfigV13(), OpenflowUtils
                .createPortConfig(out.readInt(), EncodeConstants.OF13_VERSION_ID).getPortConfigV13());
        Assert.assertEquals("Wrong mask", message.getMask().getPortConfigV13(), OpenflowUtils
                .createPortConfig(out.readInt(), EncodeConstants.OF13_VERSION_ID).getPortConfigV13());
        Assert.assertEquals("Wrong advertise", message.getAdvertise().getPortFeaturesV13(), OpenflowUtils
                .createPortFeatures(out.readInt(), EncodeConstants.OF13_VERSION_ID).getPortFeaturesV13());
        out.skipBytes(PADDING_IN_PORT_MOD_MESSAGE_03);
    }

}
