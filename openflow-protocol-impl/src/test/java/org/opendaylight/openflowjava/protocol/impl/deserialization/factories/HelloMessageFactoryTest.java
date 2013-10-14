/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
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

    /**
     * Testing {@link HelloMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 "+ //type
                                              "00 00 00 11 "+//booelan element 1
                                              "00 00 00 11"//booelan element 2
                );
        HelloMessage builtByFactory = BufferHelper.decodeV13(
                HelloMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong type", createElement().get(0).getType().getIntValue(), builtByFactory.getElements().get(0).getType().getIntValue());
    }
    
    private static List<Elements> createElement() {
        ElementsBuilder elementsBuilder = new ElementsBuilder();
        List<Elements> elementsList = new ArrayList<Elements>();
        List<Boolean> booleanList = new ArrayList<Boolean>();
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
