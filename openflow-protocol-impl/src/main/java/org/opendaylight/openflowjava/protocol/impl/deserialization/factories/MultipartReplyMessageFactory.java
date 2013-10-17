/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow.FlowStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow.FlowStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.GroupStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.GroupStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.group.stats.BucketStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.group.stats.BucketStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats.PortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats.PortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue.QueueStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue.QueueStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.TableStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.TableStatsBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactory implements OFDeserializer<MultipartReplyMessage> {

    private static MultipartReplyMessageFactory instance;
    private static final byte PADDING_IN_MULTIPART_REPLY_HEADER = 4;
    
    private MultipartReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized MultipartReplyMessageFactory getInstance() {
        if (instance == null){
            instance = new MultipartReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public MultipartReplyMessage bufferToMessage(ByteBuf rawMessage, short version) {
        MultipartReplyMessageBuilder builder = new MultipartReplyMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setType(MultipartType.forValue(rawMessage.readUnsignedShort()));
        builder.setFlags(new MultipartRequestFlags((rawMessage.readUnsignedShort() & 0x01) != 0));
        rawMessage.skipBytes(PADDING_IN_MULTIPART_REPLY_HEADER);
        // TODO - implement body
        //mrmb.setBody(rawMessage.readBytes(rawMessage.readableBytes()).array());
        
        switch (builder.getType().getIntValue()) {
        case 0:  builder.setMultipartReplyBody(setDesc(rawMessage));
                 break;
        case 1:  builder.setMultipartReplyBody(setFlow(rawMessage));
                 break;
        case 2:  builder.setMultipartReplyBody(setAggregate(rawMessage));
                 break;
        case 3:  builder.setMultipartReplyBody(setTable(rawMessage));
                 break;         
        case 4:  builder.setMultipartReplyBody(setPortStats(rawMessage));
                 break;
        case 5:  builder.setMultipartReplyBody(setQueue(rawMessage));
                 break;         
        case 6:  builder.setMultipartReplyBody(setGroup(rawMessage));
                 break;
        default: 
                 break;
        }
        
        return builder.build();
    }
    
    private static MultipartReplyDesc setDesc(ByteBuf input) {
        final int DESC_STR_LEN = 256;
        final int SERIAL_NUM_LEN = 32;
        MultipartReplyDescBuilder descBuilder = new MultipartReplyDescBuilder();
        byte[] mfrDescBytes = new byte[DESC_STR_LEN];
        input.readBytes(mfrDescBytes);
        String mfrDesc = new String(mfrDescBytes);
        descBuilder.setMfrDesc(mfrDesc.trim());
        byte[] hwDescBytes = new byte[DESC_STR_LEN];
        input.readBytes(hwDescBytes);
        String hwDesc = new String(hwDescBytes);
        descBuilder.setHwDesc(hwDesc.trim());
        byte[] swDescBytes = new byte[DESC_STR_LEN];
        input.readBytes(swDescBytes);
        String swDesc = new String(swDescBytes);
        descBuilder.setSwDesc(swDesc.trim());
        byte[] serialNumBytes = new byte[SERIAL_NUM_LEN];
        input.readBytes(serialNumBytes);
        String serialNum = new String(serialNumBytes);
        descBuilder.setSerialNum(serialNum.trim());
        byte[] dpDescBytes = new byte[DESC_STR_LEN];
        input.readBytes(dpDescBytes);
        String dpDesc = new String(dpDescBytes);
        descBuilder.setDpDesc(dpDesc.trim());
        return descBuilder.build();
    }
    
    private static MultipartReplyFlow setFlow(ByteBuf input) {
        final byte PADDING_IN_FLOW_STATS_HEADER_01 = 1;
        final byte PADDING_IN_FLOW_STATS_HEADER_02 = 4;
        MultipartReplyFlowBuilder flowBuilder = new MultipartReplyFlowBuilder();
        List<FlowStats> flowStatsList = new ArrayList<>();
        FlowStatsBuilder flowStatsBuilder = new FlowStatsBuilder();
        while (input.readableBytes() > 0) {
            flowStatsBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_FLOW_STATS_HEADER_01);
            flowStatsBuilder.setDurationSec(input.readUnsignedInt());
            flowStatsBuilder.setDurationNsec(input.readUnsignedInt());
            flowStatsBuilder.setPriority(input.readUnsignedShort());
            flowStatsBuilder.setIdleTimeout(input.readUnsignedShort());
            flowStatsBuilder.setHardTimeout(input.readUnsignedShort());
            flowStatsBuilder.setFlags(createFlowModFalgsFromBitmap(input.readShort()));
            input.skipBytes(PADDING_IN_FLOW_STATS_HEADER_02);
            byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(cookie);
            flowStatsBuilder.setCookie(new BigInteger(cookie));
            byte[] packetCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(packetCount);
            flowStatsBuilder.setPacketCount(new BigInteger(packetCount));
            byte[] byteCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(byteCount);
            flowStatsBuilder.setByteCount(new BigInteger(byteCount));
            // TODO match
            // TODO instructions
            flowStatsList.add(flowStatsBuilder.build());
        }
        flowBuilder.setFlowStats(flowStatsList);
        return flowBuilder.build();
    }
    
    private static FlowModFlags createFlowModFalgsFromBitmap(short input){
        final Boolean _oFPFFSENDFLOWREM = (input & (1 << 0)) > 0;
        final Boolean _oFPFFCHECKOVERLAP = (input & (1 << 1)) > 0;
        final Boolean _oFPFFRESETCOUNTS = (input & (1 << 2)) > 0; 
        final Boolean _oFPFFNOPKTCOUNTS = (input & (1 << 3)) > 0;
        final Boolean _oFPFFNOBYTCOUNTS = (input & (1 << 4)) > 0;
        return new FlowModFlags(_oFPFFCHECKOVERLAP, _oFPFFNOBYTCOUNTS, _oFPFFNOPKTCOUNTS, _oFPFFRESETCOUNTS, _oFPFFSENDFLOWREM);
    }
    
    private static MultipartReplyAggregate setAggregate(ByteBuf input) {
        final byte PADDING_IN_AGGREGATE_HEADER = 4;
        MultipartReplyAggregateBuilder builder = new MultipartReplyAggregateBuilder();
        byte[] packetCount = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(packetCount);
        builder.setPacketCount(new BigInteger(packetCount));
        byte[] byteCount = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(byteCount);
        builder.setByteCount(new BigInteger(byteCount));
        builder.setFlowCount(input.readUnsignedInt());
        input.skipBytes(PADDING_IN_AGGREGATE_HEADER);
        return builder.build();
    }
    
    private static MultipartReplyTable setTable(ByteBuf input) {
        final byte PADDING_IN_TABLE_HEADER = 3;
        MultipartReplyTableBuilder builder = new MultipartReplyTableBuilder();
        TableStatsBuilder tableStatsBuilder = new TableStatsBuilder();
        List<TableStats> tableStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            tableStatsBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_TABLE_HEADER);
            tableStatsBuilder.setActiveCount(input.readUnsignedInt());
            byte[] lookupCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(lookupCount);
            tableStatsBuilder.setLookupCount(new BigInteger(lookupCount));
            byte[] matchedCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(matchedCount);
            tableStatsBuilder.setMatchedCount(new BigInteger(matchedCount));
            tableStatsList.add(tableStatsBuilder.build());
        }
        builder.setTableStats(tableStatsList);
        return builder.build();
    }
    
    private static MultipartReplyPortStats setPortStats(ByteBuf input) {
        final byte PADDING_IN_PORT_STATS_HEADER = 4;
        MultipartReplyPortStatsBuilder builder = new MultipartReplyPortStatsBuilder();
        PortStatsBuilder portStatsBuilder = new PortStatsBuilder();
        List<PortStats> portStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            portStatsBuilder.setPortNo(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_PORT_STATS_HEADER);
            
            byte[] rxPackets = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxPackets);
            portStatsBuilder.setRxPackets(new BigInteger(rxPackets));
            
            byte[] txPackets = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txPackets);
            portStatsBuilder.setTxPackets(new BigInteger(txPackets));
            
            byte[] rxBytes = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxBytes);
            portStatsBuilder.setRxBytes(new BigInteger(rxBytes));
            
            byte[] txBytes = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txBytes);
            portStatsBuilder.setTxBytes(new BigInteger(txBytes));
            
            byte[] rxDropped = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxDropped);
            portStatsBuilder.setRxDropped(new BigInteger(rxDropped));
            
            byte[] txDropped = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txDropped);
            portStatsBuilder.setTxDropped(new BigInteger(txDropped));
            
            byte[] rxErrors = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxErrors);
            portStatsBuilder.setRxErrors(new BigInteger(rxErrors));
            
            byte[] txErrors = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txErrors);
            portStatsBuilder.setTxErrors(new BigInteger(txErrors));
            
            byte[] rxFrameErr = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxFrameErr);
            portStatsBuilder.setRxFrameErr(new BigInteger(rxFrameErr));
            
            byte[] rxOverErr = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxOverErr);
            portStatsBuilder.setRxOverErr(new BigInteger(rxOverErr));
            
            byte[] rxCrcErr = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(rxCrcErr);
            portStatsBuilder.setRxCrcErr(new BigInteger(rxCrcErr));
            
            byte[] collisions = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(collisions);
            portStatsBuilder.setCollisions(new BigInteger(collisions));
            
            portStatsBuilder.setDurationSec(input.readUnsignedInt());
            portStatsBuilder.setDurationNsec(input.readUnsignedInt());
            portStatsList.add(portStatsBuilder.build());
        }
        builder.setPortStats(portStatsList);
        return builder.build();
    }
    
    private static MultipartReplyQueue setQueue(ByteBuf input) {
        MultipartReplyQueueBuilder builder = new MultipartReplyQueueBuilder();
        QueueStatsBuilder queueStatsBuilder = new QueueStatsBuilder();
        List<QueueStats> queueStatsList = new ArrayList<>();
        
        while (input.readableBytes() > 0) {
            queueStatsBuilder.setPortNo(input.readUnsignedInt());
            queueStatsBuilder.setQueueId(input.readUnsignedInt());

            byte[] txBytes = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txBytes);
            queueStatsBuilder.setTxBytes(new BigInteger(txBytes));

            byte[] txPackets = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txPackets);
            queueStatsBuilder.setTxPackets(new BigInteger(txPackets));

            byte[] txErrors = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(txErrors);
            queueStatsBuilder.setTxErrors(new BigInteger(txErrors));

            queueStatsBuilder.setDurationSec(input.readUnsignedInt());
            queueStatsBuilder.setDurationNsec(input.readUnsignedInt());
            queueStatsList.add(queueStatsBuilder.build());
        }
        builder.setQueueStats(queueStatsList);
        return builder.build();
    }
    
    private static MultipartReplyGroup setGroup(ByteBuf input) {
        final byte PADDING_IN_GROUP_HEADER_01 = 2;
        final byte PADDING_IN_GROUP_HEADER_02 = 4;
        final byte BUCKET_COUNTER_LENGTH = 16;
        final byte GROUP_BODY_LENGTH = 40;
        int actualLength;
        MultipartReplyGroupBuilder builder = new MultipartReplyGroupBuilder();
        GroupStatsBuilder groupStatsBuilder = new GroupStatsBuilder();
        List<GroupStats> groupStatsList = new ArrayList<>();
        
        BucketStatsBuilder bucketStatsBuilder = new BucketStatsBuilder();
        List<BucketStats> bucketStatsList = new ArrayList<>();
        
        while (input.readableBytes() > 0) {
            int bodyLength = input.readUnsignedShort();
            actualLength = 0;
            
            input.skipBytes(PADDING_IN_GROUP_HEADER_01);
            groupStatsBuilder.setGroupId(input.readUnsignedInt());
            groupStatsBuilder.setRefCount(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_GROUP_HEADER_02);
            byte[] packetCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(packetCount);
            groupStatsBuilder.setPacketCount(new BigInteger(packetCount));
            byte[] byteCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(byteCount);
            groupStatsBuilder.setByteCount(new BigInteger(byteCount));
            groupStatsBuilder.setDurationSec(input.readUnsignedInt());
            groupStatsBuilder.setDurationNsec(input.readUnsignedInt());
            actualLength = GROUP_BODY_LENGTH;
            
            while (actualLength < bodyLength) {
                byte[] packetCountBucket = new byte[Long.SIZE/Byte.SIZE];
                input.readBytes(packetCountBucket);
                bucketStatsBuilder.setPacketCount(new BigInteger(packetCountBucket));
                byte[] byteCountBucket = new byte[Long.SIZE/Byte.SIZE];
                input.readBytes(byteCountBucket);
                bucketStatsBuilder.setByteCount(new BigInteger(byteCountBucket));
                bucketStatsList.add(bucketStatsBuilder.build());

                groupStatsBuilder.setBucketStats(bucketStatsList);
                groupStatsList.add(groupStatsBuilder.build());
                actualLength = actualLength + BUCKET_COUNTER_LENGTH;
            } 
        }
        builder.setGroupStats(groupStatsList);
        return builder.build();
    }
}
