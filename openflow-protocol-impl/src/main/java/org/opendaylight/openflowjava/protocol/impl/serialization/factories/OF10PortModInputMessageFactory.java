/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;

/**
 * @author michal.polkorab
 *
 */
public class OF10PortModInputMessageFactory implements OFSerializer<PortModInput> {

    private static final byte MESSAGE_TYPE = 15;
    private static final byte PADDING_IN_PORT_MOD_MESSAGE = 4;
    private static final int MESSAGE_LENGTH = 32;
    
    private static OF10PortModInputMessageFactory instance;
    
    private OF10PortModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10PortModInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10PortModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, PortModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getPortNo().getValue().intValue());
        out.writeBytes(ByteBufUtils.hexStringToBytes(message.getHwAddress().getValue(), false));
        out.writeInt(createPortConfigBitmask(message.getConfigV10()));
        out.writeInt(createPortConfigBitmask(message.getMaskV10()));
        out.writeInt(createPortFeaturesBitmask(message.getAdvertiseV10()));
        ByteBufUtils.padBuffer(PADDING_IN_PORT_MOD_MESSAGE, out);
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
}
