/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenterBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF13MeterBandExperimenterSerializerTest {

    /**
     * Testing of {@link OF13MeterBandExperimenterSerializer} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void test() {
        MeterBandExperimenterBuilder builder = new MeterBandExperimenterBuilder();
        builder.setType(MeterBandType.OFPMBTEXPERIMENTER);
        builder.setRate(128L);
        builder.setBurstSize(256L);
        builder.setExperimenter(512L);
        MeterBandExperimenter band = builder.build();

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        OF13MeterBandExperimenterSerializer serializer = new OF13MeterBandExperimenterSerializer();
        serializer.serialize(band, buffer);

        Assert.assertEquals("Wrong type", 65535, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong type", 16, buffer.readUnsignedShort());
        Assert.assertEquals("Wrong type", 128, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong type", 256, buffer.readUnsignedInt());
        Assert.assertEquals("Wrong type", 512, buffer.readUnsignedInt());
        Assert.assertTrue("Unexpected data", buffer.readableBytes() == 0);
    }

}
