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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowModInputMessageFactory implements OFSerializer<FlowModInput> {
    private static final byte MESSAGE_TYPE = 14;
    private static final byte PADDING_IN_FLOW_MOD_MESSAGE = 2;
    private SerializerTable serializerTable;

    @Override
    public void serialize(FlowModInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeLong(object.getCookie().longValue());
        outBuffer.writeLong(object.getCookieMask().longValue());
        outBuffer.writeByte(object.getTableId().getValue().byteValue());
        outBuffer.writeByte(object.getCommand().getIntValue());
        outBuffer.writeShort(object.getIdleTimeout().intValue());
        outBuffer.writeShort(object.getHardTimeout().intValue());
        outBuffer.writeShort(object.getPriority());
        outBuffer.writeInt(object.getBufferId().intValue());
        outBuffer.writeInt(object.getOutPort().getValue().intValue());
        outBuffer.writeInt(object.getOutGroup().intValue());
        outBuffer.writeShort(createFlowModFlagsBitmask(object.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_FLOW_MOD_MESSAGE, outBuffer);
        serializerTable.getSerializer(new MessageTypeKey<>(object.getVersion(), Match.class))
            .serialize(object.getMatch(), outBuffer);
        OFSerializer<Instruction> instructionSerializer =
                serializerTable.getSerializer(new MessageTypeKey<>(object.getVersion(), Instruction.class));
        CodingUtils.serializeList(object.getInstruction(), instructionSerializer, outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }

    private static int createFlowModFlagsBitmask(FlowModFlags flags) {
        int flowModFlagBitmask = 0;
        Map<Integer, Boolean> flowModFlagsMap = new HashMap<>();
        flowModFlagsMap.put(0, flags.isOFPFFSENDFLOWREM());
        flowModFlagsMap.put(1, flags.isOFPFFCHECKOVERLAP());
        flowModFlagsMap.put(2, flags.isOFPFFRESETCOUNTS());
        flowModFlagsMap.put(3, flags.isOFPFFNOPKTCOUNTS());
        flowModFlagsMap.put(4, flags.isOFPFFNOBYTCOUNTS());
        
        flowModFlagBitmask = ByteBufUtils.fillBitMaskFromMap(flowModFlagsMap);
        return flowModFlagBitmask;
    }

}
