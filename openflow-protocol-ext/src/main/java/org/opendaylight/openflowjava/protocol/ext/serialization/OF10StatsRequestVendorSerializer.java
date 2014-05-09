/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;

/**
 * @author michal.polkorab
 *
 */
public class OF10StatsRequestVendorSerializer implements OFSerializer<MultipartRequestExperimenter>{

    @Override
    public void serialize(MultipartRequestExperimenter input, ByteBuf outBuffer) {
        outBuffer.writeInt(input.getExperimenter().intValue());
        if (input.getData() != null) {
            outBuffer.writeBytes(input.getData());
        }
    }

}
