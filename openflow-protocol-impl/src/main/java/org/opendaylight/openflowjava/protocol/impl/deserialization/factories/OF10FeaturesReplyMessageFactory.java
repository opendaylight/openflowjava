/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OpenflowUtils;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionTypeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.CapabilitiesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.features.reply.PhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.features.reply.PhyPortBuilder;

/**
 * Translates FeaturesReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10FeaturesReplyMessageFactory implements OFDeserializer<GetFeaturesOutput> {
    
    private static final byte PADDING_IN_FEATURES_REPLY_HEADER = 3;

    @Override
    public GetFeaturesOutput deserialize(ByteBuf rawMessage) {
        GetFeaturesOutputBuilder builder = new GetFeaturesOutputBuilder();
        builder.setVersion((short) EncodeConstants.OF10_VERSION_ID);
        builder.setXid(rawMessage.readUnsignedInt());
        byte[] datapathId = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        rawMessage.readBytes(datapathId);
        builder.setDatapathId(new BigInteger(1, datapathId));
        builder.setBuffers(rawMessage.readUnsignedInt());
        builder.setTables(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        builder.setCapabilitiesV10(createCapabilitiesV10(rawMessage.readUnsignedInt()));
        builder.setActionsV10(createActionsV10(rawMessage.readUnsignedInt()));
        List<PhyPort> ports = new ArrayList<>();
        while (rawMessage.readableBytes() > 0) {
            ports.add(deserializePort(rawMessage));
        }
        builder.setPhyPort(ports);
        return builder.build();
    }

    private static CapabilitiesV10 createCapabilitiesV10(long input) {
        final Boolean FLOW_STATS = (input & (1 << 0)) != 0;
        final Boolean TABLE_STATS = (input & (1 << 1)) != 0;
        final Boolean PORT_STATS = (input & (1 << 2)) != 0;
        final Boolean STP = (input & (1 << 3)) != 0;
        final Boolean RESERVED = (input & (1 << 4)) != 0;
        final Boolean IP_REASM = (input & (1 << 5)) != 0;
        final Boolean QUEUE_STATS = (input & (1 << 6)) != 0;
        final Boolean ARP_MATCH_IP = (input & (1 << 7)) != 0;
        return new CapabilitiesV10(ARP_MATCH_IP, FLOW_STATS, IP_REASM,
                PORT_STATS, QUEUE_STATS, RESERVED, STP, TABLE_STATS);
    }
    
    private static ActionTypeV10 createActionsV10(long input) {
        final Boolean OUTPUT = (input & (1 << 0)) != 0;
        final Boolean SET_VLAN_VID = (input & (1 << 1)) != 0;
        final Boolean SET_VLAN_PCP = (input & (1 << 2)) != 0;
        final Boolean STRIP_VLAN = (input & (1 << 3)) != 0;
        final Boolean SET_DL_SRC = (input & (1 << 4)) != 0;
        final Boolean SET_DL_DST = (input & (1 << 5)) != 0;
        final Boolean SET_NW_SRC = (input & (1 << 6)) != 0;
        final Boolean SET_NW_DST = (input & (1 << 7)) != 0;
        final Boolean SET_NW_TOS = (input & (1 << 8)) != 0;
        final Boolean SET_TP_SRC = (input & (1 << 9)) != 0;
        final Boolean SET_TP_DST = (input & (1 << 10)) != 0;
        final Boolean ENQUEUE = (input & (1 << 11)) != 0;
        final Boolean VENDOR = (input & (1 << 12)) != 0;
        return new ActionTypeV10(ENQUEUE, OUTPUT, SET_DL_DST, SET_DL_SRC,
                SET_NW_DST, SET_NW_SRC, SET_NW_TOS, SET_TP_DST, SET_TP_SRC,
                SET_VLAN_PCP, SET_VLAN_VID, STRIP_VLAN, VENDOR);
    }

    private static PhyPort deserializePort(ByteBuf rawMessage) {
        PhyPortBuilder builder = new PhyPortBuilder();
        builder.setPortNo((long) rawMessage.readUnsignedShort());
        byte[] address = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        rawMessage.readBytes(address);
        builder.setHwAddr(new MacAddress(ByteBufUtils.macAddressToString(address)));
        builder.setName(ByteBufUtils.decodeNullTerminatedString(rawMessage, EncodeConstants.MAX_PORT_NAME_LENGTH));
        builder.setConfigV10(OpenflowUtils.createPortConfig(rawMessage.readUnsignedInt()));
        builder.setStateV10(OpenflowUtils.createPortState(rawMessage.readUnsignedInt()));
        builder.setCurrentFeaturesV10(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setAdvertisedFeaturesV10(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setSupportedFeaturesV10(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setPeerFeaturesV10(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt()));
        return builder.build();
    }
}