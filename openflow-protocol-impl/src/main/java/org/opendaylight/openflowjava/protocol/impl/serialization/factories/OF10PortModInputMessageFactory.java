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
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;

/**
 * Translates PortMod messages
 * @author michal.polkorab
 */
public class OF10PortModInputMessageFactory implements OFSerializer<PortModInput> {

    private static final byte MESSAGE_TYPE = 15;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE = 4;

    @Override
    public void serialize(PortModInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeShort(object.getPortNo().getValue().intValue());
        outBuffer.writeBytes(ByteBufUtils.macAddressToBytes(object.getHwAddress().getValue()));
        outBuffer.writeInt(createPortConfigBitmask(object.getConfigV10()));
        outBuffer.writeInt(createPortConfigBitmask(object.getMaskV10()));
        outBuffer.writeInt(createPortFeaturesBitmask(object.getAdvertiseV10()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE, outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    /**
     * @param config
     * @return port config bitmask 
     */
    private static int createPortConfigBitmask(PortConfigV10 config) {
        int configBitmask = 0;
        Map<Integer, Boolean> portConfigMap = new HashMap<>();
        portConfigMap.put(0, config.isPortDown());
        portConfigMap.put(1, config.isNoStp());
        portConfigMap.put(2, config.isNoRecv());
        portConfigMap.put(3, config.isNoRecvStp());
        portConfigMap.put(4, config.isNoFlood());
        portConfigMap.put(5, config.isNoFwd());
        portConfigMap.put(6, config.isNoPacketIn());
        
        configBitmask = ByteBufUtils.fillBitMaskFromMap(portConfigMap);
        return configBitmask;
    }
    
    private static int createPortFeaturesBitmask(PortFeaturesV10 feature) {
        int configBitmask = 0;
        Map<Integer, Boolean> portFeaturesMap = new HashMap<>();
        portFeaturesMap.put(0, feature.is_10mbHd());
        portFeaturesMap.put(1, feature.is_10mbFd());
        portFeaturesMap.put(2, feature.is_100mbHd());
        portFeaturesMap.put(3, feature.is_100mbFd());
        portFeaturesMap.put(4, feature.is_1gbHd());
        portFeaturesMap.put(5, feature.is_1gbFd());
        portFeaturesMap.put(6, feature.is_10gbFd());
        portFeaturesMap.put(7, feature.isCopper());
        portFeaturesMap.put(8, feature.isFiber());
        portFeaturesMap.put(9, feature.isAutoneg());
        portFeaturesMap.put(10, feature.isPause());
        portFeaturesMap.put(11, feature.isPauseAsym());
        configBitmask = ByteBufUtils.fillBitMaskFromMap(portFeaturesMap);
        return configBitmask;
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        // do nothing - no need for table in this factory
    }
}
