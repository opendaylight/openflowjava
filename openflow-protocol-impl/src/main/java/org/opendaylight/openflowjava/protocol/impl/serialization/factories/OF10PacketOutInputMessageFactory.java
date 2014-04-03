/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.CodingUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;

/**
 * Translates PacketOut messages
 * @author michal.polkorab
 */
public class OF10PacketOutInputMessageFactory implements OFSerializer<PacketOutInput> {

    private static final byte MESSAGE_TYPE = 13;
    private SerializerTable serializerTable;

    @Override
    public void serialize(PacketOutInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeInt(object.getBufferId().intValue());
        outBuffer.writeShort(object.getInPort().getValue().intValue());
        int actionsLengthIndex = outBuffer.writerIndex();
        outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
        int actionsStartIndex = outBuffer.writerIndex();
        OFSerializer<Action> serializer = 
                serializerTable.getSerializer(new MessageTypeKey<>(object.getVersion(), Action.class));
        CodingUtils.serializeList(object.getAction(), serializer, outBuffer);
        outBuffer.setShort(actionsLengthIndex, outBuffer.writerIndex() - actionsStartIndex);
        byte[] data = object.getData();
        if (data != null) {
            outBuffer.writeBytes(data);
        }
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }

}
