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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;

/**
 * @author michal.polkorab
 *
 */
public class ExperimenterMessageFactoryTest {

    /**
     * Testing {@link ExperimenterMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer("01 02 03 04 01 02 03 04");
        ExperimenterMessage builtByFactory = BufferHelper.decodeV13(
                ExperimenterMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong experimenter", 0x01020304L, builtByFactory.getExperimenter().longValue());
        Assert.assertEquals("Wrong expType", 0x01020304L, builtByFactory.getExpType().longValue());
    }

}
