/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.InstructionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeatureProperties;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestMessageFactory implements OFSerializer<MultipartRequestMessage> {
    private static final byte MESSAGE_TYPE = 18;
    private static final int MESSAGE_LENGTH = 16;
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
    private static final byte TABLE_FEAT_HEADER_LENGTH = 4;
    private static MultipartRequestMessageFactory instance; 
    
    private MultipartRequestMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized MultipartRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new MultipartRequestMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            MultipartRequestMessage message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getType().getIntValue());
        out.writeShort(createMultipartRequestFlagsBitmask(message.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_MESSAGE, out);
        
        if (message.getMultipartRequestBody() instanceof MultipartRequestFlow) {
            encodeFlowBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestAggregate) {
            encodeAggregateBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortStats) {
            encodePortStatsBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestQueue) {
            encodeQueBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroup) {
            encodeGroupStatsBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeter) {
            encodeMeterBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterConfig) {
            encodeMeterConfigBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTableFeatures) {
            encodeTableFeaturesBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestExperimenter) {
            encodeExperimenterBody(message.getMultipartRequestBody(), out);
        }
    }
    
    @Override
    public int computeLength(MultipartRequestMessage message) {
        // TODO
        return MESSAGE_LENGTH;
    }
    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    } 
    
    private static int createMultipartRequestFlagsBitmask(MultipartRequestFlags flags) {
        int multipartRequestFlagsBitmask = 0;
        Map<Integer, Boolean> multipartRequestFlagsMap = new HashMap<>();
        multipartRequestFlagsMap.put(0, flags.isOFPMPFREQMORE());
        
        multipartRequestFlagsBitmask = ByteBufUtils.fillBitMaskFromMap(multipartRequestFlagsMap);
        return multipartRequestFlagsBitmask;
    }
    
    private static void encodeFlowBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02 = 4;
        MultipartRequestFlow flow = (MultipartRequestFlow) multipartRequestBody;
        output.writeByte(flow.getTableId().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01, output);
        output.writeInt(flow.getOutPort().intValue());
        output.writeInt(flow.getOutGroup().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02, output);
        output.writeLong(flow.getCookie().longValue()); 
        output.writeLong(flow.getCookieMask().longValue());
        MatchSerializer.encodeMatch(flow.getMatch(), output);
    }
    
    private static void encodeAggregateBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_02 = 4;
        MultipartRequestAggregate aggregate = (MultipartRequestAggregate) multipartRequestBody;
        output.writeByte(aggregate.getTableId().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_01, output);
        output.writeInt(aggregate.getOutPort().intValue());
        output.writeInt(aggregate.getOutGroup().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_02, output);
        output.writeLong(aggregate.getCookie().longValue()); 
        output.writeLong(aggregate.getCookieMask().longValue());
        MatchSerializer.encodeMatch(aggregate.getMatch(), output);
    }
    
    private static void encodePortStatsBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY = 4;
        MultipartRequestPortStats portstats = (MultipartRequestPortStats) multipartRequestBody;
        output.writeInt(portstats.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY, output);
    }
    
    private static void encodeQueBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestQueue queue = (MultipartRequestQueue) multipartRequestBody;
        output.writeInt(queue.getPortNo().intValue());
        output.writeInt(queue.getQueueId().intValue());
    }
    
    private static void encodeGroupStatsBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_GROUP_BODY = 4;
        MultipartRequestGroup groupStats = (MultipartRequestGroup) multipartRequestBody;
        output.writeInt(groupStats.getGroupId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_GROUP_BODY, output);
    }
    
    private static void encodeMeterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_BODY = 4;
        MultipartRequestMeter meter = (MultipartRequestMeter) multipartRequestBody;
        output.writeInt(meter.getMeterId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_BODY, output);
    }
    
    private static void encodeMeterConfigBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY = 4;
        MultipartRequestMeterConfig meterConfig = (MultipartRequestMeterConfig) multipartRequestBody;
        output.writeInt(meterConfig.getMeterId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY, output);
    }
    
    private static void encodeExperimenterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestExperimenter experimenter = (MultipartRequestExperimenter) multipartRequestBody;
        output.writeInt(experimenter.getExperimenter().intValue());
        output.writeInt(experimenter.getExpType().intValue());
    }
    
    private static void encodeTableFeaturesBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        if (multipartRequestBody != null) {
            MultipartRequestTableFeatures tableFeatures = (MultipartRequestTableFeatures) multipartRequestBody;
            for (TableFeatures currTableFeature : tableFeatures.getTableFeatures()) {
                final byte PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY = 5;
                output.writeByte(currTableFeature.getTableId());
                ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY, output);
                output.writeBytes(currTableFeature.getName().getBytes());
                ByteBufUtils.padBuffer((32 - currTableFeature.getName().getBytes().length), output);
                output.writeLong(currTableFeature.getMetadataMatch().longValue());
                output.writeLong(currTableFeature.getMetadataWrite().longValue());
                output.writeInt(createTableConfigBitmask(currTableFeature.getConfig()));
                output.writeInt(currTableFeature.getMaxEntries().intValue());
                writeTableFeatureProperties(output, currTableFeature.getTableFeatureProperties());
            }
        }
    }
    
    private static void writeTableFeatureProperties(ByteBuf output, List<TableFeatureProperties> props) {
        if (props != null) {
            for (TableFeatureProperties property : props) {
                TableFeaturesPropType type = property.getType();
                if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONS)) {
                    final byte INSTRUCTIONS_CODE = 0;
                    writeInstructionRelatedTableProperty(output, property, INSTRUCTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS)) {
                    final byte INSTRUCTIONS_MISS_CODE = 1;
                    writeInstructionRelatedTableProperty(output, property, INSTRUCTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLES)) {
                    final byte NEXT_TABLE_CODE = 2;
                    writeNextTableRelatedTableProperty(output, property, NEXT_TABLE_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS)) {
                    final byte NEXT_TABLE_MISS_CODE = 3;
                    writeNextTableRelatedTableProperty(output, property, NEXT_TABLE_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONS)) {
                    final byte WRITE_ACTIONS_CODE = 4;
                    writeActionsRelatedTableProperty(output, property, WRITE_ACTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONSMISS)) {
                    final byte WRITE_ACTIONS_MISS_CODE = 5;
                    writeActionsRelatedTableProperty(output, property, WRITE_ACTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONS)) {
                    final byte APPLY_ACTIONS_CODE = 6;
                    writeActionsRelatedTableProperty(output, property, APPLY_ACTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONSMISS)) {
                    final byte APPLY_ACTIONS_MISS_CODE = 7;
                    writeActionsRelatedTableProperty(output, property, APPLY_ACTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTMATCH)) {
                    final byte MATCH_CODE = 8;
                    writeOxmRelatedTableProperty(output, property, MATCH_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWILDCARDS)) {
                    final byte WILDCARDS_CODE = 10;
                    writeOxmRelatedTableProperty(output, property, WILDCARDS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELD)) {
                    final byte WRITE_SETFIELD_CODE = 12;
                    writeOxmRelatedTableProperty(output, property, WRITE_SETFIELD_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELDMISS)) {
                    final byte WRITE_SETFIELD_MISS_CODE = 13;
                    writeOxmRelatedTableProperty(output, property, WRITE_SETFIELD_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD)) {
                    final byte APPLY_SETFIELD_CODE = 14;
                    writeOxmRelatedTableProperty(output, property, APPLY_SETFIELD_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELDMISS)) {
                    final byte APPLY_SETFIELD_MISS_CODE = 15;
                    writeOxmRelatedTableProperty(output, property, APPLY_SETFIELD_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTER)) {
                    final int EXPERIMENTER_CODE = 65534; // 0xFFFE
                    writeExperimenterRelatedTableProperty(output, property, EXPERIMENTER_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
                    final int EXPERIMENTER_MISS_CODE = 65535; // 0xFFFF
                    writeExperimenterRelatedTableProperty(output, property, EXPERIMENTER_MISS_CODE);
                } 
            }
        }
    }

    private static void writeInstructionRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<Instructions> instructions = property.
                getAugmentation(InstructionRelatedTableFeatureProperty.class).getInstructions();
        int length = TABLE_FEAT_HEADER_LENGTH;
        if (instructions != null) {
        output.writeShort(InstructionsSerializer.computeInstructionsLength(instructions)
                + TABLE_FEAT_HEADER_LENGTH);
        InstructionsSerializer.encodeInstructions(instructions, output);
        } else {
            output.writeShort(length);
        }
    }
    
    private static void writeNextTableRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<NextTableIds> nextTableIds = property.
                getAugmentation(NextTableRelatedTableFeatureProperty.class).getNextTableIds();
        int length = TABLE_FEAT_HEADER_LENGTH;
        if (nextTableIds != null) {
            output.writeShort(length + nextTableIds.size());
            for (NextTableIds next : nextTableIds) {
                output.writeByte(next.getTableId());
            }
        } else {
            output.writeShort(length);
        }
    }
    
    private static void writeActionsRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<ActionsList> actions = property.
                getAugmentation(ActionRelatedTableFeatureProperty.class).getActionsList();
        int length = TABLE_FEAT_HEADER_LENGTH;
        if (actions != null) {
        output.writeShort(ActionsSerializer.computeLengthOfActions(actions)
                + TABLE_FEAT_HEADER_LENGTH);
        ActionsSerializer.encodeActions(actions, output);
        } else {
            output.writeShort(length);
        }
    }
    
    private static void writeOxmRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<MatchEntries> entries = property.
                getAugmentation(OxmRelatedTableFeatureProperty.class).getMatchEntries();
        int length = TABLE_FEAT_HEADER_LENGTH;
        if (entries != null) {
        output.writeShort(MatchSerializer.computeMatchEntriesLength(entries)
                + TABLE_FEAT_HEADER_LENGTH);
        MatchSerializer.encodeMatchEntries(entries, output);
        } else {
            output.writeShort(length);
        }
    }
    
    private static void writeExperimenterRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, int code) {
        output.writeShort(code);
        ExperimenterRelatedTableFeatureProperty exp = property.
                getAugmentation(ExperimenterRelatedTableFeatureProperty.class);
        byte[] data = exp.getData();
        int length = TABLE_FEAT_HEADER_LENGTH + 2 * (Integer.SIZE / Byte.SIZE);
        if (data != null) {
            output.writeShort(length + data.length);
            output.writeInt(exp.getExperimenter().intValue());
            output.writeInt(exp.getExpType().intValue());
            output.writeBytes(data);
        } else {
            output.writeShort(length);
            output.writeInt(exp.getExperimenter().intValue());
            output.writeInt(exp.getExpType().intValue());
        }
    }
    
    private static int createTableConfigBitmask(TableConfig tableConfig) {
        int tableConfigBitmask = 0;
        Map<Integer, Boolean> tableConfigMap = new HashMap<>();
        tableConfigMap.put(3, tableConfig.isOFPTCDEPRECATEDMASK());
        
        tableConfigBitmask = ByteBufUtils.fillBitMaskFromMap(tableConfigMap);
        return tableConfigBitmask;
    }
}
