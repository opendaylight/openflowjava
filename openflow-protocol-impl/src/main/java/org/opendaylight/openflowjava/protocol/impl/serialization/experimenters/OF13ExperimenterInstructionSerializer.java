package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterInstruction;

public class OF13ExperimenterInstructionSerializer 
		implements OFSerializer<ExperimenterInstruction> {

	@Override
	public void serialize(ExperimenterInstruction input, ByteBuf outBuffer) {
		if (input.getData() != null) {
			outBuffer.writeBytes(input.getData());
		}
	}

}
