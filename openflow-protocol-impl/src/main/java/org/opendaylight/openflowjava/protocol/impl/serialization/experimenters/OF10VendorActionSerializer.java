package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;

public class OF10VendorActionSerializer implements OFSerializer<ExperimenterAction> {

	@Override
	public void serialize(ExperimenterAction input, ByteBuf outBuffer) {
		if (input.getData() != null) {
			outBuffer.writeBytes(input.getData());
		}
	}
	
}
