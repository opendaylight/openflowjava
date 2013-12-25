/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10ActionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.aggregate._case.MultipartReplyAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.desc._case.MultipartReplyDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.experimenter._case.MultipartReplyExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.MultipartReplyFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.multipart.reply.flow.FlowStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.multipart.reply.flow.FlowStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.MultipartReplyPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.MultipartReplyQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.multipart.reply.queue.QueueStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.multipart.reply.queue.QueueStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.MultipartReplyTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.multipart.reply.table.TableStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.multipart.reply.table.TableStatsBuilder;

/**
 * Translates StatsReply messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10StatsReplyMessageFactory implements OFDeserializer<MultipartReplyMessage> {

    private static final int DESC_STR_LEN = 256;
    private static final int SERIAL_NUM_LEN = 32;
    private static final byte PADDING_IN_FLOW_STATS_HEADER = 1;
    private static final byte PADDING_IN_FLOW_STATS_HEADER_02 = 6;
    private static final byte PADDING_IN_AGGREGATE_HEADER = 4;
    private static final byte PADDING_IN_TABLE_HEADER = 3;
    private static final byte MAX_TABLE_NAME_LENGTH = 32;
    private static final byte PADDING_IN_PORT_STATS_HEADER = 6;
    private static final byte PADDING_IN_QUEUE_HEADER = 2;
    private static final byte LENGTH_OF_FLOW_STATS = 88;
    
    private static OF10StatsReplyMessageFactory instance;
    
    private OF10StatsReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10StatsReplyMessageFactory getInstance() {
        if (instance == null){
            instance = new OF10StatsReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public MultipartReplyMessage bufferToMessage(ByteBuf rawMessage, short version) {
        MultipartReplyMessageBuilder builder = new MultipartReplyMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        int type = rawMessage.readUnsignedShort();
        builder.setType(MultipartType.forValue(type));
        builder.setFlags(new MultipartRequestFlags((rawMessage.readUnsignedShort() & 0x01) != 0));
        switch (type) {
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
        case 0xFFFF: builder.setMultipartReplyBody(setExperimenter(rawMessage));
            break;
        default: 
            break;
        }
        return builder.build();
    }
    
    private static MultipartReplyDescCase setDesc(ByteBuf input) {
        MultipartReplyDescCaseBuilder caseBuilder = new MultipartReplyDescCaseBuilder();
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
        caseBuilder.setMultipartReplyDesc(descBuilder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyFlowCase setFlow(ByteBuf input) {
        MultipartReplyFlowCaseBuilder caseBuilder = new MultipartReplyFlowCaseBuilder();
        MultipartReplyFlowBuilder flowBuilder = new MultipartReplyFlowBuilder();
        List<FlowStats> flowStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            FlowStatsBuilder flowStatsBuilder = new FlowStatsBuilder();
            int length = input.readUnsignedShort();
            flowStatsBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_FLOW_STATS_HEADER);
            flowStatsBuilder.setMatchV10(OF10MatchDeserializer.createMatchV10(input));
            flowStatsBuilder.setDurationSec(input.readUnsignedInt());
            flowStatsBuilder.setDurationNsec(input.readUnsignedInt());
            flowStatsBuilder.setPriority(input.readUnsignedShort());
            flowStatsBuilder.setIdleTimeout(input.readUnsignedShort());
            flowStatsBuilder.setHardTimeout(input.readUnsignedShort());
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
            flowStatsBuilder.setActionsList(OF10ActionsDeserializer
                    .createActionsList(input, length - LENGTH_OF_FLOW_STATS));
            flowStatsList.add(flowStatsBuilder.build());
        }
        flowBuilder.setFlowStats(flowStatsList);
        caseBuilder.setMultipartReplyFlow(flowBuilder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyAggregateCase setAggregate(ByteBuf input) {
        MultipartReplyAggregateCaseBuilder caseBuilder = new MultipartReplyAggregateCaseBuilder();
        MultipartReplyAggregateBuilder builder = new MultipartReplyAggregateBuilder();
        byte[] packetCount = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(packetCount);
        builder.setPacketCount(new BigInteger(packetCount));
        byte[] byteCount = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(byteCount);
        builder.setByteCount(new BigInteger(byteCount));
        builder.setFlowCount(input.readUnsignedInt());
        input.skipBytes(PADDING_IN_AGGREGATE_HEADER);
        caseBuilder.setMultipartReplyAggregate(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyTableCase setTable(ByteBuf input) {
        MultipartReplyTableCaseBuilder caseBuilder = new MultipartReplyTableCaseBuilder();
        MultipartReplyTableBuilder builder = new MultipartReplyTableBuilder();
        List<TableStats> tableStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            TableStatsBuilder tableStatsBuilder = new TableStatsBuilder();
            tableStatsBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_TABLE_HEADER);
            byte[] name = new byte[MAX_TABLE_NAME_LENGTH];
            input.readBytes(name);
            tableStatsBuilder.setName(new String(name));
            long wildcards = input.readUnsignedInt();
            tableStatsBuilder.setWildcards(OF10MatchDeserializer.createWildcards(wildcards));
            tableStatsBuilder.setNwSrcMask(OF10MatchDeserializer.decodeNwSrcMask(wildcards));
            tableStatsBuilder.setNwDstMask(OF10MatchDeserializer.decodeNwDstMask(wildcards));
            tableStatsBuilder.setMaxEntries(input.readUnsignedInt());
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
        caseBuilder.setMultipartReplyTable(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyPortStatsCase setPortStats(ByteBuf input) {
        MultipartReplyPortStatsCaseBuilder caseBuilder = new MultipartReplyPortStatsCaseBuilder();
        MultipartReplyPortStatsBuilder builder = new MultipartReplyPortStatsBuilder();
        List<PortStats> portStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            PortStatsBuilder portStatsBuilder = new PortStatsBuilder();
            portStatsBuilder.setPortNo((long) input.readUnsignedShort());
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
        }
        builder.setPortStats(portStatsList);
        caseBuilder.setMultipartReplyPortStats(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyQueueCase setQueue(ByteBuf input) {
        MultipartReplyQueueCaseBuilder caseBuilder = new MultipartReplyQueueCaseBuilder();
        MultipartReplyQueueBuilder builder = new MultipartReplyQueueBuilder();
        List<QueueStats> queueStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            QueueStatsBuilder queueStatsBuilder = new QueueStatsBuilder();
            queueStatsBuilder.setPortNo((long) input.readUnsignedShort());
            input.skipBytes(PADDING_IN_QUEUE_HEADER);
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
            queueStatsList.add(queueStatsBuilder.build());
        }
        builder.setQueueStats(queueStatsList);
        caseBuilder.setMultipartReplyQueue(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyExperimenterCase setExperimenter(ByteBuf input) {
    	MultipartReplyExperimenterCaseBuilder caseBuilder = new MultipartReplyExperimenterCaseBuilder();
        MultipartReplyExperimenterBuilder builder = new MultipartReplyExperimenterBuilder();
        builder.setExperimenter(input.readUnsignedInt());
        byte[] data = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(data);
        builder.setData(data);
        caseBuilder.setMultipartReplyExperimenter(builder.build());
        return caseBuilder.build();
    }
}
