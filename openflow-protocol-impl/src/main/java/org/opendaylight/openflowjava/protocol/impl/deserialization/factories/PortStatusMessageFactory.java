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
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessageBuilder;

/**
 * Translates PortStatus messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class PortStatusMessageFactory implements OFDeserializer<PortStatusMessage> {

    private static final byte PADDING_IN_PORT_STATUS_HEADER = 7;
    private static final byte PADDING_IN_OFP_PORT_HEADER_1 = 4;
    private static final byte PADDING_IN_OFP_PORT_HEADER_2 = 2;

    @Override
    public PortStatusMessage deserialize(ByteBuf rawMessage) {
        PortStatusMessageBuilder builder = new PortStatusMessageBuilder(); 
        builder.setVersion((short) EncodeConstants.OF13_VERSION_ID);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setReason(PortReason.forValue(rawMessage.readUnsignedByte()));
        rawMessage.skipBytes(PADDING_IN_PORT_STATUS_HEADER);
        builder.setPortNo(rawMessage.readUnsignedInt());
        rawMessage.skipBytes(PADDING_IN_OFP_PORT_HEADER_1);
        byte[] hwAddress = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        rawMessage.readBytes(hwAddress);
        builder.setHwAddr(new MacAddress(ByteBufUtils.macAddressToString(hwAddress)));
        rawMessage.skipBytes(PADDING_IN_OFP_PORT_HEADER_2);
        builder.setName(ByteBufUtils.decodeNullTerminatedString(rawMessage, EncodeConstants.MAX_PORT_NAME_LENGTH));
        builder.setConfig(createPortConfig(rawMessage.readUnsignedInt()));
        builder.setState(createPortState(rawMessage.readUnsignedInt()));
        builder.setCurrentFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setAdvertisedFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setSupportedFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setPeerFeatures(createPortFeatures(rawMessage.readUnsignedInt()));
        builder.setCurrSpeed(rawMessage.readUnsignedInt());
        builder.setMaxSpeed(rawMessage.readUnsignedInt());
        return builder.build();
    }

    private static PortFeatures createPortFeatures(long input){
        final Boolean _10mbHd = ((input) & (1<<0)) != 0;
        final Boolean _10mbFd = ((input) & (1<<1)) != 0;
        final Boolean _100mbHd = ((input) & (1<<2)) != 0;
        final Boolean _100mbFd = ((input) & (1<<3)) != 0;
        final Boolean _1gbHd = ((input) & (1<<4)) != 0;
        final Boolean _1gbFd = ((input) & (1<<5)) != 0;
        final Boolean _10gbFd = ((input) & (1<<6)) != 0;
        final Boolean _40gbFd = ((input) & (1<<7)) != 0;
        final Boolean _100gbFd = ((input) & (1<<8)) != 0;
        final Boolean _1tbFd = ((input) & (1<<9)) != 0;
        final Boolean _other = ((input) & (1<<10)) != 0;
        final Boolean _copper = ((input) & (1<<11)) != 0;
        final Boolean _fiber = ((input) & (1<<12)) != 0;
        final Boolean _autoneg = ((input) & (1<<13)) != 0;
        final Boolean _pause = ((input) & (1<<14)) != 0;
        final Boolean _pauseAsym = ((input) & (1<<15)) != 0;
        return new PortFeatures(_100gbFd, _100mbFd, _100mbHd, _10gbFd, _10mbFd, _10mbHd, _1gbFd,
                _1gbHd, _1tbFd, _40gbFd, _autoneg, _copper, _fiber, _other, _pause, _pauseAsym);
    }
    
    private static PortState createPortState(long input){
        final Boolean _linkDown = ((input) & (1<<0)) != 0;
        final Boolean _blocked  = ((input) & (1<<1)) != 0;
        final Boolean _live     = ((input) & (1<<2)) != 0;
        return new PortState(_blocked, _linkDown, _live);
    }
    
    private static PortConfig createPortConfig(long input){
        final Boolean _portDown   = ((input) & (1<<0)) != 0;
        final Boolean _noRecv    = ((input) & (1<<2)) != 0;
        final Boolean _noFwd       = ((input) & (1<<5)) != 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) != 0;
        return new PortConfig(_noFwd, _noPacketIn, _noRecv, _portDown);
    }
}
