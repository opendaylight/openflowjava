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

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowModInputMessageFactory implements OFSerializer<FlowModInput> {
    private static final byte MESSAGE_TYPE = 14;
    private static final byte PADDING_IN_FLOW_MOD_MESSAGE = 2;
    private static final int MESSAGE_LENGTH = 48;
    private static FlowModInputMessageFactory instance;
   
    private FlowModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized FlowModInputMessageFactory getInstance() {
        if(instance == null) {
            instance = new FlowModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, FlowModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeLong(message.getCookie().longValue());
        out.writeLong(message.getCookieMask().longValue());
        out.writeByte(message.getTableId().getValue().byteValue());
        out.writeByte(message.getCommand().getIntValue());
        out.writeShort(message.getIdleTimeout().intValue());
        out.writeShort(message.getHardTimeout().intValue());
        out.writeShort(message.getPriority());
        out.writeInt(message.getBufferId().intValue());
        out.writeInt(message.getOutPort().getValue().intValue());
        out.writeInt(message.getOutGroup().intValue());
        out.writeShort(createFlowModFlagsBitmask(message.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_FLOW_MOD_MESSAGE, out);
        MatchSerializer.encodeMatch(message.getMatch(), out);
        InstructionsSerializer.encodeInstructions(message.getInstruction(), out);
    }

    @Override
    public int computeLength(FlowModInput message) {
        return MESSAGE_LENGTH + MatchSerializer.computeMatchLength(message.getMatch())
                + InstructionsSerializer.computeInstructionsLength(message.getInstruction());
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
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
