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

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.CodeKeyMaker;
import org.opendaylight.openflowjava.protocol.impl.util.CodeKeyMakerFactory;
import org.opendaylight.openflowjava.protocol.impl.util.ListDeserializer;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.openflowjava.util.ExperimenterDeserializerKeyFactory;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIdsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ActionType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupCapabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupTypes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterBandTypeBitmap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.grouping.BucketsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.buckets.grouping.BucketsListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDropCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandDscpRemarkCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.drop._case.MeterBandDropBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.meter.band.dscp.remark._case.MeterBandDscpRemarkBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyAggregateCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyFlowCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyGroupFeaturesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfigCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterConfigCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyMeterFeaturesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyPortStatsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyQueueCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.MultipartReplyTableFeaturesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.aggregate._case.MultipartReplyAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.desc._case.MultipartReplyDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.MultipartReplyFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.multipart.reply.flow.FlowStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.flow._case.multipart.reply.flow.FlowStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.MultipartReplyGroupBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.multipart.reply.group.GroupStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.multipart.reply.group.GroupStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.multipart.reply.group.group.stats.BucketStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group._case.multipart.reply.group.group.stats.BucketStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc._case.MultipartReplyGroupDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc._case.multipart.reply.group.desc.GroupDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.desc._case.multipart.reply.group.desc.GroupDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.group.features._case.MultipartReplyGroupFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.MultipartReplyMeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.multipart.reply.meter.MeterStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.multipart.reply.meter.MeterStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.multipart.reply.meter.meter.stats.MeterBandStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter._case.multipart.reply.meter.meter.stats.MeterBandStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.MultipartReplyMeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.multipart.reply.meter.config.MeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.multipart.reply.meter.config.MeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.multipart.reply.meter.config.meter.config.Bands;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.config._case.multipart.reply.meter.config.meter.config.BandsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.meter.features._case.MultipartReplyMeterFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.MultipartReplyPortDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.multipart.reply.port.desc.Ports;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.desc._case.multipart.reply.port.desc.PortsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.MultipartReplyPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.port.stats._case.multipart.reply.port.stats.PortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.MultipartReplyQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.multipart.reply.queue.QueueStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.queue._case.multipart.reply.queue.QueueStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.MultipartReplyTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.multipart.reply.table.TableStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table._case.multipart.reply.table.TableStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.features._case.MultipartReplyTableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.features._case.multipart.reply.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.reply.multipart.reply.body.multipart.reply.table.features._case.multipart.reply.table.features.TableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeaturePropertiesBuilder;

/**
 * Translates MultipartReply messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactory implements OFDeserializer<MultipartReplyMessage>,
        DeserializerRegistryInjector {

    private static final byte PADDING_IN_MULTIPART_REPLY_HEADER = 4;
    private static final int DESC_STR_LEN = 256;
    private static final int SERIAL_NUM_LEN = 32;
    private static final byte PADDING_IN_FLOW_STATS_HEADER_01 = 1;
    private static final byte PADDING_IN_FLOW_STATS_HEADER_02 = 4;
    private static final byte PADDING_IN_AGGREGATE_HEADER = 4;
    private static final byte PADDING_IN_TABLE_HEADER = 3;
    private static final byte PADDING_IN_MULTIPART_REPLY_TABLE_FEATURES = 5;
    private static final byte MAX_TABLE_NAME_LENGTH = 32;
    private static final byte MULTIPART_REPLY_TABLE_FEATURES_STRUCTURE_LENGTH = 64;
    private static final byte COMMON_PROPERTY_LENGTH = 4;
    private static final byte PADDING_IN_PORT_STATS_HEADER = 4;
    private static final byte PADDING_IN_GROUP_HEADER_01 = 2;
    private static final byte PADDING_IN_GROUP_HEADER_02 = 4;
    private static final byte BUCKET_COUNTER_LENGTH = 16;
    private static final byte GROUP_BODY_LENGTH = 40;
    private static final byte PADDING_IN_METER_FEATURES_HEADER = 2;
    private static final byte PADDING_IN_METER_STATS_HEADER = 6;
    private static final byte METER_BAND_STATS_LENGTH = 16;
    private static final byte METER_BODY_LENGTH = 40;
    private static final byte METER_CONFIG_LENGTH = 8;
    private static final byte PADDING_IN_METER_BAND_DROP_HEADER = 4;
    private static final byte PADDING_IN_METER_BAND_DSCP_HEADER = 3;
    private static final byte PADDING_IN_PORT_DESC_HEADER_01 = 4;
    private static final byte PADDING_IN_PORT_DESC_HEADER_02 = 2;
    private static final int GROUP_TYPES = 4;
    private static final byte PADDING_IN_GROUP_DESC_HEADER = 1;
    private static final byte PADDING_IN_BUCKETS_HEADER = 4;
    private static final byte GROUP_DESC_HEADER_LENGTH = 8;
    private static final byte BUCKETS_HEADER_LENGTH = 16;
    private DeserializerRegistry registry;

    @Override
    public MultipartReplyMessage deserialize(ByteBuf rawMessage) {
        MultipartReplyMessageBuilder builder = new MultipartReplyMessageBuilder();
        builder.setVersion((short) EncodeConstants.OF13_VERSION_ID);
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
    
    private MultipartReplyFlowCase setFlow(ByteBuf input) {
        MultipartReplyFlowCaseBuilder caseBuilder = new MultipartReplyFlowCaseBuilder();
        MultipartReplyFlowBuilder flowBuilder = new MultipartReplyFlowBuilder();
        List<FlowStats> flowStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            FlowStatsBuilder flowStatsBuilder = new FlowStatsBuilder();
            int flowRecordLength = input.readUnsignedShort();
            ByteBuf subInput = input.readSlice(flowRecordLength - EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
            flowStatsBuilder.setTableId(subInput.readUnsignedByte());
            subInput.skipBytes(PADDING_IN_FLOW_STATS_HEADER_01);
            flowStatsBuilder.setDurationSec(subInput.readUnsignedInt());
            flowStatsBuilder.setDurationNsec(subInput.readUnsignedInt());
            flowStatsBuilder.setPriority(subInput.readUnsignedShort());
            flowStatsBuilder.setIdleTimeout(subInput.readUnsignedShort());
            flowStatsBuilder.setHardTimeout(subInput.readUnsignedShort());
            flowStatsBuilder.setFlags(createFlowModFlagsFromBitmap(subInput.readUnsignedShort()));
            subInput.skipBytes(PADDING_IN_FLOW_STATS_HEADER_02);
            byte[] cookie = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            subInput.readBytes(cookie);
            flowStatsBuilder.setCookie(new BigInteger(1, cookie));
            byte[] packetCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            subInput.readBytes(packetCount);
            flowStatsBuilder.setPacketCount(new BigInteger(1, packetCount));
            byte[] byteCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            subInput.readBytes(byteCount);
            flowStatsBuilder.setByteCount(new BigInteger(1, byteCount));
            OFDeserializer<Match> matchDeserializer = registry.getDeserializer(new MessageCodeKey(
                    EncodeConstants.OF13_VERSION_ID, EncodeConstants.EMPTY_VALUE, Match.class));
            flowStatsBuilder.setMatch(matchDeserializer.deserialize(subInput));
            CodeKeyMaker keyMaker = CodeKeyMakerFactory
                    .createInstructionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
            List<Instruction> instructions = ListDeserializer.deserializeList(
                    EncodeConstants.OF13_VERSION_ID, subInput.readableBytes(), subInput, keyMaker, registry);
            flowStatsBuilder.setInstruction(instructions);
            flowStatsList.add(flowStatsBuilder.build());
        }
        flowBuilder.setFlowStats(flowStatsList);
        caseBuilder.setMultipartReplyFlow(flowBuilder.build());
        return caseBuilder.build();
    }
    
    private static FlowModFlags createFlowModFlagsFromBitmap(int input){
        final Boolean _oFPFFSENDFLOWREM = (input & (1 << 0)) != 0;
        final Boolean _oFPFFCHECKOVERLAP = (input & (1 << 1)) != 0;
        final Boolean _oFPFFRESETCOUNTS = (input & (1 << 2)) != 0; 
        final Boolean _oFPFFNOPKTCOUNTS = (input & (1 << 3)) != 0;
        final Boolean _oFPFFNOBYTCOUNTS = (input & (1 << 4)) != 0;
        return new FlowModFlags(_oFPFFCHECKOVERLAP, _oFPFFNOBYTCOUNTS, _oFPFFNOPKTCOUNTS, _oFPFFRESETCOUNTS, _oFPFFSENDFLOWREM);
    }
    
    private static MultipartReplyAggregateCase setAggregate(ByteBuf input) {
        MultipartReplyAggregateCaseBuilder caseBuilder = new MultipartReplyAggregateCaseBuilder();
        MultipartReplyAggregateBuilder builder = new MultipartReplyAggregateBuilder();
        byte[] packetCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(packetCount);
        builder.setPacketCount(new BigInteger(1, packetCount));
        byte[] byteCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        input.readBytes(byteCount);
        builder.setByteCount(new BigInteger(1, byteCount));
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
            tableStatsBuilder.setActiveCount(input.readUnsignedInt());
            byte[] lookupCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(lookupCount);
            tableStatsBuilder.setLookupCount(new BigInteger(1, lookupCount));
            byte[] matchedCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(matchedCount);
            tableStatsBuilder.setMatchedCount(new BigInteger(1, matchedCount));
            tableStatsList.add(tableStatsBuilder.build());
        }
        builder.setTableStats(tableStatsList);
        caseBuilder.setMultipartReplyTable(builder.build());
        return caseBuilder.build();
    }
    
    private MultipartReplyTableFeaturesCase setTableFeatures(ByteBuf input) {
        MultipartReplyTableFeaturesCaseBuilder caseBuilder = new MultipartReplyTableFeaturesCaseBuilder();
        MultipartReplyTableFeaturesBuilder builder = new MultipartReplyTableFeaturesBuilder();
        List<TableFeatures> features = new ArrayList<>();
        while (input.readableBytes() > 0) {
            TableFeaturesBuilder featuresBuilder = new TableFeaturesBuilder();
            int length = input.readUnsignedShort();
            featuresBuilder.setTableId(input.readUnsignedByte());
            input.skipBytes(PADDING_IN_MULTIPART_REPLY_TABLE_FEATURES);
            featuresBuilder.setName(ByteBufUtils.decodeNullTerminatedString(input, MAX_TABLE_NAME_LENGTH));
            byte[] metadataMatch = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(metadataMatch);
            featuresBuilder.setMetadataMatch(metadataMatch);
            byte[] metadataWrite = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(metadataWrite);
            featuresBuilder.setMetadataWrite(metadataWrite);
            featuresBuilder.setConfig(createTableConfig(input.readUnsignedInt()));
            featuresBuilder.setMaxEntries(input.readUnsignedInt());
            featuresBuilder.setTableFeatureProperties(createTableFeaturesProperties(input, 
                    length - MULTIPART_REPLY_TABLE_FEATURES_STRUCTURE_LENGTH));
            features.add(featuresBuilder.build());
        }
        builder.setTableFeatures(features);
        caseBuilder.setMultipartReplyTableFeatures(builder.build());
        return caseBuilder.build();
    }
    
    private static TableConfig createTableConfig(long input) {
        boolean deprecated = (input & 3) != 0;
        return new TableConfig(deprecated);
    }
    
    private List<TableFeatureProperties> createTableFeaturesProperties(ByteBuf input, int length) {
        List<TableFeatureProperties> properties = new ArrayList<>();
        int tableFeaturesLength = length;
        while (tableFeaturesLength > 0) {
            int propStartIndex = input.readerIndex();
            TableFeaturePropertiesBuilder builder = new TableFeaturePropertiesBuilder();
            TableFeaturesPropType type = TableFeaturesPropType.forValue(input.readUnsignedShort());
            builder.setType(type);
            int propertyLength = input.readUnsignedShort();
            int paddingRemainder = propertyLength % EncodeConstants.PADDING;
            tableFeaturesLength -= propertyLength;
            if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS)) {
                InstructionRelatedTableFeaturePropertyBuilder insBuilder = new InstructionRelatedTableFeaturePropertyBuilder();
                CodeKeyMaker keyMaker = CodeKeyMakerFactory.createInstructionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
                List<Instruction> instructions = ListDeserializer.deserializeHeaders(EncodeConstants.OF13_VERSION_ID,
                        propertyLength - COMMON_PROPERTY_LENGTH, input, keyMaker, registry);
                insBuilder.setInstruction(instructions);
                builder.addAugmentation(InstructionRelatedTableFeatureProperty.class, insBuilder.build());
            } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLES)
                    || type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS)) {
                propertyLength -= COMMON_PROPERTY_LENGTH;
                NextTableRelatedTableFeaturePropertyBuilder tableBuilder = new NextTableRelatedTableFeaturePropertyBuilder();
                List<NextTableIds> ids = new ArrayList<>();
                while (propertyLength > 0) {
                    NextTableIdsBuilder nextTableIdsBuilder = new NextTableIdsBuilder();
                    nextTableIdsBuilder.setTableId(input.readUnsignedByte());
                    ids.add(nextTableIdsBuilder.build());
                    propertyLength--;
                }
                tableBuilder.setNextTableIds(ids);
                builder.addAugmentation(NextTableRelatedTableFeatureProperty.class, tableBuilder.build());
            } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONSMISS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONSMISS)) {
                ActionRelatedTableFeaturePropertyBuilder actionBuilder = new ActionRelatedTableFeaturePropertyBuilder();
                CodeKeyMaker keyMaker = CodeKeyMakerFactory.createActionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
                List<Action> actions = ListDeserializer.deserializeHeaders(EncodeConstants.OF13_VERSION_ID,
                        propertyLength - COMMON_PROPERTY_LENGTH, input, keyMaker, registry);
                actionBuilder.setAction(actions);
                builder.addAugmentation(ActionRelatedTableFeatureProperty.class, actionBuilder.build());
            } else if (type.equals(TableFeaturesPropType.OFPTFPTMATCH)
                    || type.equals(TableFeaturesPropType.OFPTFPTWILDCARDS)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELD)
                    || type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELDMISS)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD)
                    || type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELDMISS)) {
                OxmRelatedTableFeaturePropertyBuilder oxmBuilder = new OxmRelatedTableFeaturePropertyBuilder();
                CodeKeyMaker keyMaker = CodeKeyMakerFactory
                        .createMatchEntriesKeyMaker(EncodeConstants.OF13_VERSION_ID);
                List<MatchEntries> entries = ListDeserializer.deserializeHeaders(EncodeConstants.OF13_VERSION_ID,
                        propertyLength - COMMON_PROPERTY_LENGTH, input, keyMaker, registry);
                oxmBuilder.setMatchEntries(entries);
                builder.addAugmentation(OxmRelatedTableFeatureProperty.class, oxmBuilder.build());
            } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTER)
                    || type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
                long expId = input.readUnsignedInt();
                input.readerIndex(propStartIndex);
                OFDeserializer<TableFeatureProperties> propDeserializer = registry.getDeserializer(
                        ExperimenterDeserializerKeyFactory.createMultipartReplyTFDeserializerKey(
                                EncodeConstants.OF13_VERSION_ID, expId));
                TableFeatureProperties expProp = propDeserializer.deserialize(input);
                properties.add(expProp);
                continue;
            }
            if (paddingRemainder != 0) {
                input.skipBytes(EncodeConstants.PADDING - paddingRemainder);
                tableFeaturesLength -= EncodeConstants.PADDING - paddingRemainder;
            }
            properties.add(builder.build());
        }
        return properties;
    }
    
    private static MultipartReplyPortStatsCase setPortStats(ByteBuf input) {
        MultipartReplyPortStatsCaseBuilder caseBuilder = new MultipartReplyPortStatsCaseBuilder();
        MultipartReplyPortStatsBuilder builder = new MultipartReplyPortStatsBuilder();
        List<PortStats> portStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            PortStatsBuilder portStatsBuilder = new PortStatsBuilder();
            portStatsBuilder.setPortNo(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_PORT_STATS_HEADER);
            byte[] rxPackets = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxPackets);
            portStatsBuilder.setRxPackets(new BigInteger(1, rxPackets));
            byte[] txPackets = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txPackets);
            portStatsBuilder.setTxPackets(new BigInteger(1, txPackets));
            byte[] rxBytes = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxBytes);
            portStatsBuilder.setRxBytes(new BigInteger(1, rxBytes));
            byte[] txBytes = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txBytes);
            portStatsBuilder.setTxBytes(new BigInteger(1, txBytes));
            byte[] rxDropped = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxDropped);
            portStatsBuilder.setRxDropped(new BigInteger(1, rxDropped));
            byte[] txDropped = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txDropped);
            portStatsBuilder.setTxDropped(new BigInteger(1, txDropped));
            byte[] rxErrors = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxErrors);
            portStatsBuilder.setRxErrors(new BigInteger(1, rxErrors));
            byte[] txErrors = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txErrors);
            portStatsBuilder.setTxErrors(new BigInteger(1, txErrors));
            byte[] rxFrameErr = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxFrameErr);
            portStatsBuilder.setRxFrameErr(new BigInteger(1, rxFrameErr));
            byte[] rxOverErr = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxOverErr);
            portStatsBuilder.setRxOverErr(new BigInteger(1, rxOverErr));
            byte[] rxCrcErr = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(rxCrcErr);
            portStatsBuilder.setRxCrcErr(new BigInteger(1, rxCrcErr));
            byte[] collisions = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(collisions);
            portStatsBuilder.setCollisions(new BigInteger(1, collisions));
            portStatsBuilder.setDurationSec(input.readUnsignedInt());
            portStatsBuilder.setDurationNsec(input.readUnsignedInt());
            portStatsList.add(portStatsBuilder.build());
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
            queueStatsBuilder.setPortNo(input.readUnsignedInt());
            queueStatsBuilder.setQueueId(input.readUnsignedInt());
            byte[] txBytes = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txBytes);
            queueStatsBuilder.setTxBytes(new BigInteger(1, txBytes));
            byte[] txPackets = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txPackets);
            queueStatsBuilder.setTxPackets(new BigInteger(1, txPackets));
            byte[] txErrors = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(txErrors);
            queueStatsBuilder.setTxErrors(new BigInteger(1, txErrors));
            queueStatsBuilder.setDurationSec(input.readUnsignedInt());
            queueStatsBuilder.setDurationNsec(input.readUnsignedInt());
            queueStatsList.add(queueStatsBuilder.build());
        }
        builder.setQueueStats(queueStatsList);
        caseBuilder.setMultipartReplyQueue(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyGroupCase setGroup(ByteBuf input) {
        MultipartReplyGroupCaseBuilder caseBuilder = new MultipartReplyGroupCaseBuilder();
        MultipartReplyGroupBuilder builder = new MultipartReplyGroupBuilder();
        List<GroupStats> groupStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            GroupStatsBuilder groupStatsBuilder = new GroupStatsBuilder();
            int bodyLength = input.readUnsignedShort();
            input.skipBytes(PADDING_IN_GROUP_HEADER_01);
            groupStatsBuilder.setGroupId(new GroupId(input.readUnsignedInt()));
            groupStatsBuilder.setRefCount(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_GROUP_HEADER_02);
            byte[] packetCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(packetCount);
            groupStatsBuilder.setPacketCount(new BigInteger(1, packetCount));
            byte[] byteCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(byteCount);
            groupStatsBuilder.setByteCount(new BigInteger(1, byteCount));
            groupStatsBuilder.setDurationSec(input.readUnsignedInt());
            groupStatsBuilder.setDurationNsec(input.readUnsignedInt());
            int actualLength = GROUP_BODY_LENGTH;
            List<BucketStats> bucketStatsList = new ArrayList<>();
            while (actualLength < bodyLength) {
                BucketStatsBuilder bucketStatsBuilder = new BucketStatsBuilder();
                byte[] packetCountBucket = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
                input.readBytes(packetCountBucket);
                bucketStatsBuilder.setPacketCount(new BigInteger(1, packetCountBucket));
                byte[] byteCountBucket = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
                input.readBytes(byteCountBucket);
                bucketStatsBuilder.setByteCount(new BigInteger(1, byteCountBucket));
                bucketStatsList.add(bucketStatsBuilder.build());
                actualLength += BUCKET_COUNTER_LENGTH;
            } 
            groupStatsBuilder.setBucketStats(bucketStatsList);
            groupStatsList.add(groupStatsBuilder.build());
        }
        builder.setGroupStats(groupStatsList);
        caseBuilder.setMultipartReplyGroup(builder.build());
        return caseBuilder.build();
    }
    
    private static MultipartReplyMeterFeaturesCase setMeterFeatures(ByteBuf input) {
        MultipartReplyMeterFeaturesCaseBuilder caseBuilder = new MultipartReplyMeterFeaturesCaseBuilder();
        MultipartReplyMeterFeaturesBuilder builder = new MultipartReplyMeterFeaturesBuilder();
        builder.setMaxMeter(input.readUnsignedInt());
        builder.setBandTypes(createMeterBandsBitmap(input.readUnsignedInt()));
        builder.setCapabilities(createMeterFlags(input.readUnsignedInt()));
        builder.setMaxBands(input.readUnsignedByte());
        builder.setMaxColor(input.readUnsignedByte());
        input.skipBytes(PADDING_IN_METER_FEATURES_HEADER);
        caseBuilder.setMultipartReplyMeterFeatures(builder.build());
        return caseBuilder.build();
    }
    
    private static MeterFlags createMeterFlags(long input){
        final Boolean _oFPMFKBPS = (input & (1 << 0)) != 0;
        final Boolean _oFPMFPKTPS = (input & (1 << 1)) != 0;
        final Boolean _oFPMFBURST = (input & (1 << 2)) != 0;
        final Boolean _oFPMFSTATS = (input & (1 << 3)) != 0;
        return new MeterFlags(_oFPMFBURST, _oFPMFKBPS, _oFPMFPKTPS, _oFPMFSTATS);
    }
    
    private static MeterBandTypeBitmap createMeterBandsBitmap(long input) {
        final Boolean _oFPMBTDROP = (input & (1 << 1)) != 0;
        final Boolean _oFPMBTDSCPREMARK = (input & (1 << 2)) != 0;
        return new MeterBandTypeBitmap(_oFPMBTDROP, _oFPMBTDSCPREMARK);
    }
    
    private static MultipartReplyMeterCase setMeter(ByteBuf input) {
        MultipartReplyMeterCaseBuilder caseBuilder = new MultipartReplyMeterCaseBuilder();
        MultipartReplyMeterBuilder builder = new MultipartReplyMeterBuilder();
        List<MeterStats> meterStatsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            MeterStatsBuilder meterStatsBuilder = new MeterStatsBuilder();
            meterStatsBuilder.setMeterId(new MeterId(input.readUnsignedInt()));
            int meterStatsBodyLength = input.readUnsignedShort();
            input.skipBytes(PADDING_IN_METER_STATS_HEADER);
            meterStatsBuilder.setFlowCount(input.readUnsignedInt());
            byte[] packetInCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(packetInCount);
            meterStatsBuilder.setPacketInCount(new BigInteger(1, packetInCount));
            byte[] byteInCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
            input.readBytes(byteInCount);
            meterStatsBuilder.setByteInCount(new BigInteger(1, byteInCount));
            meterStatsBuilder.setDurationSec(input.readUnsignedInt());
            meterStatsBuilder.setDurationNsec(input.readUnsignedInt());
            int actualLength = METER_BODY_LENGTH;
            List<MeterBandStats> meterBandStatsList = new ArrayList<>();
            while (actualLength < meterStatsBodyLength) {
                MeterBandStatsBuilder meterBandStatsBuilder = new MeterBandStatsBuilder();
                byte[] packetBandCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
                input.readBytes(packetBandCount);
                meterBandStatsBuilder.setPacketBandCount(new BigInteger(1, packetBandCount));
                byte[] byteBandCount = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
                input.readBytes(byteBandCount);
                meterBandStatsBuilder.setByteBandCount(new BigInteger(1, byteBandCount));
                meterBandStatsList.add(meterBandStatsBuilder.build());
                actualLength += METER_BAND_STATS_LENGTH;
            }
            meterStatsBuilder.setMeterBandStats(meterBandStatsList);
            meterStatsList.add(meterStatsBuilder.build());
        }
        builder.setMeterStats(meterStatsList);
        caseBuilder.setMultipartReplyMeter(builder.build());
        return caseBuilder.build();
    }
    
    private MultipartReplyMeterConfigCase setMeterConfig(ByteBuf input) {
        MultipartReplyMeterConfigCaseBuilder caseBuilder = new MultipartReplyMeterConfigCaseBuilder();
        MultipartReplyMeterConfigBuilder builder = new MultipartReplyMeterConfigBuilder();
        List<MeterConfig> meterConfigList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            MeterConfigBuilder meterConfigBuilder = new MeterConfigBuilder();
            int meterConfigBodyLength = input.readUnsignedShort();
            meterConfigBuilder.setFlags(createMeterFlags(input.readUnsignedShort()));
            meterConfigBuilder.setMeterId(new MeterId(input.readUnsignedInt()));
            int actualLength = METER_CONFIG_LENGTH;
            List<Bands> bandsList = new ArrayList<>();
            while (actualLength < meterConfigBodyLength) {
                int bandStartIndex = input.readerIndex();
                BandsBuilder bandsBuilder = new BandsBuilder();
                int bandType = input.readUnsignedShort();
                switch (bandType) {
                    case 1:
                    	MeterBandDropCaseBuilder bandDropCaseBuilder = new MeterBandDropCaseBuilder();
                        MeterBandDropBuilder bandDropBuilder = new MeterBandDropBuilder();
                        bandDropBuilder.setType(MeterBandType.forValue(bandType));
                        actualLength += input.readUnsignedShort();
                        bandDropBuilder.setRate(input.readUnsignedInt());
                        bandDropBuilder.setBurstSize(input.readUnsignedInt());
                        input.skipBytes(PADDING_IN_METER_BAND_DROP_HEADER);
                        bandDropCaseBuilder.setMeterBandDrop(bandDropBuilder.build());
                        bandsBuilder.setMeterBand(bandDropCaseBuilder.build());
                        break;
                    case 2:
                    	MeterBandDscpRemarkCaseBuilder bandDscpRemarkCaseBuilder = new MeterBandDscpRemarkCaseBuilder();
                        MeterBandDscpRemarkBuilder bandDscpRemarkBuilder = new MeterBandDscpRemarkBuilder();
                        bandDscpRemarkBuilder.setType(MeterBandType.forValue(bandType));
                        actualLength += input.readUnsignedShort();
                        bandDscpRemarkBuilder.setRate(input.readUnsignedInt());
                        bandDscpRemarkBuilder.setBurstSize(input.readUnsignedInt());
                        bandDscpRemarkBuilder.setPrecLevel(input.readUnsignedByte());
                        input.skipBytes(PADDING_IN_METER_BAND_DSCP_HEADER);
                        bandDscpRemarkCaseBuilder.setMeterBandDscpRemark(bandDscpRemarkBuilder.build());
                        bandsBuilder.setMeterBand(bandDscpRemarkCaseBuilder.build());
                        break;
                    case 0xFFFF:
                        actualLength += input.readUnsignedShort();
                        long expId = input.getUnsignedInt(input.readerIndex() + 2 * EncodeConstants.SIZE_OF_INT_IN_BYTES);
                        input.readerIndex(bandStartIndex);
                        OFDeserializer<MeterBandExperimenterCase> deserializer = registry.getDeserializer(
                                ExperimenterDeserializerKeyFactory.createMeterBandDeserializerKey(
                                        EncodeConstants.OF13_VERSION_ID, expId));
                        bandsBuilder.setMeterBand(deserializer.deserialize(input));
                        break;
                    default:
                        break;
                }
                bandsList.add(bandsBuilder.build());
            }
            meterConfigBuilder.setBands(bandsList);
            meterConfigList.add(meterConfigBuilder.build());
        }
        builder.setMeterConfig(meterConfigList);
        caseBuilder.setMultipartReplyMeterConfig(builder.build());
        return caseBuilder.build();
    }
    
    private MultipartReplyExperimenterCase setExperimenter(ByteBuf input) {
        return registry.getDeserializer(ExperimenterDeserializerKeyFactory.createMultipartReplyMessageDeserializerKey(
                EncodeConstants.OF13_VERSION_ID, input.readUnsignedInt()));
    }
    
    private static MultipartReplyPortDescCase setPortDesc(ByteBuf input) {
        MultipartReplyPortDescCaseBuilder caseBuilder = new MultipartReplyPortDescCaseBuilder();
        MultipartReplyPortDescBuilder builder = new MultipartReplyPortDescBuilder();
        List<Ports> portsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            PortsBuilder portsBuilder = new PortsBuilder();
            portsBuilder.setPortNo(input.readUnsignedInt());
            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_01);
            byte[] hwAddress = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
            input.readBytes(hwAddress);
            portsBuilder.setHwAddr(new MacAddress(ByteBufUtils.macAddressToString(hwAddress)));
            input.skipBytes(PADDING_IN_PORT_DESC_HEADER_02);
            portsBuilder.setName(ByteBufUtils.decodeNullTerminatedString(input, EncodeConstants.MAX_PORT_NAME_LENGTH));
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
        builder.setPorts(portsList);
        caseBuilder.setMultipartReplyPortDesc(builder.build());
        return caseBuilder.build();
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
        return new PortState(_blocked, _linkDown, _live);
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
        return new PortFeatures(_100gbFd, _100mbFd, _100mbHd, _10gbFd, _10mbFd, _10mbHd, _1gbFd,
                _1gbHd, _1tbFd, _40gbFd, _autoneg, _copper, _fiber, _other, _pause, _pauseAsym);
    }
    
    private static MultipartReplyGroupFeaturesCase setGroupFeatures(ByteBuf rawMessage) {
        MultipartReplyGroupFeaturesCaseBuilder caseBuilder = new MultipartReplyGroupFeaturesCaseBuilder();
        MultipartReplyGroupFeaturesBuilder featuresBuilder = new MultipartReplyGroupFeaturesBuilder();
        featuresBuilder.setTypes(createGroupType(rawMessage.readUnsignedInt()));
        featuresBuilder.setCapabilities(createCapabilities(rawMessage.readUnsignedInt()));
        List<Long> maxGroupsList = new ArrayList<>();
        for (int i = 0; i < GROUP_TYPES ; i++) {
            maxGroupsList.add(rawMessage.readUnsignedInt());
        }
        featuresBuilder.setMaxGroups(maxGroupsList);
        List<ActionType> actionBitmaps = new ArrayList<>();
        for (int i = 0; i < GROUP_TYPES ; i++) {
            actionBitmaps.add(createActionBitmap(rawMessage.readUnsignedInt()));
        }
        featuresBuilder.setActionsBitmap(actionBitmaps);
        caseBuilder.setMultipartReplyGroupFeatures(featuresBuilder.build());
        return caseBuilder.build();
    }
    
    private static ActionType createActionBitmap(long input) {
        final Boolean OFPAT_OUTPUT = ((input) & (1<<0)) != 0;
        final Boolean OFPAT_COPY_TTL_OUT = ((input) & (1<<11)) != 0;
        final Boolean OFPAT_COPY_TTL_IN = ((input) & (1<<12)) != 0;
        final Boolean OFPAT_SET_MPLS_TTL = ((input) & (1<<15)) != 0;
        final Boolean OFPAT_DEC_MPLS_TTL = ((input) & (1<<16)) != 0;
        final Boolean OFPAT_PUSH_VLAN = ((input) & (1<<17)) != 0;
        final Boolean OFPAT_POP_VLAN = ((input) & (1<<18)) != 0;
        final Boolean OFPAT_PUSH_MPLS = ((input) & (1<<19)) != 0;
        final Boolean OFPAT_POP_MPLS = ((input) & (1<<20)) != 0;
        final Boolean OFPAT_SET_QUEUE = ((input) & (1<<21)) != 0;
        final Boolean OFPAT_GROUP = ((input) & (1<<22)) != 0;
        final Boolean OFPAT_SET_NW_TTL = ((input) & (1<<23)) != 0;
        final Boolean OFPAT_DEC_NW_TTL = ((input) & (1<<24)) != 0;
        final Boolean OFPAT_SET_FIELD = ((input) & (1<<25)) != 0;
        final Boolean OFPAT_PUSH_PBB = ((input) & (1<<26)) != 0;
        final Boolean OFPAT_POP_PBB = ((input) & (1<<27)) != 0;
        final Boolean OFPAT_EXPERIMENTER = false;
        return new ActionType(OFPAT_COPY_TTL_IN, OFPAT_COPY_TTL_OUT, OFPAT_DEC_MPLS_TTL,
                OFPAT_DEC_NW_TTL, OFPAT_EXPERIMENTER, OFPAT_GROUP, OFPAT_OUTPUT, OFPAT_POP_MPLS,
                OFPAT_POP_PBB, OFPAT_POP_VLAN, OFPAT_PUSH_MPLS, OFPAT_PUSH_PBB, OFPAT_PUSH_VLAN,
                OFPAT_SET_FIELD, OFPAT_SET_MPLS_TTL, OFPAT_SET_NW_TTL, OFPAT_SET_QUEUE);
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
    
    private MultipartReplyGroupDescCase setGroupDesc(ByteBuf input) {
        MultipartReplyGroupDescCaseBuilder caseBuilder = new MultipartReplyGroupDescCaseBuilder();
        MultipartReplyGroupDescBuilder builder = new MultipartReplyGroupDescBuilder();
        List<GroupDesc> groupDescsList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            GroupDescBuilder groupDescBuilder = new GroupDescBuilder();
            int bodyLength = input.readUnsignedShort();
            groupDescBuilder.setType(GroupType.forValue(input.readUnsignedByte()));
            input.skipBytes(PADDING_IN_GROUP_DESC_HEADER);
            groupDescBuilder.setGroupId(new GroupId(input.readUnsignedInt()));
            int actualLength = GROUP_DESC_HEADER_LENGTH;
            List<BucketsList> bucketsList = new ArrayList<>();
            while (actualLength < bodyLength) {
                BucketsListBuilder bucketsBuilder = new BucketsListBuilder();
                int bucketsLength = input.readUnsignedShort();
                bucketsBuilder.setWeight(input.readUnsignedShort());
                bucketsBuilder.setWatchPort(new PortNumber(input.readUnsignedInt()));
                bucketsBuilder.setWatchGroup(input.readUnsignedInt());
                input.skipBytes(PADDING_IN_BUCKETS_HEADER);
                CodeKeyMaker keyMaker = CodeKeyMakerFactory.createActionsKeyMaker(EncodeConstants.OF13_VERSION_ID);
                List<Action> actions = ListDeserializer.deserializeList(EncodeConstants.OF13_VERSION_ID,
                        bucketsLength - BUCKETS_HEADER_LENGTH, input, keyMaker, registry);
                bucketsBuilder.setAction(actions);
                bucketsList.add(bucketsBuilder.build());
                actualLength += bucketsLength;
            }
            groupDescBuilder.setBucketsList(bucketsList);
            groupDescsList.add(groupDescBuilder.build());
        }
        builder.setGroupDesc(groupDescsList);
        caseBuilder.setMultipartReplyGroupDesc(builder.build());
        return caseBuilder.build();
    }

    @Override
    public void injectDeserializerRegistry(
            DeserializerRegistry deserializerRegistry) {
        registry = deserializerRegistry;
    }
}
