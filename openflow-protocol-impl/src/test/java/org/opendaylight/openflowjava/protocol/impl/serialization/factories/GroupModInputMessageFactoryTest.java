/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.group.mod.Buckets;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.group.mod.BucketsBuilder;

/**
 * @author timotej.kubas
 *
 */
public class GroupModInputMessageFactoryTest {
    private static final byte PADDING_IN_GROUP_MOD_MESSAGE = 1;
    
    /**
     * @throws Exception
     * Testing of {@link GroupModInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testGroupModInputMessage() throws Exception {
        GroupModInputBuilder builder = new GroupModInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setCommand(GroupModCommand.forValue(2));
        builder.setType(GroupType.forValue(3));
        builder.setGroupId(256L);
        builder.setBuckets(createBucketsList());
        GroupModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GroupModInputMessageFactory factory = GroupModInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength());
        Assert.assertEquals("Wrong command", message.getCommand().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readByte());
        out.skipBytes(PADDING_IN_GROUP_MOD_MESSAGE);
        Assert.assertEquals("Wrong groupId", message.getGroupId().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong bucketList", true, compareBucketsLists(createBucketsList(), createBucketsListFromBufer(out)));
    }
    
    private static List<Buckets> createBucketsList(){
        List<Buckets> bucketsList = new ArrayList<Buckets>();
        BucketsBuilder bucketsBuilder = new BucketsBuilder();
        Buckets bucket;
        bucketsBuilder.setWeight(10);
        bucketsBuilder.setWatchPort(new PortNumber(65L));
        bucketsBuilder.setWatchGroup(22L);
        bucket = bucketsBuilder.build();
        bucketsList.add(bucket);
        return bucketsList;
    }
    
    private static List<Buckets> createBucketsListFromBufer(ByteBuf out){
        List<Buckets> bucketsList = new ArrayList<Buckets>();
        BucketsBuilder bucketsBuilder = new BucketsBuilder();
        Buckets bucket;
        bucketsBuilder.setWeight((int) out.readShort());
        bucketsBuilder.setWatchPort(new PortNumber(out.readUnsignedInt()));
        bucketsBuilder.setWatchGroup(out.readUnsignedInt());
        out.skipBytes(4);
        bucket = bucketsBuilder.build();
        bucketsList.add(bucket);
        return bucketsList;
    }
    
    private static boolean compareBucketsLists(List<Buckets> bucketsFromMessage, List<Buckets> bucketsFromBuffer) {
        boolean result = false;
        int bucketsFromMessageLength = bucketsFromMessage.size();
        for (int i = 0; i < bucketsFromMessageLength; i++) {
            if (bucketsFromMessage.get(i).getWeight().equals(bucketsFromBuffer.get(i).getWeight())) {
                result = true;
            } else {
                result = false;
                break;
            }
            if (bucketsFromMessage.get(i).getWatchPort()
                    .equals(bucketsFromBuffer.get(i).getWatchPort())) {
                result = true;
            } else {
                result = false;
                break;
            }
            if (bucketsFromMessage.get(i).getWatchGroup()
                    .equals(bucketsFromBuffer.get(i).getWatchGroup())) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
     // TODO get method for field length missing
     // TODO actions structure missing
        return result;
    }
}
