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
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;

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
    public void serialize(PortModInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeInt(message.getPortNo().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_01, outBuffer);
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(message.getHwAddress().getValue()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_02, outBuffer);
        outBuffer.writeInt(createPortConfigBitmask(message.getConfig()));
        outBuffer.writeInt(createPortConfigBitmask(message.getMask()));
        outBuffer.writeInt(createPortFeaturesBitmask(message.getAdvertise()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_03, outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    /**
     * @param config
     * @return port config bitmask 
     */
    private static int createPortConfigBitmask(PortConfig config) {
        int configBitmask = 0;
        Map<Integer, Boolean> portConfigMap = new HashMap<>();
        portConfigMap.put(0, config.isPortDown());
        portConfigMap.put(2, config.isNoRecv());
        portConfigMap.put(5, config.isNoFwd());
        portConfigMap.put(6, config.isNoPacketIn());
        
        configBitmask = ByteBufUtils.fillBitMaskFromMap(portConfigMap);
        return configBitmask;
    }
    
    private static int createPortFeaturesBitmask(PortFeatures feature) {
        int configBitmask = 0;
        Map<Integer, Boolean> portFeaturesMap = new HashMap<>();
        portFeaturesMap.put(0, feature.is_10mbHd());
        portFeaturesMap.put(1, feature.is_10mbFd());
        portFeaturesMap.put(2, feature.is_100mbHd());
        portFeaturesMap.put(3, feature.is_100mbFd());
        portFeaturesMap.put(4, feature.is_1gbHd());
        portFeaturesMap.put(5, feature.is_1gbFd());
        portFeaturesMap.put(6, feature.is_10gbFd());
        portFeaturesMap.put(7, feature.is_40gbFd());
        portFeaturesMap.put(8, feature.is_100gbFd());
        portFeaturesMap.put(9, feature.is_1tbFd());
        portFeaturesMap.put(10, feature.isOther());
        portFeaturesMap.put(11, feature.isCopper());
        portFeaturesMap.put(12, feature.isFiber());
        portFeaturesMap.put(13, feature.isAutoneg());
        portFeaturesMap.put(14, feature.isPause());
        portFeaturesMap.put(15, feature.isPauseAsym());
        
        configBitmask = ByteBufUtils.fillBitMaskFromMap(portFeaturesMap);
        return configBitmask;
    }

}
