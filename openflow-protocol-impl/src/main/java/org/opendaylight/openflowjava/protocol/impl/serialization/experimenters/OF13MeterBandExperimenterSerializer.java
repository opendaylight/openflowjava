/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.experimenter._case.MeterBandExperimenter;

public class OF13MeterBandExperimenterSerializer
		implements OFSerializer<MeterBandExperimenter> {

	@Override
	public void serialize(MeterBandExperimenter meterBand, ByteBuf outBuffer) {
		int startIndex = outBuffer.writerIndex();
		outBuffer.writeShort(meterBand.getType().getIntValue());
		int lengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeInt(meterBand.getRate().intValue());
        outBuffer.writeInt(meterBand.getBurstSize().intValue());
        outBuffer.writeInt(meterBand.getExperimenter().intValue());
        if (meterBand.getData() != null) {
        	outBuffer.writeBytes(meterBand.getData());
        }
        outBuffer.setShort(lengthIndex, outBuffer.writerIndex() - startIndex);
	}

}
