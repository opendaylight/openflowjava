/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.CodingUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlagsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author michal.polkorab
 */
public class OF10FlowModInputMessageFactory implements OFSerializer<FlowModInput> {

    private static final byte MESSAGE_TYPE = 14;
    private SerializerTable serializerTable;

    @Override
    public void serialize(FlowModInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        serializerTable.getSerializer(new MessageTypeKey<>(object.getVersion(), MatchV10.class))
            .serialize(object.getMatchV10(), outBuffer);
        outBuffer.writeLong(object.getCookie().longValue());
        outBuffer.writeShort(object.getCommand().getIntValue());
        outBuffer.writeShort(object.getIdleTimeout().intValue());
        outBuffer.writeShort(object.getHardTimeout().intValue());
        outBuffer.writeShort(object.getPriority());
        outBuffer.writeInt(object.getBufferId().intValue());
        outBuffer.writeShort(object.getOutPort().getValue().intValue());
        outBuffer.writeShort(createFlowModFlagsBitmask(object.getFlagsV10()));
        OFSerializer<Action> actionSerializer = serializerTable.getSerializer(
                new MessageTypeKey<>(object.getVersion(), Action.class));
        CodingUtils.serializeList(object.getAction(), actionSerializer, outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    private static int createFlowModFlagsBitmask(FlowModFlagsV10 flags) {
        int flowModFlagBitmask = 0;
        Map<Integer, Boolean> flowModFlagsMap = new HashMap<>();
        flowModFlagsMap.put(0, flags.isOFPFFSENDFLOWREM());
        flowModFlagsMap.put(1, flags.isOFPFFCHECKOVERLAP());
        flowModFlagsMap.put(2, flags.isOFPFFEMERG());
        flowModFlagBitmask = ByteBufUtils.fillBitMaskFromMap(flowModFlagsMap);
        return flowModFlagBitmask;
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }
}
