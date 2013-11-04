/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterBandCommons;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.MeterBand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDrop;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemark;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.mod.Bands;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MeterModInputMessageFactory implements OFSerializer<MeterModInput> {
    private static final byte MESSAGE_TYPE = 29;
    private static final int MESSAGE_LENGTH = 16;
    private static final short LENGTH_OF_METER_BANDS = 16;
    private static final short PADDING_IN_METER_BAND_DROP = 4;
    private static final short PADDING_IN_METER_BAND_DSCP_REMARK = 3;
    private static MeterModInputMessageFactory instance;
    
    private MeterModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized MeterModInputMessageFactory getInstance() {
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
    public int computeLength(MeterModInput message) {
        
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
        for (Bands currentBand : bands) {
            MeterBand meterBand = currentBand.getMeterBand();
            writeBandCommonFields((MeterBandCommons) meterBand, outBuffer);
            if (meterBand instanceof MeterBandDrop) {
                ByteBufUtils.padBuffer(PADDING_IN_METER_BAND_DROP, outBuffer);
            } else if (meterBand instanceof MeterBandDscpRemark) {
                MeterBandDscpRemark dscpRemarkBand = (MeterBandDscpRemark) meterBand;
                outBuffer.writeByte(dscpRemarkBand.getPrecLevel());
                ByteBufUtils.padBuffer(PADDING_IN_METER_BAND_DSCP_REMARK, outBuffer);
            } else if (meterBand instanceof MeterBandExperimenter) {
                MeterBandExperimenter experimenterBand = (MeterBandExperimenter) meterBand;
                outBuffer.writeInt(experimenterBand.getExperimenter().intValue());
            }
        }
    }
    
    private static void writeBandCommonFields(MeterBandCommons meterBand, ByteBuf outBuffer) {
        outBuffer.writeShort(meterBand.getType().getIntValue());
        outBuffer.writeShort(LENGTH_OF_METER_BANDS);
        outBuffer.writeInt(meterBand.getRate().intValue());
        outBuffer.writeInt(meterBand.getBurstSize().intValue());
    }
    
}
