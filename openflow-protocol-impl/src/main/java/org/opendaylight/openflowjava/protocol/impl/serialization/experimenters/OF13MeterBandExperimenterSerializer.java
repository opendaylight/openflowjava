package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
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
