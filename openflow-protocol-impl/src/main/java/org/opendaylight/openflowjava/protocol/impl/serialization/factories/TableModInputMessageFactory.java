/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class TableModInputMessageFactory implements OFSerializer<TableModInput> {

    private static final byte MESSAGE_TYPE = 17;
    private static final byte PADDING_IN_TABLE_MOD_MESSAGE = 3;
    private static final int MESSAGE_LENGTH = 16;
    private static TableModInputMessageFactory instance;
    
    private TableModInputMessageFactory() {
        // just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static TableModInputMessageFactory getInstance() {
        if(instance == null){
            instance = new TableModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out, TableModInput message) {
        
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeByte(message.getTableId().getValue().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_TABLE_MOD_MESSAGE, out);
        out.writeInt(createConfigBitmask(message.getConfig()));
    }

    @Override
    public int computeLength() {
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
    public static int createConfigBitmask(PortConfig config) {
        int configBitmask = 0;
        Map<Integer, Boolean> portConfigMap = new HashMap<>();
        portConfigMap.put(0, config.isPortDown());
        portConfigMap.put(2, config.isNoRecv());
        portConfigMap.put(5, config.isNoFwd());
        portConfigMap.put(6, config.isNoPacketIn());
        
        configBitmask = ByteBufUtils.fillBitMaskFromMap(portConfigMap);
        return configBitmask;
    }
}
