/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.BucketsList;

/**
 * Translates GroupMod messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GroupModInputMessageFactory implements OFSerializer<GroupModInput> {
    private static final byte MESSAGE_TYPE = 15;
    private static final int MESSAGE_LENGTH = 16;
    private static final byte PADDING_IN_GROUP_MOD_MESSAGE = 1;
    private static final byte LENGTH_OF_BUCKET_STRUCTURE = 16;
    private static final byte PADDING_IN_BUCKET = 4;
    private static GroupModInputMessageFactory instance;

    private GroupModInputMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized GroupModInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new GroupModInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            GroupModInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getCommand().getIntValue());
        out.writeByte(message.getType().getIntValue());
        ByteBufUtils.padBuffer(PADDING_IN_GROUP_MOD_MESSAGE, out);
        out.writeInt(message.getGroupId().intValue());
        encodeBuckets(message.getBucketsList(), out);
    }

    @Override
    public int computeLength(GroupModInput message) {
        return MESSAGE_LENGTH + computeLengthOfBuckets(message.getBucketsList());
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
    private static void encodeBuckets(List<BucketsList> buckets, ByteBuf outBuffer) {
        if (buckets != null) {
            for (BucketsList currentBucket : buckets) {
                outBuffer.writeShort(computeLengthOfBucket(currentBucket));
                
                if (null != currentBucket.getWeight()) {
                    outBuffer.writeShort(currentBucket.getWeight().shortValue());
                }
                
                if (null != currentBucket.getWatchPort()) {
                    outBuffer.writeInt(currentBucket.getWatchPort().getValue().intValue());
                }
                
                if (null != currentBucket.getWatchGroup()) {
                    outBuffer.writeInt(currentBucket.getWatchGroup().intValue());
                }
                ByteBufUtils.padBuffer(PADDING_IN_BUCKET, outBuffer);
                ActionsSerializer.encodeActions(currentBucket.getActionsList(), outBuffer);
            }
        }
    }
    
    private static int computeLengthOfBucket(BucketsList bucket) {
        int lengthOfBuckets = 0;
        if (bucket != null) {
            lengthOfBuckets = LENGTH_OF_BUCKET_STRUCTURE + ActionsSerializer.computeLengthOfActions(bucket.getActionsList());
        }
        return lengthOfBuckets;
    }
    
    private static int computeLengthOfBuckets(List<BucketsList> buckets) {
        int lengthOfBuckets = 0;
        if (buckets != null) {
            for (BucketsList currentBucket : buckets) {
                lengthOfBuckets += LENGTH_OF_BUCKET_STRUCTURE + ActionsSerializer.computeLengthOfActions(currentBucket.getActionsList());
            }
        }
        return lengthOfBuckets;
    }

    
}
