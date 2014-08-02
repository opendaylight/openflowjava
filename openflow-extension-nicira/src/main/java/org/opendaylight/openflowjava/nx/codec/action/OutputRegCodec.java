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
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.NxmNxOutputReg;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.OfjAugNxAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.output.reg.grouping.ActionOutputReg;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.extension.nicira.action.rev140421.ofj.nx.action.output.reg.grouping.ActionOutputRegBuilder;

/**
 * Codec for the Nicira OutputRegAction
 * @author readams
 */
public class OutputRegCodec extends AbstractActionSerializer implements OFDeserializer<ActionOutputReg> {
    public static final ExperimenterActionSerializerKey SERIALIZER_KEY = new ExperimenterActionSerializerKey(
            EncodeConstants.OF13_VERSION_ID, NiciraConstants.NX_VENDOR_ID, NxmNxOutputReg.class);
    public static final int LENGTH = 24;
    public static final byte SUBTYPE = 15; // NXAST_OUTPUT_REG

    @Override
    public void serialize(Action input, ByteBuf outBuffer) {
        ActionOutputReg action = input.getAugmentation(OfjAugNxAction.class).getActionOutputReg();
        serializeHeader(LENGTH, SUBTYPE, outBuffer);
        outBuffer.writeShort(action.getNBits().shortValue());
        outBuffer.writeInt(action.getSrc().intValue());
        outBuffer.writeShort(action.getMaxLen().shortValue());
        outBuffer.writeZero(6);
    }

    @Override
    public ActionOutputReg deserialize(ByteBuf message) {
        ActionOutputRegBuilder builder = new ActionOutputRegBuilder();
        builder.setNBits(message.readUnsignedShort());
        builder.setSrc(message.readUnsignedInt());
        builder.setMaxLen(message.readUnsignedShort());
        return builder.build();
    }

}
