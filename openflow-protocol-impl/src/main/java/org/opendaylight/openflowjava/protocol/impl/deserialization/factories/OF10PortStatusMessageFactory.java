/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.OpenflowUtils;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortReason;

/**
 * Translates PortStatus messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10PortStatusMessageFactory implements OFDeserializer<PortStatusMessage> {

    private static final byte PADDING_IN_PORT_STATUS_HEADER = 7;

    @Override
    public PortStatusMessage deserialize(ByteBuf rawMessage) {
        PortStatusMessageBuilder builder = new PortStatusMessageBuilder();
        builder.setVersion((short) EncodeConstants.OF10_VERSION_ID);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setReason(PortReason.forValue(rawMessage.readUnsignedByte()));
        rawMessage.skipBytes(PADDING_IN_PORT_STATUS_HEADER);
        deserializePort(rawMessage, builder);
        return builder.build();
    }

    private static void deserializePort(ByteBuf rawMessage, PortStatusMessageBuilder builder) {
        builder.setPortNo((long) rawMessage.readUnsignedShort());
        byte[] address = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        rawMessage.readBytes(address);
        builder.setHwAddr(new MacAddress(ByteBufUtils.macAddressToString(address)));
        builder.setName(ByteBufUtils.decodeNullTerminatedString(rawMessage, EncodeConstants.MAX_PORT_NAME_LENGTH));
        builder.setConfig(OpenflowUtils.createPortConfig(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
        builder.setState(OpenflowUtils.createPortState(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
        builder.setCurrentFeatures(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
        builder.setAdvertisedFeatures(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
        builder.setSupportedFeatures(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
        builder.setPeerFeatures(OpenflowUtils.createPortFeatures(rawMessage.readUnsignedInt(),
                EncodeConstants.OF10_VERSION_ID));
    }
}