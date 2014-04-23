package org.opendaylight.openflowjava.protocol.impl.serialization.experimenters;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;

public class OF13MultipartExperimenterSerializer 
		implements OFSerializer<MultipartRequestExperimenter> {

	@Override
	public void serialize(MultipartRequestExperimenter input, ByteBuf outBuffer) {
		outBuffer.writeInt(input.getExperimenter().intValue());
		outBuffer.writeInt(input.getExpType().intValue());
        if (input.getData() != null) {
        	outBuffer.writeBytes(input.getData());
        }
	}

}
