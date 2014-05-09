/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.ext.serialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.ext.util.ExtBufferUtils;
import org.opendaylight.openflowjava.protocol.ext.util.ExtConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * Translates Vendor messages
 * @author michal.polkorab
 */
public class OF10VendorInputMessageFactory implements OFSerializer<ExperimenterInput> {

    private static final byte MESSAGE_TYPE = 4;

    @Override
    public void serialize(ExperimenterInput input, ByteBuf outBuffer) {
        ExtBufferUtils.writeOFHeader(MESSAGE_TYPE, input, outBuffer, ExtConstants.EMPTY_LENGTH);
        outBuffer.writeInt(input.getExperimenter().intValue());
        byte[] data = input.getData();
        if (data != null) {
            outBuffer.writeBytes(data);
        }
        ExtBufferUtils.updateOFHeaderLength(outBuffer);
    }

}