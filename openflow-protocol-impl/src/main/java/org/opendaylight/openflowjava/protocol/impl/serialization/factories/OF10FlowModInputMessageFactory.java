/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.OF10ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlagsV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;

/**
 * Translates FlowMod messages
 * @author michal.polkorab
 */
public class OF10FlowModInputMessageFactory implements OFSerializer<FlowModInput> {

    private static final byte MESSAGE_TYPE = 14;
    private static final int MESSAGE_LENGTH = 72;
    
    private static OF10FlowModInputMessageFactory instance;
   
    private OF10FlowModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10FlowModInputMessageFactory getInstance() {
        if(instance == null) {
            instance = new OF10FlowModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, FlowModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        OF10MatchSerializer.encodeMatchV10(out, message.getMatchV10());
        out.writeLong(message.getCookie().longValue());
        out.writeShort(message.getCommand().getIntValue());
        out.writeShort(message.getIdleTimeout().intValue());
        out.writeShort(message.getHardTimeout().intValue());
        out.writeShort(message.getPriority());
        out.writeInt(message.getBufferId().intValue());
        out.writeShort(message.getOutPort().getValue().intValue());
        out.writeShort(createFlowModFlagsBitmask(message.getFlagsV10()));
        OF10ActionsSerializer.encodeActionsV10(out, message.getActionsList());
    }

    @Override
    public int computeLength(FlowModInput message) {
        return MESSAGE_LENGTH + OF10ActionsSerializer.computeActionsLength(message.getActionsList());
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
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
}
