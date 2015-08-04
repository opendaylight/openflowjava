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
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.VersatileFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PacketInReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessageBuilder;

/**
 * Translates PacketIn messages
 *
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PacketInMessageFactory extends VersatileFactory implements OFDeserializer<PacketInMessage>,
        DeserializerRegistryInjector {

    private static final byte PADDING_IN_PACKET_IN_HEADER = 2;
    private MessageCodeKey MATCH_KEY;

    private DeserializerRegistry registry;

    @Override
    public PacketInMessage deserialize(final ByteBuf rawMessage) {
        PacketInMessageBuilder builder = new PacketInMessageBuilder();
        builder.setVersion(getVersion());
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setBufferId(rawMessage.readUnsignedInt());
        builder.setTotalLen(rawMessage.readUnsignedShort());
        builder.setReason(PacketInReason.forValue(rawMessage.readUnsignedByte()));
        builder.setTableId(new TableId((long) rawMessage.readUnsignedByte()));
        byte[] cookie = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        rawMessage.readBytes(cookie);
        builder.setCookie(new BigInteger(1, cookie));
        OFDeserializer<Match> matchDeserializer = registry.getDeserializer(MATCH_KEY);
        builder.setMatch(matchDeserializer.deserialize(rawMessage));
        rawMessage.skipBytes(PADDING_IN_PACKET_IN_HEADER);
        builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return builder.build();
    }

    @Override
    protected void onVersionAssigned() {
        MATCH_KEY = new MessageCodeKey(getVersion(), EncodeConstants.EMPTY_VALUE, Match.class);
    }

    @Override
    public void injectDeserializerRegistry(final DeserializerRegistry deserializerRegistry) {
        registry = deserializerRegistry;
    }
}
