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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;

/**
 * Translates TableMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class TableModInputMessageFactory implements OFSerializer<TableModInput> {
    private static final byte MESSAGE_TYPE = 17;
    private static final byte PADDING_IN_TABLE_MOD_MESSAGE = 3;

    @Override
    public void serialize(TableModInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeByte(object.getTableId().getValue().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_TABLE_MOD_MESSAGE, outBuffer);
        outBuffer.writeInt(createConfigBitmask(object.getConfig()));
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    /**
     * @param tableConfig
     * @return port config bitmask 
     */
    private static int createConfigBitmask(TableConfig tableConfig) {
        Map<Integer, Boolean> portConfigMap = new HashMap<>();
        portConfigMap.put(3, tableConfig.isOFPTCDEPRECATEDMASK());
        int configBitmask = ByteBufUtils.fillBitMaskFromMap(portConfigMap);
        return configBitmask;
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        // do nothing - no need for table in this factory
    }
}
