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
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.DefaultDeserializerFactoryTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Capabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;

/**
 * Test for {@link org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FeaturesReplyMessageFactory}.
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class FeaturesReplyMessageFactoryTest extends DefaultDeserializerFactoryTest<GetFeaturesOutput>{

    /**
     * Initializes deserializer registry and lookups OF13 deserializer.
     */
    public FeaturesReplyMessageFactoryTest() {
        super(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 6, GetFeaturesOutput.class));
    }

    /**
     * Testing {@link EchoRequestMessageFactory} for correct header version.
     */
    @Test
    public void testVersions() {
        List<Byte> versions = new ArrayList<>(Arrays.asList(
                EncodeConstants.OF13_VERSION_ID,
                EncodeConstants.OF14_VERSION_ID,
                EncodeConstants.OF15_VERSION_ID
        ));
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00"
                                           + " 00 00 00 00 01 02 03");
        testHeaderVersions(versions, bb);
    }

    /**
     * Testing {@link FeaturesReplyMessageFactory} for correct translation into POJO.
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00"
                                           + " 00 00 00 00 01 02 03");
        GetFeaturesOutput builtByFactory = BufferHelper.deserialize(factory, bb);

        Assert.assertEquals("Wrong datapathId", 0x0001020304050607L, builtByFactory.getDatapathId().longValue());
        Assert.assertEquals("Wrong buffers", 0x00010203L, builtByFactory.getBuffers().longValue());
        Assert.assertEquals("Wrong number of tables", 0x01, builtByFactory.getTables().shortValue());
        Assert.assertEquals("Wrong auxiliaryId", 0x01, builtByFactory.getAuxiliaryId().shortValue());
        Assert.assertEquals("Wrong capabilities", new Capabilities(false, false, false, false, false, false, false), builtByFactory.getCapabilities());
        Assert.assertEquals("Wrong reserved", 0x00010203L, builtByFactory.getReserved().longValue());
    }

    /**
     * Testing {@link FeaturesReplyMessageFactory} for correct translation into POJO.
     * (capabilities set)
     */
    @Test
    public void testCapabilities() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00"
                                           + " 00 01 6F 00 01 02 03");
        GetFeaturesOutput builtByFactory = BufferHelper.deserialize(factory, bb);

        Assert.assertEquals("Wrong capabilities", new Capabilities(true, true, true, true, true, true, true), builtByFactory.getCapabilities());
    }
}