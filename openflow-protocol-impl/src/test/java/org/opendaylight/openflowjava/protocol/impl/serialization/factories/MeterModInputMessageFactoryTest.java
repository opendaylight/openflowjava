/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.mod.Bands;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.mod.BandsBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MeterModInputMessageFactoryTest {

    /**
     * @throws Exception 
     * Testing of {@link MeterModInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMeterModInputMessage() throws Exception {
        MeterModInputBuilder builder = new MeterModInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setCommand(MeterModCommand.forValue(1));
        builder.setFlags(new MeterFlags(false, true, true, false));
        builder.setMeterId(new MeterId(2248L));
        builder.setBands(createBandsList());
        MeterModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MeterModInputMessageFactory factory = MeterModInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength());
        Assert.assertEquals("Wrong meterModCommand", message.getCommand().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong meterFlags", message.getFlags(), decodeMeterModFlags(out.readShort()));
        Assert.assertEquals("Wrong meterId", message.getMeterId().getValue().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong bands", createBandsList(), decodeBandsList(out));
    }
    
    private static MeterFlags decodeMeterModFlags(short input){
        final Boolean _oFPMFKBPS = (input & (1 << 0)) > 0;
        final Boolean _oFPMFPKTPS = (input & (1 << 1)) > 0;
        final Boolean _oFPMFBURST = (input & (1 << 2)) > 0; 
        final Boolean _oFPMFSTATS = (input & (1 << 3)) > 0;
        return new MeterFlags(_oFPMFBURST, _oFPMFKBPS, _oFPMFPKTPS, _oFPMFSTATS);
    }
    
    private static List<Bands> createBandsList(){
        List<Bands> bandsList = new ArrayList<Bands>();
        BandsBuilder bandsBuilder = new BandsBuilder();
        Bands band;
        bandsBuilder.setType(MeterBandType.forValue(1));
        bandsBuilder.setRate(2254L);
        bandsBuilder.setBurstSize(12L);
        band = bandsBuilder.build();
        bandsList.add(band);
        return bandsList;
    }
    
    private static List<Bands> decodeBandsList(ByteBuf input){
        List<Bands> bandsList = new ArrayList<Bands>();
        BandsBuilder bandsBuilder = new BandsBuilder();
        Bands band;
        bandsBuilder.setType(MeterBandType.forValue(input.readShort()));
        bandsBuilder.setRate(input.readUnsignedInt());
        bandsBuilder.setBurstSize(input.readUnsignedInt());
        band = bandsBuilder.build();
        bandsList.add(band);
        return bandsList;
    }
}
