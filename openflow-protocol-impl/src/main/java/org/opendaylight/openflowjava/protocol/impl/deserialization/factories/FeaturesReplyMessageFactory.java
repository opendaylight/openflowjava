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

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.Capabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutputBuilder;

/**
 * Translates FeaturesReply messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class FeaturesReplyMessageFactory implements OFDeserializer<GetFeaturesOutput>{

    private static final byte PADDING_IN_FEATURES_REPLY_HEADER = 2;

    @Override
    public GetFeaturesOutput deserialize(ByteBuf rawMessage, short version) {
        GetFeaturesOutputBuilder builder = new GetFeaturesOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        byte[] datapathId = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        rawMessage.readBytes(datapathId);
        builder.setDatapathId(new BigInteger(1, datapathId));
        builder.setBuffers(rawMessage.readUnsignedInt());
        builder.setTables(rawMessage.readUnsignedByte());
        builder.setAuxiliaryId(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        builder.setCapabilities(createCapabilities(rawMessage.readUnsignedInt()));
        builder.setReserved(rawMessage.readUnsignedInt());
        return builder.build();
    }

    private static Capabilities createCapabilities(long input) {
        final Boolean FLOW_STATS = (input & (1 << 0)) != 0;
        final Boolean TABLE_STATS = (input & (1 << 1)) != 0;
        final Boolean PORT_STATS = (input & (1 << 2)) != 0;
        final Boolean GROUP_STATS = (input & (1 << 3)) != 0;
        final Boolean IP_REASM = (input & (1 << 5)) != 0;
        final Boolean QUEUE_STATS = (input & (1 << 6)) != 0;
        final Boolean PORT_BLOCKED = (input & (1 << 8)) != 0;
        return new Capabilities(FLOW_STATS, GROUP_STATS, IP_REASM,
                PORT_BLOCKED, PORT_STATS, QUEUE_STATS, TABLE_STATS);
    }

}
