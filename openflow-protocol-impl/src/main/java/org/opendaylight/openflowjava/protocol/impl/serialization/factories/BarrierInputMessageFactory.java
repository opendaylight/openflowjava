/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;

/**
 * Translates BarrierRequest messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class BarrierInputMessageFactory implements OFSerializer<BarrierInput> {

    /** Code type of BarrierRequest message */
    public static final byte MESSAGE_TYPE = 20;

    @Override
    public void serialize(BarrierInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.OFHEADER_SIZE);
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        // do nothing - no need for table in this factory
    }

}
