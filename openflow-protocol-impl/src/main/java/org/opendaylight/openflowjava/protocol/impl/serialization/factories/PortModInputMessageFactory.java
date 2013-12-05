/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
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
    private static final int MESSAGE_LENGTH = 40;
    private static PortModInputMessageFactory instance;
    
    private PortModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized PortModInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new PortModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, PortModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getPortNo().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_01, out);
        out.writeBytes(ByteBufUtils.macAddressToBytes(message.getHwAddress().getValue()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_02, out);
        out.writeInt(createPortConfigBitmask(message.getConfig()));
        out.writeInt(createPortConfigBitmask(message.getMask()));
        out.writeInt(createPortFeaturesBitmask(message.getAdvertise()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE_03, out);
    }

    @Override
    public int computeLength(PortModInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
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
        System.out.println("configBitmask: " + configBitmask);
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
