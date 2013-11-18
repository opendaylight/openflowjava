/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.BucketsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.BucketsListBuilder;

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
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setCommand(GroupModCommand.forValue(2));
        builder.setType(GroupType.forValue(3));
        builder.setGroupId(256L);
        List<BucketsList> exp = createBucketsList();
        builder.setBucketsList(exp);
        GroupModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        GroupModInputMessageFactory factory = GroupModInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong command", message.getCommand().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readUnsignedByte());
        out.skipBytes(PADDING_IN_GROUP_MOD_MESSAGE);
        Assert.assertEquals("Wrong groupId", message.getGroupId().intValue(), out.readUnsignedInt());
        List<BucketsList> rec = createBucketsListFromBufer(out);
        Assert.assertArrayEquals("Wrong bucketList", exp.toArray(), rec.toArray());
    }
    
    private static List<BucketsList> createBucketsList(){
        List<BucketsList> bucketsList = new ArrayList<>();
        BucketsListBuilder bucketsBuilder = new BucketsListBuilder();
        BucketsList bucket;
        bucketsBuilder.setWeight(10);
        bucketsBuilder.setWatchPort(new PortNumber(65L));
        bucketsBuilder.setWatchGroup(22L);
        bucket = bucketsBuilder.build();
        bucketsList.add(bucket);
        return bucketsList;
    }
    
    private static List<BucketsList> createBucketsListFromBufer(ByteBuf out){
        List<BucketsList> bucketsList = new ArrayList<>();
        BucketsListBuilder bucketsBuilder = new BucketsListBuilder();
        BucketsList bucket;
        out.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        bucketsBuilder.setWeight(out.readUnsignedShort());
        bucketsBuilder.setWatchPort(new PortNumber(out.readUnsignedInt()));
        bucketsBuilder.setWatchGroup(out.readUnsignedInt());
        out.skipBytes(4);
        bucket = bucketsBuilder.build();
        bucketsList.add(bucket);
        return bucketsList;
    }
}
