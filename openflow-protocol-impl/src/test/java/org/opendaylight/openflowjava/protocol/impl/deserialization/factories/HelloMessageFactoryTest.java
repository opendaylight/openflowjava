/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactory}.
 * @author michal.polkorab
 * @author timotej.kubas
 * @author madamjak
 */
public class HelloMessageFactoryTest {

    private OFDeserializer<HelloMessage> helloFactory;
    private DeserializerRegistry registry;

    /**
     * Initializes deserializer registry and lookups deserializer for OFP v1.3.
     */
    @Before
    public void startUp() {
        registry = new DeserializerRegistryImpl();
        registry.init();
        helloFactory = registry.getDeserializer(
                new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 0, HelloMessage.class));
    }

    /**
     * Testing {@link HelloMessageFactory} for correct version.
     */
    @Test
    public void testVersion() {
        testVersion(EncodeConstants.OF13_VERSION_ID);
        testVersion(EncodeConstants.OF14_VERSION_ID);
        testVersion(EncodeConstants.OF15_VERSION_ID);
    }

    private void testVersion(short version) {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 " // type
                                            + "00 08 " // length
                                            + "00 00 00 11" // bitmap 1
        );

        helloFactory = registry.getDeserializer(new MessageCodeKey(version, 0, HelloMessage.class));
        HelloMessage builtByFactory13 = BufferHelper.deserialize(helloFactory, bb);
        BufferHelper.checkHeader(builtByFactory13, version);
    }

    /**
     * Testing {@link HelloMessageFactory} for correct length without padding.
     */
    @Test
    public void testWithoutPadding() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 " // type
                                            + "00 08 " // length
                                            + "00 00 00 11" // bitmap 1
                                            );
        HelloMessage builtByFactory = BufferHelper.deserialize(helloFactory, bb);
        List<Elements> element = createElement(4,HelloElementType.VERSIONBITMAP.getIntValue());
        Assert.assertEquals("Wrong type", element.get(0).getType(), builtByFactory.getElements().get(0).getType());
        Assert.assertEquals("Wrong versionBitmap", element.get(0).getVersionBitmap(), builtByFactory.getElements().get(0).getVersionBitmap());
    }

    /**
     * Testing {@link HelloMessageFactory} for correct length with padding.
     */
    @Test
    public void testWithPadding() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 " // type31.10.2016
                                            + "00 0c " // length
                                            + "00 00 00 11 " // bitmap 1
                                            + "00 00 00 00 " // bitmap 2
                                            + "00 00 00 00"  // padding
                                            );
        HelloMessage builtByFactory = BufferHelper.deserialize(helloFactory, bb);
        List<Elements> element = createElement(8,HelloElementType.VERSIONBITMAP.getIntValue());
        Assert.assertEquals("Wrong type", element.get(0).getType(), builtByFactory.getElements().get(0).getType());
        Assert.assertEquals("Wrong versionBitmap", element.get(0).getVersionBitmap(), builtByFactory.getElements().get(0).getVersionBitmap());
    }

    /**
     * Testing {@link HelloMessageFactory} if incorrect version is set.
     */
    @Test
    public void testBadType(){
        ByteBuf bb = BufferHelper.buildBuffer("00 02 " // type
                                            + "00 0c " // length
                                            + "00 00 00 11 " // bitmap 1
                                            + "00 00 00 00 " // bitmap 2
                                            + "00 00 00 00"  // padding
                                            );
        HelloMessage builtByFactory = BufferHelper.deserialize(helloFactory, bb);
        Assert.assertEquals("Wrong - no element has been expected", 0, builtByFactory.getElements().size());
    }

    private static List<Elements> createElement(int lengthInByte, int type) {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<>();
        List<Boolean> booleanList = new ArrayList<>();
        booleanList.add(true);
        booleanList.add(false);
        booleanList.add(false);
        booleanList.add(false);
        booleanList.add(true);
        int inSize = booleanList.size();
        for (int i = 1; i < ((lengthInByte * 8) - inSize + 1); i++) {
            booleanList.add(false);
        }
        elementsBuilder.setType(HelloElementType.forValue(type));
        elementsBuilder.setVersionBitmap(booleanList);
        elementsList.add(elementsBuilder.build());
        return elementsList;
    }
}
