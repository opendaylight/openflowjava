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
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * Translates Experimenter messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class OF13ExperimenterInputMessageFactory implements OFSerializer<ExperimenterInput>{

    /** Code type of Experimenter message */
    public static final byte MESSAGE_TYPE = 4;

    @Override
    public void serialize(ExperimenterInput message, ByteBuf outBuffer) {
        ExtBufferUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, ExtConstants.EMPTY_LENGTH);
        outBuffer.writeInt(message.getExperimenter().intValue());
        outBuffer.writeInt(message.getExpType().intValue());
        byte[] data = message.getData();
        if (data != null) {
            outBuffer.writeBytes(data);
        }
        ExtBufferUtils.updateOFHeaderLength(outBuffer);
    }

}