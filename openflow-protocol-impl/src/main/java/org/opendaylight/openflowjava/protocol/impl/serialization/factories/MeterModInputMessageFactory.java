/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.mod.Bands;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MeterModInputMessageFactory implements OFSerializer<MeterModInput> {
    private static final byte MESSAGE_TYPE = 29;
    private static final int MESSAGE_LENGTH = 16; 
    private static MeterModInputMessageFactory instance;
    
    private MeterModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static MeterModInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new MeterModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            MeterModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getCommand().getIntValue());
        out.writeShort(createMeterFlagsBitmask(message.getFlags()));
        out.writeInt(message.getMeterId().getValue().intValue());
        encodeBands(message.getBands(), out);
    }

    @Override
    public int computeLength() {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

    private static int createMeterFlagsBitmask(MeterFlags flags) {
        int meterFlagBitmask = 0;
        Map<Integer, Boolean> meterModFlagsMap = new HashMap<>();
        meterModFlagsMap.put(0, flags.isOFPMFKBPS());
        meterModFlagsMap.put(1, flags.isOFPMFPKTPS());
        meterModFlagsMap.put(2, flags.isOFPMFBURST());
        meterModFlagsMap.put(3, flags.isOFPMFSTATS());
        
        meterFlagBitmask = ByteBufUtils.fillBitMaskFromMap(meterModFlagsMap);
        return meterFlagBitmask;
    }
    
    private static void encodeBands(List<Bands> bands, ByteBuf outBuffer) {
        for (Iterator<Bands> iterator = bands.iterator(); iterator.hasNext();) {
            Bands currentBands = iterator.next();
            outBuffer.writeShort(currentBands.getType().getIntValue());
            // TODO outBuffer.writeShort(currentBands.get); length is missing
            outBuffer.writeInt(currentBands.getRate().intValue());
            outBuffer.writeInt(currentBands.getBurstSize().intValue());
            // TODO what to do with ofp_meter_band_drop?
        }
    }
    
}
