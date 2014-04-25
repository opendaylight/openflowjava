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
import org.opendaylight.openflowjava.protocol.impl.util.CodingUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlagsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author michal.polkorab
 */
public class OF10FlowModInputMessageFactory implements OFSerializer<FlowModInput>, SerializerRegistryInjector {

    private static final byte MESSAGE_TYPE = 14;
    private SerializerRegistry registry;

    @Override
    public void serialize(FlowModInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        OFSerializer<MatchV10> matchSerializer = registry.getSerializer(new MessageTypeKey<>(
                message.getVersion(), MatchV10.class));
        matchSerializer.serialize(message.getMatchV10(), outBuffer);
        outBuffer.writeLong(message.getCookie().longValue());
        outBuffer.writeShort(message.getCommand().getIntValue());
        outBuffer.writeShort(message.getIdleTimeout().intValue());
        outBuffer.writeShort(message.getHardTimeout().intValue());
        outBuffer.writeShort(message.getPriority());
        outBuffer.writeInt(message.getBufferId().intValue());
        outBuffer.writeShort(message.getOutPort().getValue().intValue());
        outBuffer.writeShort(createFlowModFlagsBitmask(message.getFlagsV10()));
        CodingUtils.serializeOF10Actions(message.getAction(), registry, outBuffer);
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
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
    }
}
