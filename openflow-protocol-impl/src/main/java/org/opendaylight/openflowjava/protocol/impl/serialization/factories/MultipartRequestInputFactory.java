/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.EnhancedMessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ActionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.grouping.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.grouping.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfigCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeaturesCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.aggregate._case.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.flow._case.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.group._case.MultipartRequestGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.meter._case.MultipartRequestMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.meter.config._case.MultipartRequestMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.port.stats._case.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.queue._case.MultipartRequestQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.MultipartRequestTableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;

/**
 * Translates MultipartRequest messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestInputFactory implements OFSerializer<MultipartRequestInput>, SerializerRegistryInjector {
    private static final byte MESSAGE_TYPE = 18;
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
    private static final byte TABLE_FEAT_HEADER_LENGTH = 4;
    private static final byte INSTRUCTIONS_CODE = 0;
    private static final byte INSTRUCTIONS_MISS_CODE = 1;
    private static final byte NEXT_TABLE_CODE = 2;
    private static final byte NEXT_TABLE_MISS_CODE = 3;
    private static final byte WRITE_ACTIONS_CODE = 4;
    private static final byte WRITE_ACTIONS_MISS_CODE = 5;
    private static final byte APPLY_ACTIONS_CODE = 6;
    private static final byte APPLY_ACTIONS_MISS_CODE = 7;
    private static final byte MATCH_CODE = 8;
    private static final byte WILDCARDS_CODE = 10;
    private static final byte WRITE_SETFIELD_CODE = 12;
    private static final byte WRITE_SETFIELD_MISS_CODE = 13;
    private static final byte APPLY_SETFIELD_CODE = 14;
    private static final byte APPLY_SETFIELD_MISS_CODE = 15;
    private static final byte STRUCTURE_HEADER_LENGTH = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01 = 3;
    private static final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02 = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_01 = 3;
    private static final byte PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_02 = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_GROUP_BODY = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_METER_BODY = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY = 5;
    private SerializerRegistry registry;

    @Override
    public void serialize(MultipartRequestInput message, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, message, outBuffer, EncodeConstants.EMPTY_LENGTH);
        outBuffer.writeShort(message.getType().getIntValue());
        outBuffer.writeShort(createMultipartRequestFlagsBitmask(message.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_MESSAGE, outBuffer);

        if (message.getMultipartRequestBody() instanceof MultipartRequestDescCase){
            serializeDescBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestFlowCase) {
            serializeFlowBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestAggregateCase) {
            serializeAggregateBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTableCase) {
            serializeTableBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortStatsCase) {
            serializePortStatsBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestQueueCase) {
            serializeQueueBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroupCase) {
            serializeeGroupStatsBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroupDescCase) {
            serializeGroupDescBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroupFeaturesCase) {
            serializeGroupFeaturesBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterCase) {
            serializeMeterBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterConfigCase) {
            serializeMeterConfigBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterFeaturesCase) {
            serializeMeterFeaturesBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTableFeaturesCase) {
            serializeTableFeaturesBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortDescCase) {
            serializePortDescBody(message.getMultipartRequestBody(), outBuffer);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestExperimenterCase) {
        	serializeExperimenterBody(message, outBuffer);
        }
        ByteBufUtils.updateOFHeaderLength(outBuffer);
    }

	private void serializeExperimenterBody(MultipartRequestInput message,
			ByteBuf outBuffer) {
		MultipartRequestExperimenterCase expCase =
				(MultipartRequestExperimenterCase) message.getMultipartRequestBody();
		MultipartRequestExperimenter experimenter = expCase.getMultipartRequestExperimenter();
		OFSerializer<MultipartRequestExperimenter> serializer = registry.getSerializer(
				new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, MultipartRequestExperimenter.class));
		serializer.serialize(experimenter, outBuffer);
	}

    private static int createMultipartRequestFlagsBitmask(MultipartRequestFlags flags) {
        int multipartRequestFlagsBitmask = 0;
        Map<Integer, Boolean> multipartRequestFlagsMap = new HashMap<>();
        multipartRequestFlagsMap.put(0, flags.isOFPMPFREQMORE());

        multipartRequestFlagsBitmask = ByteBufUtils.fillBitMaskFromMap(multipartRequestFlagsMap);
        return multipartRequestFlagsBitmask;
    }

    /**
     * @param multipartRequestBody
     * @param output
     */
    private void serializeDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf output) {
        // The body of MultiPartRequestDesc is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void serializeTableBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartTable is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void serializeGroupDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartRequestGroupDesc is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void serializeGroupFeaturesBody(
            MultipartRequestBody multipartRequestBody, ByteBuf out) {
     // The body of MultiPartRequestGroupFeatures is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void serializeMeterFeaturesBody(
            MultipartRequestBody multipartRequestBody, ByteBuf out) {
     // The body of MultiPartMeterFeatures is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void serializePortDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartPortDesc is empty
    }

    private void serializeFlowBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestFlowCase flowCase = (MultipartRequestFlowCase) multipartRequestBody;
        MultipartRequestFlow flow = flowCase.getMultipartRequestFlow();
        output.writeByte(flow.getTableId().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01, output);
        output.writeInt(flow.getOutPort().intValue());
        output.writeInt(flow.getOutGroup().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02, output);
        output.writeLong(flow.getCookie().longValue());
        output.writeLong(flow.getCookieMask().longValue());
        OFSerializer<Match> serializer = registry.getSerializer(new MessageTypeKey<>(
                EncodeConstants.OF13_VERSION_ID, Match.class));
        serializer.serialize(flow.getMatch(), output);
    }

    private void serializeAggregateBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestAggregateCase aggregateCase = (MultipartRequestAggregateCase) multipartRequestBody;
        MultipartRequestAggregate aggregate = aggregateCase.getMultipartRequestAggregate();
        output.writeByte(aggregate.getTableId().byteValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_01, output);
        output.writeInt(aggregate.getOutPort().intValue());
        output.writeInt(aggregate.getOutGroup().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_AGREGGATE_BODY_02, output);
        output.writeLong(aggregate.getCookie().longValue());
        output.writeLong(aggregate.getCookieMask().longValue());
        OFSerializer<Match> serializer = registry.getSerializer(new MessageTypeKey<>(
                EncodeConstants.OF13_VERSION_ID, Match.class));
        serializer.serialize(aggregate.getMatch(), output);
    }

    private static void serializePortStatsBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestPortStatsCase portstatsCase = (MultipartRequestPortStatsCase) multipartRequestBody;
        MultipartRequestPortStats portstats = portstatsCase.getMultipartRequestPortStats();
        output.writeInt(portstats.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY, output);
    }

    private static void serializeQueueBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestQueueCase queueCase = (MultipartRequestQueueCase) multipartRequestBody;
        MultipartRequestQueue queue = queueCase.getMultipartRequestQueue();
        output.writeInt(queue.getPortNo().intValue());
        output.writeInt(queue.getQueueId().intValue());
    }

    private static void serializeeGroupStatsBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestGroupCase groupStatsCase = (MultipartRequestGroupCase) multipartRequestBody;
        MultipartRequestGroup groupStats = groupStatsCase.getMultipartRequestGroup();
        output.writeInt(groupStats.getGroupId().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_GROUP_BODY, output);
    }

    private static void serializeMeterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestMeterCase meterCase = (MultipartRequestMeterCase) multipartRequestBody;
        MultipartRequestMeter meter = meterCase.getMultipartRequestMeter();
        output.writeInt(meter.getMeterId().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_BODY, output);
    }

    private static void serializeMeterConfigBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestMeterConfigCase meterConfigCase = (MultipartRequestMeterConfigCase) multipartRequestBody;
        MultipartRequestMeterConfig meterConfig = meterConfigCase.getMultipartRequestMeterConfig();
        output.writeInt(meterConfig.getMeterId().getValue().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY, output);
    }

    private void serializeTableFeaturesBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        if (multipartRequestBody != null) {
            MultipartRequestTableFeaturesCase tableFeaturesCase = (MultipartRequestTableFeaturesCase) multipartRequestBody;
            MultipartRequestTableFeatures tableFeatures = tableFeaturesCase.getMultipartRequestTableFeatures();
            if(tableFeatures.getTableFeatures() != null) {
                for (TableFeatures currTableFeature : tableFeatures.getTableFeatures()) {
                    int tableFeatureLengthIndex = output.writerIndex();
                    output.writeShort(EncodeConstants.EMPTY_LENGTH);
                    output.writeByte(currTableFeature.getTableId());
                    ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY, output);
                    output.writeBytes(currTableFeature.getName().getBytes());
                    ByteBufUtils.padBuffer((32 - currTableFeature.getName().getBytes().length), output);
                    output.writeLong(currTableFeature.getMetadataMatch().longValue());
                    output.writeLong(currTableFeature.getMetadataWrite().longValue());
                    output.writeInt(createTableConfigBitmask(currTableFeature.getConfig()));
                    output.writeInt(currTableFeature.getMaxEntries().intValue());
                    writeTableFeatureProperties(output, currTableFeature.getTableFeatureProperties());
                    output.setShort(tableFeatureLengthIndex, output.writerIndex() - tableFeatureLengthIndex);
                }
            }
        }
    }

    private void writeTableFeatureProperties(ByteBuf output, List<TableFeatureProperties> props) {
        if (props != null) {
            for (TableFeatureProperties property : props) {
                TableFeaturesPropType type = property.getType();
                if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONS)) {
                    writeInstructionRelatedTableProperty(output, property, INSTRUCTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS)) {
                    writeInstructionRelatedTableProperty(output, property, INSTRUCTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLES)) {
                    writeNextTableRelatedTableProperty(output, property, NEXT_TABLE_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS)) {
                    writeNextTableRelatedTableProperty(output, property, NEXT_TABLE_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONS)) {
                    writeActionsRelatedTableProperty(output, property, WRITE_ACTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITEACTIONSMISS)) {
                    writeActionsRelatedTableProperty(output, property, WRITE_ACTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONS)) {
                    writeActionsRelatedTableProperty(output, property, APPLY_ACTIONS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYACTIONSMISS)) {
                    writeActionsRelatedTableProperty(output, property, APPLY_ACTIONS_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTMATCH)) {
                    writeOxmRelatedTableProperty(output, property, MATCH_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWILDCARDS)) {
                    writeOxmRelatedTableProperty(output, property, WILDCARDS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELD)) {
                    writeOxmRelatedTableProperty(output, property, WRITE_SETFIELD_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTWRITESETFIELDMISS)) {
                    writeOxmRelatedTableProperty(output, property, WRITE_SETFIELD_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD)) {
                    writeOxmRelatedTableProperty(output, property, APPLY_SETFIELD_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTAPPLYSETFIELDMISS)) {
                    writeOxmRelatedTableProperty(output, property, APPLY_SETFIELD_MISS_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTER)) {
                    writeExperimenterRelatedTableProperty(output, property);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
                    writeExperimenterRelatedTableProperty(output, property);
                }
            }
        }
    }

    private void writeInstructionRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<Instruction> instructions = property.
                getAugmentation(InstructionRelatedTableFeatureProperty.class).getInstruction();
        int length = TABLE_FEAT_HEADER_LENGTH;
        int padding = 0;
        if (instructions != null) {
            for (Instruction instruction : instructions) {
                if (instruction.getType().isAssignableFrom(Experimenter.class)) {
                    length += EncodeConstants.EXPERIMENTER_IDS_LENGTH;
                } else {
                    length += STRUCTURE_HEADER_LENGTH;
                }
            }
            padding = paddingNeeded(length);
            output.writeShort(length);
            for (Instruction instruction : instructions) {
                HeaderSerializer<Instruction> entrySerializer = registry.getSerializer(
                        new EnhancedMessageTypeKey<>(EncodeConstants.OF13_VERSION_ID,
                                Instruction.class, instruction.getType()));
                entrySerializer.serializeHeader(instruction, output);
            }
        } else {
            padding = paddingNeeded(length);
            output.writeShort(length);
        }
        ByteBufUtils.padBuffer(padding, output);
    }

    private static void writeNextTableRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<NextTableIds> nextTableIds = property.
                getAugmentation(NextTableRelatedTableFeatureProperty.class).getNextTableIds();
        int length = TABLE_FEAT_HEADER_LENGTH;
        int padding = 0;
        if (nextTableIds != null) {
            length += nextTableIds.size();
            padding = paddingNeeded(length);
            output.writeShort(length);
            for (NextTableIds next : nextTableIds) {
                output.writeByte(next.getTableId());
            }
        } else {
            padding = paddingNeeded(length); 
            output.writeShort(length + padding);
        }
        ByteBufUtils.padBuffer(padding, output);
    }

    private static int paddingNeeded(int length) {
        int paddingRemainder = length % EncodeConstants.PADDING;
        int result = 0;
        if (paddingRemainder != 0) {
            result = EncodeConstants.PADDING - paddingRemainder;
        }
        return result;
    }

    private void writeActionsRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<Action> actions = property.
                getAugmentation(ActionRelatedTableFeatureProperty.class).getAction();
        int length = TABLE_FEAT_HEADER_LENGTH;
        int padding = 0;
        if (actions != null) {
            for (Action action : actions) {
                if (action.getType().isAssignableFrom(Experimenter.class)) {
                    length += EncodeConstants.EXPERIMENTER_IDS_LENGTH;
                } else {
                    length += STRUCTURE_HEADER_LENGTH;
                }
            }
            length += actions.size() * STRUCTURE_HEADER_LENGTH;
            padding += paddingNeeded(length);
            output.writeShort(length);
            for (Action action : actions) {
                HeaderSerializer<Action> entrySerializer = registry.getSerializer(
                        new EnhancedMessageTypeKey<>(EncodeConstants.OF13_VERSION_ID,
                                Action.class, action.getType()));
                entrySerializer.serializeHeader(action, output);
            }
        } else {
            padding = paddingNeeded(length);
            output.writeShort(length);
        }
        ByteBufUtils.padBuffer(padding, output);
    }

    private void writeOxmRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property, byte code) {
        output.writeShort(code);
        List<MatchEntries> entries = property.
                getAugmentation(OxmRelatedTableFeatureProperty.class).getMatchEntries();
        int length = TABLE_FEAT_HEADER_LENGTH;
        int padding = 0;
        if (entries != null) {
            // experimenter length / definition ?
            length += entries.size() * STRUCTURE_HEADER_LENGTH;
            padding = paddingNeeded(length);
            output.writeShort(length);
            
            for (MatchEntries entry : entries) {
                HeaderSerializer<MatchEntries> entrySerializer = registry.getSerializer(
                        new EnhancedMessageTypeKey<>(EncodeConstants.OF13_VERSION_ID,
                                entry.getOxmClass(), entry.getOxmMatchField()));
                entrySerializer.serializeHeader(entry, output);
            }
        } else {
            padding = paddingNeeded(length);
            output.writeShort(length);
        }
        ByteBufUtils.padBuffer(padding, output);
    }

    private void writeExperimenterRelatedTableProperty(ByteBuf output,
            TableFeatureProperties property) {
    	OFSerializer<TableFeatureProperties> serializer = registry.getSerializer(
    			new MessageTypeKey<>(EncodeConstants.OF13_VERSION_ID, TableFeatureProperties.class));
    	serializer.serialize(property, output);
    }

    private static int createTableConfigBitmask(TableConfig tableConfig) {
        int tableConfigBitmask = 0;
        Map<Integer, Boolean> tableConfigMap = new HashMap<>();
        tableConfigMap.put(3, tableConfig.isOFPTCDEPRECATEDMASK());

        tableConfigBitmask = ByteBufUtils.fillBitMaskFromMap(tableConfigMap);
        return tableConfigBitmask;
    }

    @Override
    public void injectSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.registry = serializerRegistry;
    }
}
