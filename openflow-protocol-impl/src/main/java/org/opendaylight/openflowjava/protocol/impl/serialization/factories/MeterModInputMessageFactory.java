/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterBandCommons;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.MeterBand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDropCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemarkCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.drop._case.MeterBandDrop;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.dscp.remark._case.MeterBandDscpRemark;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.mod.Bands;

/**
 * Translates MeterMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MeterModInputMessageFactory implements OFSerializer<MeterModInput> {
    private static final byte MESSAGE_TYPE = 29;
    private static final short LENGTH_OF_METER_BANDS = 16;
    private static final short PADDING_IN_METER_BAND_DROP = 4;
    private static final short PADDING_IN_METER_BAND_DSCP_REMARK = 3;

    @Override
    public void serialize(MeterModInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeShort(message.getCommand().getIntValue());
        outBuffer.writeShort(createMeterFlagsBitmask(message.getFlags()));
        outBuffer.writeInt(message.getMeterId().getValue().intValue());
        serializeBands(message.getBands(), outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
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
    
    private static void serializeBands(List<Bands> bands, ByteBuf outBuffer) {
        if (bands != null) {
            for (Bands currentBand : bands) {
                MeterBand meterBand = currentBand.getMeterBand();
                if (meterBand instanceof MeterBandDropCase) {
                    MeterBandDropCase dropBandCase = (MeterBandDropCase) meterBand;
                    MeterBandDrop dropBand = dropBandCase.getMeterBandDrop();
                    writeBandCommonFields(dropBand, outBuffer);
                    ByteBufUtils.padBuffer(PADDING_IN_METER_BAND_DROP, outBuffer);
                } else if (meterBand instanceof MeterBandDscpRemarkCase) {
                    MeterBandDscpRemarkCase dscpRemarkBandCase = (MeterBandDscpRemarkCase) meterBand;
                    MeterBandDscpRemark dscpRemarkBand = dscpRemarkBandCase.getMeterBandDscpRemark();
                    writeBandCommonFields(dscpRemarkBand, outBuffer);
                    outBuffer.writeByte(dscpRemarkBand.getPrecLevel());
                    ByteBufUtils.padBuffer(PADDING_IN_METER_BAND_DSCP_REMARK, outBuffer);
                } else if (meterBand instanceof MeterBandExperimenterCase) {
                    MeterBandExperimenterCase experimenterBandCase = (MeterBandExperimenterCase) meterBand;
                    MeterBandExperimenter experimenterBand = experimenterBandCase.getMeterBandExperimenter();
                    writeBandCommonFields(experimenterBand, outBuffer);
                    outBuffer.writeInt(experimenterBand.getExperimenter().intValue());
                }
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
