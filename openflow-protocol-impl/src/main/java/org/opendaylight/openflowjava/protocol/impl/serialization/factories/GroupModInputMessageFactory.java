/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.CodingUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.grouping.BucketsList;

/**
 * Translates GroupMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GroupModInputMessageFactory implements OFSerializer<GroupModInput> {
    private static final byte MESSAGE_TYPE = 15;
    private static final byte PADDING_IN_GROUP_MOD_MESSAGE = 1;
    private static final byte PADDING_IN_BUCKET = 4;
    private SerializerTable serializerTable;

    @Override
    public void serialize(GroupModInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeShort(object.getCommand().getIntValue());
        outBuffer.writeByte(object.getType().getIntValue());
        ByteBufUtils.padBuffer(PADDING_IN_GROUP_MOD_MESSAGE, outBuffer);
        outBuffer.writeInt(object.getGroupId().getValue().intValue());
        encodeBuckets(object.getBucketsList(), outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    private void encodeBuckets(List<BucketsList> buckets, ByteBuf outBuffer) {
        if (buckets != null) {
            for (BucketsList currentBucket : buckets) {
                int bucketLengthIndex = outBuffer.writerIndex();
                outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
                outBuffer.writeShort(currentBucket.getWeight().shortValue());
                outBuffer.writeInt(currentBucket.getWatchPort().getValue().intValue());
                outBuffer.writeInt(currentBucket.getWatchGroup().intValue());
                ByteBufUtils.padBuffer(PADDING_IN_BUCKET, outBuffer);
                OFSerializer<Action> actionSerializer = serializerTable.getSerializer(
                        new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, Action.class));
                CodingUtils.serializeList(currentBucket.getAction(), actionSerializer, outBuffer);
                outBuffer.setShort(bucketLengthIndex, outBuffer.writerIndex() - bucketLengthIndex);
            }
        }
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }

}
