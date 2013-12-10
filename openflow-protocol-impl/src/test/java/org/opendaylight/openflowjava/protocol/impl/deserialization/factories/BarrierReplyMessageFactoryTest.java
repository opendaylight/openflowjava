/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;

/**
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class BarrierReplyMessageFactoryTest {

    /**
     * Testing of {@link BarrierReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test() {
        ByteBuf bb = BufferHelper.buildBuffer();
        BarrierOutput builtByFactory = BufferHelper.decodeV13(
                BarrierReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
    }
    
    /**
     * Testing of {@link BarrierReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void testV10() {
        ByteBuf bb = BufferHelper.buildBuffer();
        BarrierOutput builtByFactory = BufferHelper.decodeV10(
                BarrierReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV10(builtByFactory);
    }
}
