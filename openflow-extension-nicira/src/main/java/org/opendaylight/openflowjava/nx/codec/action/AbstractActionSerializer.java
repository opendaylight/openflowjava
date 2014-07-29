/**
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.nx.codec.action;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.nx.NiciraConstants;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;

/**
 * @author msunal
 *
 */
public abstract class AbstractActionSerializer implements OFSerializer<Action> {

    protected final static void serializeHeader(int msgLength, int subtype, ByteBuf outBuffer) {
        outBuffer.writeShort(EncodeConstants.EXPERIMENTER_VALUE);
        writeMsgLengthVendorIdSubtypeToBuffer(msgLength, subtype, outBuffer);
    }

    private final static void writeMsgLengthVendorIdSubtypeToBuffer(int msgLength, int subtype, ByteBuf outBuffer) {
        outBuffer.writeShort(msgLength);
        outBuffer.writeInt(NiciraConstants.NX_VENDOR_ID.intValue());
        outBuffer.writeShort(subtype);
    }

}
