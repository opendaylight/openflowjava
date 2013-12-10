/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Capabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class FeaturesReplyMessageFactoryTest {

    /**
     * Testing {@link FeaturesReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("00 01 02 03 04 05 06 07 00 01 02 03 01 01 00 00 00"
                + " 00 01 41 00 01 02 03");
        GetFeaturesOutput builtByFactory = BufferHelper.decodeV13(
                FeaturesReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong datapathId", 0x0001020304050607L, builtByFactory.getDatapathId().longValue());
        Assert.assertEquals("Wrong buffers", 0x00010203L, builtByFactory.getBuffers().longValue());
        Assert.assertEquals("Wrong number of tables", 0x01, builtByFactory.getTables().shortValue());
        Assert.assertEquals("Wrong auxiliaryId", 0x01, builtByFactory.getAuxiliaryId().shortValue());
        Assert.assertEquals("Wrong capabilities", new Capabilities(true, false, false, true, false, true, false), builtByFactory.getCapabilities());
        Assert.assertEquals("Wrong reserved", 0x00010203L, builtByFactory.getReserved().longValue());
    }
}
