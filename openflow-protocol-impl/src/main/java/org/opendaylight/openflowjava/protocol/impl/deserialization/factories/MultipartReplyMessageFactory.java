/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionsDeserializer;
import org.opendaylight.openflowjava.protocol.impl.util.MatchEntriesDeserializer;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIdsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupCapabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupTypes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.BucketsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.BucketsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDropBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemarkBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.MultipartReplyBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow.FlowStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow.FlowStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.GroupStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.GroupStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc.GroupDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc.GroupDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.group.stats.BucketStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.group.stats.BucketStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.MeterStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.MeterStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config.MeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config.MeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config.meter.config.Bands;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config.meter.config.BandsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.meter.stats.MeterBandStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.meter.stats.MeterBandStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc.Ports;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc.PortsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats.PortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats.PortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue.QueueStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue.QueueStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.TableStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.TableStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.features.TableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeaturePropertiesBuilder;

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
        int type = rawMessage.readUnsignedShort();
        builder.setType(MultipartType.forValue(type));
        builder.setFlags(new MultipartRequestFlags((rawMessage.readUnsignedShort() & 0x01) != 0));
        rawMessage.skipBytes(PADDING_IN_MULTIPART_REPLY_HEADER);

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
        case 6:  builder.setMultipartReplyBody(setGroup(rawMessage));
                 break;
        case 7:  builder.setMultipartReplyBody(setGroupDesc(rawMessage));
                 break;
        case 8:  builder.setMultipartReplyBody(setGroupFeatures(rawMessage));
                 break;
        case 9:  builder.setMultipartReplyBody(setMeter(rawMessage));
                 break;
        case 10: builder.setMultipartReplyBody(setMeterConfig(rawMessage));
                 break;
        case 11: builder.setMultipartReplyBody(setMeterFeatures(rawMessage));
                 break;
        case 12: builder.setMultipartReplyBody(setTableFeatures(rawMessage));
                 break;
        case 13: builder.setMultipartReplyBody(setPortDesc(rawMessage));
                 break;
        case 0xFFFF: builder.setMultipartReplyBody(setExperimenter(rawMessage));
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
        final byte flowLength = 2;
        MultipartReplyFlowBuilder flowBuilder = new MultipartReplyFlowBuilder();
        List<FlowStats> flowStatsList = new ArrayList<>();
        FlowStatsBuilder flowStatsBuilder = new FlowStatsBuilder();
        while (input.readableBytes() > 0) {
            input.skipBytes(flowLength);
            flowStatsBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_FLOW_STATS_HEADER_01);
            flowStatsBuilder.setDurationSec(input.readUnsignedInt());
            flowStatsBuilder.setDurationNsec(input.readUnsignedInt());
            flowStatsBuilder.setPriority(input.readUnsignedShort());
            flowStatsBuilder.setIdleTimeout(input.readUnsignedShort());
            flowStatsBuilder.setHardTimeout(input.readUnsignedShort());
            flowStatsBuilder.setFlags(createFlowModFlagsFromBitmap(input.readShort()));
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
        flowBuilder.setFlowStats(new ArrayList<>(flowStatsList));
        flowStatsList.clear();
        return flowBuilder.build();
    }
    
    private static FlowModFlags createFlowModFlagsFromBitmap(short input){
        final Boolean _oFPFFSENDFLOWREM = (input & (1 << 0)) != 0;
        final Boolean _oFPFFCHECKOVERLAP = (input & (1 << 1)) != 0;
        final Boolean _oFPFFRESETCOUNTS = (input & (1 << 2)) != 0; 
        final Boolean _oFPFFNOPKTCOUNTS = (input & (1 << 3)) != 0;
        final Boolean _oFPFFNOBYTCOUNTS = (input & (1 << 4)) != 0;
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
        builder.setTableStats(new ArrayList<>(tableStatsList));
        tableStatsList.clear();
        return builder.build();
    }
    
    private static MultipartReplyTableFeatures setTableFeatures(ByteBuf input) {
        final byte PADDING_IN_MULTIPART_REPLY_TABLE_FEATURES = 5;
        final byte MAX_TABLE_NAME_LENGTH = 32;
        final byte MULTIPART_REPLY_TABLE_FEATURES_STRUCTURE_LENGTH = 64;
        MultipartReplyTableFeaturesBuilder builder = new MultipartReplyTableFeaturesBuilder();
        List<TableFeatures> features = new ArrayList<>();
        while (input.readableBytes() > 0) {
            TableFeaturesBuilder featuresBuilder = new TableFeaturesBuilder();
            int length = input.readUnsignedShort();
            featuresBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_MULTIPART_REPLY_TABLE_FEATURES);
            featuresBuilder.setName(input.readBytes(MAX_TABLE_NAME_LENGTH).toString());
            byte[] metadataMatch = new byte[Long.SIZE / Byte.SIZE];
            input.readBytes(metadataMatch);
            featuresBuilder.setMetadataMatch(metadataMatch);
            byte[] metadataWrite = new byte[Long.SIZE / Byte.SIZE];
            input.readBytes(metadataWrite);
            featuresBuilder.setMetadataWrite(metadataWrite);
            featuresBuilder.setConfig(createPortConfig(input.readUnsignedInt()));
            featuresBuilder.setMaxEntries(input.readUnsignedInt());
            featuresBuilder.setTableFeatureProperties(createTableFeaturesProperties(input, 
                    length - MULTIPART_REPLY_TABLE_FEATURES_STRUCTURE_LENGTH));
            features.add(featuresBuilder.build());
        }
        builder.setTableFeatures(features);
        return builder.build();
    }
    
    private static List<TableFeatureProperties> createTableFeaturesProperties(ByteBuf input, int length) {
        final byte COMMON_PROPERTY_LENGTH = 4;
        List<TableFeatureProperties> properties = new ArrayList<>();
        int tableFeaturesLength = length;
        while (tableFeaturesLength > 0) {
            TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
            TableFeaturesPropType type = TableFeaturesPropType.forValue(input.readUnsignedShort());
            builder.setType(type);
            int propertyLength = input.readUnsignedShort();
            tableFeaturesLength -= propertyLength;
            if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS)) {
                InstructionsDeserializer.createInstructions(input, propertyLength - COMMON_PROPERTY_LENGTH);
            } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLES)
                    || type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS)) {
                propertyLength -= COMMON_PROPERTY_LENGTH;
                NextTableRelatedTableFeaturePropertyBuilder tableBuilder = new NextTableRelatedTableFeaturePropertyBuilder();
                List<NextTableIds> ids = new ArrayList<>();
                while (propertyLength > 0) {
                    NextTableIdsBuilder nextTableIdsBuilder = new NextTableIdsBuilder();
                    nextTableIdsBuilder.setTableId(input.readUnsignedByte());
                    ids.add(nextTableIdsBuilder.build());
                }
                tableBuilder.setNextTableIds(ids);
                builder.addAugmentation(NextTableRelatedTableFeatureProperty.class, tableBuilder.build());
                properties.add(builder.build());
            } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONSMISS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONSMISS)) {
                ActionsDeserializer.createActionsList(input, propertyLength - COMMON_PROPERTY_LENGTH);
            } else if (type.equals(TableFeaturesPropType.OFPTFPTMATCH)
                    || type.equals(TableFeaturesPropType.OFPTFPTWILDCARDS)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELD)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELDMISS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELDMISS)) {
                MatchEntriesDeserializer.createMatchEntries(input, propertyLength - COMMON_PROPERTY_LENGTH);
            } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTER)
                    || type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
                final byte EXPERIMENTER_PROPERTY_LENGTH = 12;
                ExperimenterRelatedTableFeaturePropertyBuilder expBuilder = new ExperimenterRelatedTableFeaturePropertyBuilder();
                expBuilder.setExperimenter(input.readUnsignedInt());
                expBuilder.setExpType(input.readUnsignedInt());
                byte[] data = new byte[propertyLength - EXPERIMENTER_PROPERTY_LENGTH];
                input.readBytes(data);
                expBuilder.setData(data);
                builder.addAugmentation(ExperimenterRelatedTableFeatureProperty.class, expBuilder.build());
                properties.add(builder.build());
            }
        }
        return properties;
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
        builder.setPortStats(new ArrayList<>(portStatsList));
        portStatsList.clear();
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
        builder.setQueueStats(new ArrayList<>(queueStatsList));
        queueStatsList.clear();
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

                actualLength = actualLength + BUCKET_COUNTER_LENGTH;
            } 
            groupStatsBuilder.setBucketStats(new ArrayList<>(bucketStatsList));
            bucketStatsList.clear();
            groupStatsList.add(groupStatsBuilder.build());
        }
        builder.setGroupStats(new ArrayList<>(groupStatsList));
        groupStatsList.clear();
        return builder.build();
    }
    
    private static MultipartReplyMeterFeatures setMeterFeatures(ByteBuf input) {
        final byte PADDING_IN_METER_FEATURES_HEADER = 2;
        MultipartReplyMeterFeaturesBuilder builder = new MultipartReplyMeterFeaturesBuilder();
        builder.setMaxMeter(input.readUnsignedInt());
        builder.setBandTypes(MeterBandType.forValue(input.readInt()));
        builder.setCapabilities(decodeMeterModFlags((int) input.readUnsignedInt()));
        builder.setMaxBands(input.readUnsignedByte());
        builder.setMaxColor(input.readUnsignedByte());
        input.skipBytes(PADDING_IN_METER_FEATURES_HEADER);
        return builder.build();
    }
    
    private static MeterFlags decodeMeterModFlags(int input){
        final Boolean _oFPMFKBPS = (input & (1 << 0)) != 0;
        final Boolean _oFPMFPKTPS = (input & (1 << 1)) != 0;
        final Boolean _oFPMFBURST = (input & (1 << 2)) != 0; 
        final Boolean _oFPMFSTATS = (input & (1 << 3)) != 0;
        return new MeterFlags(_oFPMFBURST, _oFPMFKBPS, _oFPMFPKTPS, _oFPMFSTATS);
    }
    
    private static MultipartReplyMeter setMeter(ByteBuf input) {
        final byte PADDING_IN_METER_STATS_HEADER = 6;
        final byte METER_BAND_STATS_LENGTH = 16;
        final byte METER_BODY_LENGTH = 40;
        int actualLength;
        MultipartReplyMeterBuilder builder = new MultipartReplyMeterBuilder();
        List<MeterStats> meterStatsList = new ArrayList<>();
        MeterStatsBuilder meterStatsBuilder = new MeterStatsBuilder();
        List<MeterBandStats> meterBandStatsList = new ArrayList<>();
        MeterBandStatsBuilder meterBandStatsBuilder = new MeterBandStatsBuilder();
        while (input.readableBytes() > 0) {
            meterStatsBuilder.setMeterId(input.readUnsignedInt());
            actualLength = 0;
            int meterStatsBodyLength = input.readUnsignedShort();
            input.skipBytes(PADDING_IN_METER_STATS_HEADER);
            meterStatsBuilder.setFlowCount(input.readUnsignedInt());
            byte[] packetInCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(packetInCount);
            meterStatsBuilder.setPacketInCount(new BigInteger(packetInCount));
            byte[] byteInCount = new byte[Long.SIZE/Byte.SIZE];
            input.readBytes(byteInCount);
            meterStatsBuilder.setByteInCount(new BigInteger(byteInCount));
            meterStatsBuilder.setDurationSec(input.readUnsignedInt());
            meterStatsBuilder.setDurationNsec(input.readUnsignedInt());
            actualLength = METER_BODY_LENGTH;
            
            while (actualLength < meterStatsBodyLength) {
                byte[] packetBandCount = new byte[Long.SIZE/Byte.SIZE];
                input.readBytes(packetBandCount);
                meterBandStatsBuilder.setPacketBandCount(new BigInteger(packetBandCount));
                byte[] byteBandCount = new byte[Long.SIZE/Byte.SIZE];
                input.readBytes(byteBandCount);
                meterBandStatsBuilder.setByteBandCount(new BigInteger(byteBandCount));
                
                meterBandStatsList.add(meterBandStatsBuilder.build());
                actualLength = actualLength + METER_BAND_STATS_LENGTH;
            }
        
            meterStatsBuilder.setMeterBandStats(new ArrayList<>(meterBandStatsList));
            meterBandStatsList.clear();
            meterStatsList.add(meterStatsBuilder.build());
        }
        builder.setMeterStats(new ArrayList<>(meterStatsList));
        meterStatsList.clear();
        return builder.build();
    }
    
    private static MultipartReplyMeterConfig setMeterConfig(ByteBuf input) {
        final byte METER_BAND_LENGTH = 16;
        final byte METER_CONFIG_LENGTH = 8;
        final byte bandLength = 2;
        int actualLength;
        MultipartReplyMeterConfigBuilder builder = new MultipartReplyMeterConfigBuilder();
        List<MeterConfig> meterConfigList = new ArrayList<>();
        MeterConfigBuilder meterConfigBuilder = new MeterConfigBuilder();
        List<Bands> bandsList = new ArrayList<>();
        BandsBuilder bandsBuilder = new BandsBuilder();
        
        while (input.readableBytes() > 0) {
            int meterConfigBodyLength = input.readUnsignedShort();
            actualLength = 0;
            meterConfigBuilder.setFlags(MeterModCommand.forValue(input.readUnsignedShort()));
            meterConfigBuilder.setMeterId(input.readUnsignedInt());
            actualLength = METER_CONFIG_LENGTH;
            
            while (actualLength < meterConfigBodyLength) {
                MeterBandDropBuilder bandDropBuilder = new MeterBandDropBuilder();
                final byte PADDING_IN_METER_BAND_DROP_HEADER = 4;
                MeterBandDscpRemarkBuilder bandDscpRemarkBuilder = new MeterBandDscpRemarkBuilder();
                final byte PADDING_IN_METER_BAND_DSCP_HEADER = 3;
                MeterBandExperimenterBuilder bandExperimenterBuilder = new MeterBandExperimenterBuilder(); 
                int bandType = input.readUnsignedShort();
                switch (bandType) {
                    case 1: bandDropBuilder.setType(MeterBandType.forValue(bandType));
                            input.skipBytes(bandLength);
                            bandDropBuilder.setRate(input.readUnsignedInt());
                            bandDropBuilder.setBurstSize(input.readUnsignedInt());
                            input.skipBytes(PADDING_IN_METER_BAND_DROP_HEADER);
                            bandsBuilder.setMeterBand(bandDropBuilder.build());
                            break;
                    case 2: bandDscpRemarkBuilder.setType(MeterBandType.forValue(bandType));
                            input.skipBytes(bandLength);
                            bandDscpRemarkBuilder.setRate(input.readUnsignedInt());
                            bandDscpRemarkBuilder.setBurstSize(input.readUnsignedInt());
                            bandDscpRemarkBuilder.setPrecLevel(input.readUnsignedByte());
                            input.skipBytes(PADDING_IN_METER_BAND_DSCP_HEADER);
                            bandsBuilder.setMeterBand(bandDscpRemarkBuilder.build());
                            break;
                    case 0xFFFF: bandExperimenterBuilder.setType(MeterBandType.forValue(bandType));
                                 input.skipBytes(bandLength);
                                 bandExperimenterBuilder.setRate(input.readUnsignedInt());
                                 bandExperimenterBuilder.setBurstSize(input.readUnsignedInt());
                                 bandExperimenterBuilder.setExperimenter(input.readUnsignedInt());
                                 bandsBuilder.setMeterBand(bandExperimenterBuilder.build());
                                 break;
                    default: break;
                }
                bandsList.add(bandsBuilder.build());
                actualLength = actualLength + METER_BAND_LENGTH;
            }
            meterConfigBuilder.setBands(new ArrayList<>(bandsList));
            bandsList.clear();
            meterConfigList.add(meterConfigBuilder.build());
        }
        builder.setMeterConfig(new ArrayList<>(meterConfigList));
        meterConfigList.clear();
        return builder.build();
    }
    
    private static MultipartReplyExperimenter setExperimenter(ByteBuf input) {
        MultipartReplyExperimenterBuilder builder = new MultipartReplyExperimenterBuilder();
        builder.setExperimenter(input.readUnsignedInt());
        builder.setExpType(input.readUnsignedInt());
        byte[] data = new byte[Long.SIZE/Byte.SIZE];
        input.readBytes(data);
        builder.setData(data);
        return builder.build();
    }
    
    private static MultipartReplyPortDesc setPortDesc(ByteBuf input) {
        final byte PADDING_IN_PORT_DESC_HEADER_01 = 4;
        final byte PADDING_IN_PORT_DESC_HEADER_02 = 2;
        final int macAddressLength = 6;
        final byte MAX_PORT_NAME_LEN = 16;
        MultipartReplyPortDescBuilder builder = new MultipartReplyPortDescBuilder();
        List<Ports> portsList = new ArrayList<>();
        PortsBuilder portsBuilder = new PortsBuilder();
        
        while (input.readableBytes() > 0) {
            portsBuilder.setPortNo(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_01);
            StringBuffer macToString = new StringBuffer();
            for(int i=0; i<macAddressLength; i++){
                short mac = 0;
                mac = input.readUnsignedByte();
                macToString.append(String.format("%02X", mac));
            }
            portsBuilder.setHwAddr(new MacAddress(macToString.toString()));
            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_02);
            
            byte[] portNameBytes = new byte[MAX_PORT_NAME_LEN];
            input.readBytes(portNameBytes);
            String portName = new String(portNameBytes);
            portsBuilder.setName(portName.trim());
            
            portsBuilder.setConfig(createPortConfig(input.readUnsignedInt()));
            portsBuilder.setState(createPortState(input.readUnsignedInt()));
            portsBuilder.setCurrentFeatures(createPortFeatures(input.readUnsignedInt()));
            portsBuilder.setAdvertisedFeatures(createPortFeatures(input.readUnsignedInt()));
            portsBuilder.setSupportedFeatures(createPortFeatures(input.readUnsignedInt()));
            portsBuilder.setPeerFeatures(createPortFeatures(input.readUnsignedInt()));
            portsBuilder.setCurrSpeed(input.readUnsignedInt());
            portsBuilder.setMaxSpeed(input.readUnsignedInt());
            portsList.add(portsBuilder.build());
        }
        
        builder.setPorts(new ArrayList<>(portsList));
        portsList.clear();
        return builder.build();
    }
    
    private static PortConfig createPortConfig(long input){
        final Boolean _portDown   = ((input) & (1<<0)) != 0;
        final Boolean _noRecv    = ((input) & (1<<2)) != 0;
        final Boolean _noFwd       = ((input) & (1<<5)) != 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) != 0;
        return new PortConfig(_noFwd, _noPacketIn, _noRecv, _portDown);
    }
    
    private static PortState createPortState(long input){
        final Boolean _linkDown = ((input) & (1<<0)) != 0;
        final Boolean _blocked  = ((input) & (1<<1)) != 0;
        final Boolean _live     = ((input) & (1<<2)) != 0;
        return new PortState(_linkDown, _blocked,_live);
    }
    
    private static PortFeatures createPortFeatures(long input){
        final Boolean _10mbHd = ((input) & (1<<0)) != 0;
        final Boolean _10mbFd = ((input) & (1<<1)) != 0;
        final Boolean _100mbHd = ((input) & (1<<2)) != 0;
        final Boolean _100mbFd = ((input) & (1<<3)) != 0;
        final Boolean _1gbHd = ((input) & (1<<4)) != 0;
        final Boolean _1gbFd = ((input) & (1<<5)) != 0;
        final Boolean _10gbFd = ((input) & (1<<6)) != 0;
        final Boolean _40gbFd = ((input) & (1<<7)) != 0;
        final Boolean _100gbFd = ((input) & (1<<8)) != 0;
        final Boolean _1tbFd = ((input) & (1<<9)) != 0;
        final Boolean _other = ((input) & (1<<10)) != 0;
        final Boolean _copper = ((input) & (1<<11)) != 0;
        final Boolean _fiber = ((input) & (1<<12)) != 0;
        final Boolean _autoneg = ((input) & (1<<13)) != 0;
        final Boolean _pause = ((input) & (1<<14)) != 0;
        final Boolean _pauseAsym = ((input) & (1<<15)) != 0;
        return new PortFeatures(_10mbHd, _10mbFd, _100mbHd, _100mbFd, _1gbHd, _1gbFd, _10gbFd,
                _40gbFd, _100gbFd, _1tbFd, _other, _copper, _fiber, _autoneg, _pause, _pauseAsym);
    }
    
    private static MultipartReplyBody setGroupFeatures(ByteBuf rawMessage) {
        final int SIZE_OF_MAX_GROUPS = 4;
        MultipartReplyGroupFeaturesBuilder featuresBuilder = new MultipartReplyGroupFeaturesBuilder();
        featuresBuilder.setTypes(createGroupType(rawMessage.readUnsignedInt()));
        featuresBuilder.setCapabilities(createCapabilities(rawMessage.readUnsignedInt()));
        List<Long> maxGroupsList = new ArrayList<>();
        for (int i = 0; i < SIZE_OF_MAX_GROUPS ; i++) {
            maxGroupsList.add(rawMessage.readUnsignedInt());
        }
        featuresBuilder.setMaxGroups(maxGroupsList);
        // TODO - groupfeatures - actions bitmap
        rawMessage.skipBytes(4);
        return featuresBuilder.build();
    }

    private static GroupCapabilities createCapabilities(long input) {
        final Boolean OFOFPGFC_SELECT_WEIGHT = ((input) & (1<<0)) != 0;
        final Boolean OFPGFC_SELECT_LIVENESS = ((input) & (1<<1)) != 0;
        final Boolean OFPGFC_CHAINING = ((input) & (1<<2)) != 0;
        final Boolean OFPGFC_CHAINING_CHECKS = ((input) & (1<<3)) != 0;
        return new GroupCapabilities(OFPGFC_CHAINING, OFPGFC_CHAINING_CHECKS, OFPGFC_SELECT_LIVENESS, OFOFPGFC_SELECT_WEIGHT);
    }

    private static GroupTypes createGroupType(long input) {
        final Boolean OFPGT_ALL = ((input) & (1<<0)) != 0;
        final Boolean OFPGT_SELECT = ((input) & (1<<1)) != 0;
        final Boolean OFPGT_INDIRECT = ((input) & (1<<2)) != 0;
        final Boolean OFPGT_FF = ((input) & (1<<3)) != 0;
        return new GroupTypes(OFPGT_ALL, OFPGT_FF, OFPGT_INDIRECT, OFPGT_SELECT);
    }
    
    private static MultipartReplyGroupDesc setGroupDesc(ByteBuf input) {
        final byte PADDING_IN_GROUP_DESC_HEADER = 1;
        final byte PADDING_IN_BUCKETS_HEADER = 4;
        final byte GROUP_DESC_HEADER_LENGTH = 8;
        int actualLength;
        int bucketsLength = 0;
        int bucketsCurrentLength = 0;
        int bodyLength = 0;
        MultipartReplyGroupDescBuilder builder = new MultipartReplyGroupDescBuilder();
        GroupDescBuilder groupDescBuilder = new GroupDescBuilder();
        List<GroupDesc> groupDescsList = new ArrayList<>();
        BucketsListBuilder bucketsBuilder = new BucketsListBuilder();
        List<BucketsList> bucketsList = new ArrayList<>();
        List<ActionsList> actionsList = new ArrayList<>();
        
        while (input.readableBytes() > 0) {
            bodyLength = input.readUnsignedShort();
            actualLength = 0;
            
            groupDescBuilder.setType(GroupType.forValue(input.readUnsignedByte())); // TODO enum or class?
            input.skipBytes(PADDING_IN_GROUP_DESC_HEADER);
            groupDescBuilder.setGroupId(input.readUnsignedInt());
            actualLength = GROUP_DESC_HEADER_LENGTH;
            
            while (actualLength < bodyLength) {
                
                bucketsLength = input.readUnsignedShort();
                bucketsBuilder.setWeight(input.readUnsignedShort());
                bucketsBuilder.setWatchPort(new PortNumber(input.readUnsignedInt()));
                bucketsBuilder.setWatchGroup(input.readUnsignedInt());
                input.skipBytes(PADDING_IN_BUCKETS_HEADER);
                bucketsCurrentLength = bucketsLength;
                actionsList = ActionsDeserializer.createActionsList(input, bucketsLength);
                bucketsBuilder.setActionsList(new ArrayList<>(actionsList));
                actionsList.clear();
                bucketsList.add(bucketsBuilder.build());
                actualLength = actualLength + bucketsCurrentLength;
            }
            groupDescBuilder.setBucketsList(new ArrayList<>(bucketsList));
            bucketsList.clear();
            groupDescsList.add(groupDescBuilder.build());
        }
        builder.setGroupDesc(new ArrayList<>(groupDescsList));
        groupDescsList.clear();
        return builder.build();
    }
    
}
