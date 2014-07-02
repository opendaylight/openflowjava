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

import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.EnhancedTypeKeyMakerFactory;
import org.opendaylight.openflowjava.protocol.impl.util.ListSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.grouping.BucketsList;

/**
 * Translates GroupMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GroupModInputMessageFactory implements OFSerializer<GroupModInput>, SerializerRegistryInjector {
    private static final byte MESSAGE_TYPE = 15;
    private static final byte PADDING_IN_GROUP_MOD_MESSAGE = 1;
    private static final byte PADDING_IN_BUCKET = 4;
    private SerializerRegistry registry;

    @Override
    public void serialize(GroupModInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeShort(message.getCommand().getIntValue());
        outBuffer.writeByte(message.getType().getIntValue());
        ByteBufUtils.padBuffer(PADDING_IN_GROUP_MOD_MESSAGE, outBuffer);
        outBuffer.writeInt(message.getGroupId().getValue().intValue());
        serializerBuckets(message.getBucketsList(), outBuffer);
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

    private void serializerBuckets(List<BucketsList> buckets, ByteBuf outBuffer) {
        if (buckets != null) {
            for (BucketsList currentBucket : buckets) {
                int bucketLengthIndex = outBuffer.writerIndex();
                outBuffer.writeShort(EncodeConstants.EMPTY_LENGTH);
                outBuffer.writeShort(currentBucket.getWeight().shortValue());
                outBuffer.writeInt(currentBucket.getWatchPort().getValue().intValue());
                outBuffer.writeInt(currentBucket.getWatchGroup().intValue());
                ByteBufUtils.padBuffer(PADDING_IN_BUCKET, outBuffer);
                ListSerializer.serializeList(currentBucket.getAction(), EnhancedTypeKeyMakerFactory
                        .createActionKeyMaker(EncodeConstants.OF13_VERSION_ID), registry, outBuffer);
                outBuffer.setShort(bucketLengthIndex, outBuffer.writerIndex() - bucketLengthIndex);
            }
        }
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
    }

}
