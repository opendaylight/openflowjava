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
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloElementType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.Elements;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.hello.ElementsBuilder;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class HelloMessageFactoryTest {

    /** Number of currently supported version / codec */
    public static final Short VERSION_YET_SUPPORTED = 0x04;
    private OFDeserializer<HelloMessage> helloFactory;

    /**
     * Initializes deserializer registry and lookups correct deserializer
     */
    @Before
    public void startUp() {
        DeserializerRegistry registry = new DeserializerRegistryImpl();
        registry.init();
        helloFactory = registry.getDeserializer(
                new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 0, HelloMessage.class));
    }

    /**
     * Testing {@link HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 " // type
                                            + "00 0c " // length
                                            + "00 00 00 11 " // bitmap 1
                                            + "00 00 00 00 " // bitmap 2
                                            + "00 00 00 00"  // padding
                );
        HelloMessage builtByFactory = BufferHelper.decodeV13(helloFactory, bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        List<Elements> element = createElement();
        Assert.assertEquals("Wrong type", element.get(0).getType(), builtByFactory.getElements().get(0).getType());
        Assert.assertEquals("Wrong versionBitmap", element.get(0).getVersionBitmap(), builtByFactory.getElements().get(0).getVersionBitmap());
    }
    
    private static List<Elements> createElement() {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<>();
        List<Boolean> booleanList = new ArrayList<>();
        booleanList.add(true);
        booleanList.add(false);
        booleanList.add(false);
        booleanList.add(false);
        booleanList.add(true);
        for (int i = 1; i < 60; i++) {
            booleanList.add(false);
        }
        elementsBuilder.setType(HelloElementType.forValue(1));
        elementsBuilder.setVersionBitmap(booleanList);
        elementsList.add(elementsBuilder.build());
        
        return elementsList;
    }
}
