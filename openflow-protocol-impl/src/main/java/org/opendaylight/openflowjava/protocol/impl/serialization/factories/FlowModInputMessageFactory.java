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
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.EnhancedTypeKeyMakerFactory;
import org.opendaylight.openflowjava.protocol.impl.util.ListSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowModInputMessageFactory implements OFSerializer<FlowModInput>, SerializerRegistryInjector {
    private static final byte MESSAGE_TYPE = 14;
    private static final byte PADDING_IN_FLOW_MOD_MESSAGE = 2;
    private SerializerRegistry registry;

    @Override
    public void serialize(FlowModInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeLong(message.getCookie().longValue());
        outBuffer.writeLong(message.getCookieMask().longValue());
        outBuffer.writeByte(message.getTableId().getValue().byteValue());
        outBuffer.writeByte(message.getCommand().getIntValue());
        outBuffer.writeShort(message.getIdleTimeout().intValue());
        outBuffer.writeShort(message.getHardTimeout().intValue());
        outBuffer.writeShort(message.getPriority());
        outBuffer.writeInt(message.getBufferId().intValue());
        outBuffer.writeInt(message.getOutPort().getValue().intValue());
        outBuffer.writeInt(message.getOutGroup().intValue());
        outBuffer.writeShort(createFlowModFlagsBitmask(message.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_FLOW_MOD_MESSAGE, outBuffer);
        registry.<Match, OFSerializer<Match>>getSerializer(new MessageTypeKey<>(message.getVersion(), Match.class))
            .serialize(message.getMatch(), outBuffer);
        ListSerializer.serializeList(message.getInstruction(), EnhancedTypeKeyMakerFactory
                .createInstructionKeyBuilder(EncodeConstants.OF13_VERSION_ID), registry, outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
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
