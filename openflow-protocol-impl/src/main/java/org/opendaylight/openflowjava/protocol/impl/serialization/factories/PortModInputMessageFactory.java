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

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortConfigV13;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.shared.port.rev141119.PortFeaturesV13;

/**
 * Translates PortMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class PortModInputMessageFactory implements OFSerializer<PortModInput> {
    private static final byte MESSAGE_TYPE = 16;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_01 = 4;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_02 = 2;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE_03 = 4;

    @Override
    public void serialize(final PortModInput message, final ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeInt(message.getPortNo().getValue().intValue());
        outBuffer.writeZero(PADDING_IN_PORT_MOD_MESSAGE_01);
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(message.getHwAddress().getValue()));
        outBuffer.writeZero(PADDING_IN_PORT_MOD_MESSAGE_02);
        outBuffer.writeInt(createPortConfigBitmask(message.getConfig().getPortConfigV13()));
        outBuffer.writeInt(createPortConfigBitmask(message.getMask().getPortConfigV13()));
        outBuffer.writeInt(createPortFeaturesBitmask(message.getAdvertise().getPortFeaturesV13()));
        outBuffer.writeZero(PADDING_IN_PORT_MOD_MESSAGE_03);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    /**
     * @param config
     * @return port config bitmask
     */
    private static int createPortConfigBitmask(final PortConfigV13 config) {
        int configBitmask = 0;
        Map<Integer, Boolean> portConfigMap = new HashMap<>();
        portConfigMap.put(0, config.isPortDown());
        portConfigMap.put(2, config.isNoRecv());
        portConfigMap.put(5, config.isNoFwd());
        portConfigMap.put(6, config.isNoPacketIn());

        configBitmask = ByteBufUtils.fillBitMaskFromMap(portConfigMap);
        return configBitmask;
    }

    private static int createPortFeaturesBitmask(final PortFeaturesV13 feature) {
        return ByteBufUtils.fillBitMask(0, feature.is_10mbHd(),
                feature.is_10mbFd(),
                feature.is_100mbHd(),
                feature.is_100mbFd(),
                feature.is_1gbHd(),
                feature.is_1gbFd(),
                feature.is_10gbFd(),
                feature.is_40gbFd(),
                feature.is_100gbFd(),
                feature.is_1tbFd(),
                feature.isOther(),
                feature.isCopper(),
                feature.isFiber(),
                feature.isAutoneg(),
                feature.isPause(),
                feature.isPauseAsym());
    }

}
