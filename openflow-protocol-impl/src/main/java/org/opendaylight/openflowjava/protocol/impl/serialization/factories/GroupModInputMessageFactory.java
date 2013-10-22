/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.group.mod.BucketsList;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class GroupModInputMessageFactory implements OFSerializer<GroupModInput> {
    private static final byte MESSAGE_TYPE = 15;
    private static final byte PADDING_IN_GROUP_MOD_MESSAGE = 1;
    private static final int MESSAGE_LENGTH = 16; 
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
    public int computeLength() {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
    private static void encodeBuckets(List<BucketsList> buckets, ByteBuf outBuffer) {
        final byte PADDING_IN_BUCKET = 4;
        
        for (Iterator<BucketsList> iterator = buckets.iterator(); iterator.hasNext();) {
            BucketsList currentBucket = iterator.next();
            // TODO get method for field length missing
            outBuffer.writeShort(currentBucket.getWeight().intValue());
            outBuffer.writeInt(currentBucket.getWatchPort().getValue().intValue());
            outBuffer.writeInt(currentBucket.getWatchGroup().intValue());
            ByteBufUtils.padBuffer(PADDING_IN_BUCKET, outBuffer);
            // TODO actions structure missing
        }
    }

}
