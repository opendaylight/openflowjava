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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;

public class OF13ExperimenterActionSerializer implements OFSerializer<ExperimenterAction> {

	@Override
	public void serialize(ExperimenterAction input, ByteBuf outBuffer) {
		if (input.getData() != null) {
			outBuffer.writeBytes(input.getData());
		}
	}
	
}
