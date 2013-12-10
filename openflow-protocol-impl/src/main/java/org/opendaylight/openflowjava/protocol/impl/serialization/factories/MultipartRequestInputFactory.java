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

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ActionsSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortDesc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeatureProperties;

/**
 * Translates MultipartRequest messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestInputFactory implements OFSerializer<MultipartRequestInput> {
    private static final byte MESSAGE_TYPE = 18;
    private static final int MESSAGE_LENGTH = 16;
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
    private static final byte TABLE_FEAT_HEADER_LENGTH = 4;
    private static MultipartRequestInputFactory instance;
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
    private static final int EXPERIMENTER_CODE = 65534; // 0xFFFE
    private static final int EXPERIMENTER_MISS_CODE = 65535; // 0xFFFF
    private static final byte FLOW_BODY_LENGTH = 32;
    private static final byte AGGREGATE_BODY_LENGTH = 32;
    private static final byte PORT_STATS_BODY_LENGTH = 8;
    private static final byte QUEUE_BODY_LENGTH = 8;
    private static final byte GROUP_BODY_LENGTH = 8;
    private static final byte METER_BODY_LENGTH = 8;
    private static final byte METER_CONFIG_BODY_LENGTH = 8;
    private static final byte EXPERIMENTER_BODY_LENGTH = 8;
    private static final byte TABLE_FEATURES_LENGTH = 64;
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
    


    private MultipartRequestInputFactory() {
        // singleton
    }

    /**
     * @return singleton factory
     */
    public static synchronized MultipartRequestInputFactory getInstance() {
        if (instance == null) {
            instance = new MultipartRequestInputFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out,
            MultipartRequestInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getType().getIntValue());
        out.writeShort(createMultipartRequestFlagsBitmask(message.getFlags()));
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_MESSAGE, out);

        if (message.getMultipartRequestBody() instanceof MultipartRequestDesc ){
            encodeDescBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestFlow) {
            encodeFlowBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestAggregate) {
            encodeAggregateBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTable) {
            encodeTableBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortStats) {
            encodePortStatsBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestQueue) {
            encodeQueueBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroup) {
            encodeGroupStatsBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroupDesc) {
            encodeGroupDescBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestGroupFeatures) {
            encodeGroupFeaturesBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeter) {
            encodeMeterBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterConfig) {
            encodeMeterConfigBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestMeterFeatures) {
            encodeMeterFeaturesBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTableFeatures) {
            encodeTableFeaturesBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortDesc) {
            encodePortDescBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestExperimenter) {
            encodeExperimenterBody(message.getMultipartRequestBody(), out);
        }
    }

    @Override
    public int computeLength(MultipartRequestInput message) {
        return MESSAGE_LENGTH + computeBodyLength(message);
    }
    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

    /**
     *
     * @param message
     * @return length of MultipartRequestMessage
     */
    public int computeBodyLength(MultipartRequestInput message) {
        int length = 0;
        MultipartType type = message.getType();
        if (type.equals(MultipartType.OFPMPFLOW)) {
            MultipartRequestFlow body = (MultipartRequestFlow) message.getMultipartRequestBody();
            length += FLOW_BODY_LENGTH + MatchSerializer.computeMatchLength(body.getMatch());
        } else if (type.equals(MultipartType.OFPMPAGGREGATE)) {
            MultipartRequestAggregate body = (MultipartRequestAggregate) message.getMultipartRequestBody();
            length += AGGREGATE_BODY_LENGTH + MatchSerializer.computeMatchLength(body.getMatch());
        } else if (type.equals(MultipartType.OFPMPPORTSTATS)) {
            length += PORT_STATS_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPQUEUE)) {
            length += QUEUE_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPGROUP)) {
            length += GROUP_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPMETER)) {
            length += METER_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPMETERCONFIG)) {
            length += METER_CONFIG_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPTABLEFEATURES)) {
            MultipartRequestTableFeatures body = (MultipartRequestTableFeatures) message.getMultipartRequestBody();
            length += computeTableFeaturesLength(body);
        } else if (type.equals(MultipartType.OFPMPEXPERIMENTER)) {
            MultipartRequestExperimenter body = (MultipartRequestExperimenter) message.getMultipartRequestBody();
            length += EXPERIMENTER_BODY_LENGTH;
            if (body.getData() != null) {
                length += body.getData().length;
            }
        }
        return length;
    }

    private static int computeTableFeaturesLength(MultipartRequestTableFeatures body) {
        int length = 0;
        if (body != null && body.getTableFeatures() != null) {
            List<TableFeatures> tableFeatures = body.getTableFeatures();
            for (TableFeatures feature : tableFeatures) {
                length += TABLE_FEATURES_LENGTH;
                List<TableFeatureProperties> featureProperties = feature.getTableFeatureProperties();
                if (featureProperties != null) {
                    for (TableFeatureProperties featProp : featureProperties) {
                        length += TABLE_FEAT_HEADER_LENGTH;
                        if (featProp.getAugmentation(InstructionRelatedTableFeatureProperty.class) != null) {
                            InstructionRelatedTableFeatureProperty property =
                                    featProp.getAugmentation(InstructionRelatedTableFeatureProperty.class);
                            length += property.getInstructions().size() * STRUCTURE_HEADER_LENGTH;
                        } else if (featProp.getAugmentation(NextTableRelatedTableFeatureProperty.class) != null) {
                            NextTableRelatedTableFeatureProperty property =
                                    featProp.getAugmentation(NextTableRelatedTableFeatureProperty.class);
                            length += property.getNextTableIds().size();
                        } else if (featProp.getAugmentation(ActionRelatedTableFeatureProperty.class) != null) {
                            ActionRelatedTableFeatureProperty property =
                                    featProp.getAugmentation(ActionRelatedTableFeatureProperty.class);
                            length += property.getActionsList().size() * STRUCTURE_HEADER_LENGTH;
                        } else if (featProp.getAugmentation(OxmRelatedTableFeatureProperty.class) != null) {
                            OxmRelatedTableFeatureProperty property =
                                    featProp.getAugmentation(OxmRelatedTableFeatureProperty.class);
                            length += property.getMatchEntries().size() * STRUCTURE_HEADER_LENGTH;
                        } else if (featProp.getAugmentation(ExperimenterRelatedTableFeatureProperty.class) != null) {
                            ExperimenterRelatedTableFeatureProperty property =
                                    featProp.getAugmentation(ExperimenterRelatedTableFeatureProperty.class);
                            length += 2 * (EncodeConstants.SIZE_OF_INT_IN_BYTES);
                            if (property.getData() != null) {
                                length += property.getData().length;
                            }
                        }
                    }
                }
            }
        }
        return length;
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
    private void encodeDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf output) {
        // The body of MultiPartRequestDesc is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void encodeTableBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartTable is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void encodeGroupDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartRequestGroupDesc is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void encodeGroupFeaturesBody(
            MultipartRequestBody multipartRequestBody, ByteBuf out) {
     // The body of MultiPartRequestGroupFeatures is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void encodeMeterFeaturesBody(
            MultipartRequestBody multipartRequestBody, ByteBuf out) {
     // The body of MultiPartMeterFeatures is empty
    }

    /**
     * @param multipartRequestBody
     * @param out
     */
    private void encodePortDescBody(MultipartRequestBody multipartRequestBody,
            ByteBuf out) {
     // The body of MultiPartPortDesc is empty
    }

    private static void encodeFlowBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
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
        MultipartRequestPortStats portstats = (MultipartRequestPortStats) multipartRequestBody;
        output.writeInt(portstats.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY, output);
    }

    private static void encodeQueueBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestQueue queue = (MultipartRequestQueue) multipartRequestBody;
        output.writeInt(queue.getPortNo().intValue());
        output.writeInt(queue.getQueueId().intValue());
    }

    private static void encodeGroupStatsBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestGroup groupStats = (MultipartRequestGroup) multipartRequestBody;
        output.writeInt(groupStats.getGroupId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_GROUP_BODY, output);
    }

    private static void encodeMeterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestMeter meter = (MultipartRequestMeter) multipartRequestBody;
        output.writeInt(meter.getMeterId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_BODY, output);
    }

    private static void encodeMeterConfigBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestMeterConfig meterConfig = (MultipartRequestMeterConfig) multipartRequestBody;
        output.writeInt(meterConfig.getMeterId().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY, output);
    }

    private static void encodeExperimenterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestExperimenter experimenter = (MultipartRequestExperimenter) multipartRequestBody;
        output.writeInt(experimenter.getExperimenter().intValue());
        output.writeInt(experimenter.getExpType().intValue());
        byte[] data = experimenter.getData();
        if (data != null) {
            output.writeBytes(data);
        }
    }

    private static void encodeTableFeaturesBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        if (multipartRequestBody != null) {
            MultipartRequestTableFeatures tableFeatures = (MultipartRequestTableFeatures) multipartRequestBody;
            if(tableFeatures.getTableFeatures() != null) {
                for (TableFeatures currTableFeature : tableFeatures.getTableFeatures()) {
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
    }

    private static void writeTableFeatureProperties(ByteBuf output, List<TableFeatureProperties> props) {
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
                    writeExperimenterRelatedTableProperty(output, property, EXPERIMENTER_CODE);
                } else if (type.equals(TableFeaturesPropType.OFPTFPTEXPERIMENTERMISS)) {
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
        int length = TABLE_FEAT_HEADER_LENGTH + 2 * (EncodeConstants.SIZE_OF_INT_IN_BYTES);
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
