/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.InstructionRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NextTableRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeatureProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmRelatedTableFeaturePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIds;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.table.features.properties.container.table.feature.properties.NextTableIdsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.ClearActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.GotoTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.WriteMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.instruction.rev130731.instructions.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesPropType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntriesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregateCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestDescCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlowCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfigCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfigCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStatsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueueCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeaturesCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.aggregate._case.MultipartRequestAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.desc._case.MultipartRequestDescBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.flow._case.MultipartRequestFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.group._case.MultipartRequestGroupBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.meter._case.MultipartRequestMeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.meter.config._case.MultipartRequestMeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.port.stats._case.MultipartRequestPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.queue._case.MultipartRequestQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.MultipartRequestTableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features._case.multipart.request.table.features.TableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeatureProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.TableFeaturePropertiesBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestInputFactoryTest {
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestInputFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(1));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestFlow());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong flow", message.getMultipartRequestBody(), decodeRequestFlow(out));
    }

    private static MultipartRequestFlowCase createRequestFlow() {
        MultipartRequestFlowCaseBuilder caseBuilder = new MultipartRequestFlowCaseBuilder();
        MultipartRequestFlowBuilder builder = new MultipartRequestFlowBuilder();
        builder.setTableId((short) 8);
        builder.setOutPort(85L);
        builder.setOutGroup(95L);
        byte[] cookie = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookieMask(new BigInteger(cookieMask));
        caseBuilder.setMultipartRequestFlow(builder.build());
        //TODO match field
        return caseBuilder.build();
    }

    private static MultipartRequestFlowCase decodeRequestFlow(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02 = 4;
        MultipartRequestFlowCaseBuilder caseBuilder = new MultipartRequestFlowCaseBuilder();
        MultipartRequestFlowBuilder builder = new MultipartRequestFlowBuilder();
        builder.setTableId(output.readUnsignedByte());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01);
        builder.setOutPort(output.readUnsignedInt());
        builder.setOutGroup(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02);
        byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(cookie);
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(cookieMask);
        builder.setCookieMask(new BigInteger(cookieMask));
        caseBuilder.setMultipartRequestFlow(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestInputAggregateBodyFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(2));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestAggregate());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong aggregate", message.getMultipartRequestBody(), decodeRequestAggregate(out));
    }

    private static MultipartRequestFlags decodeMultipartRequestFlags(short input){
        final Boolean _oFPMPFREQMORE = (input & (1 << 0)) > 0;
        return new MultipartRequestFlags(_oFPMPFREQMORE);
    }


    private static MultipartRequestAggregateCase createRequestAggregate() {
        MultipartRequestAggregateCaseBuilder caseBuilder = new MultipartRequestAggregateCaseBuilder();
        MultipartRequestAggregateBuilder builder = new MultipartRequestAggregateBuilder();
        builder.setTableId((short) 8);
        builder.setOutPort(85L);
        builder.setOutGroup(95L);
        byte[] cookie = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookieMask(new BigInteger(cookieMask));
        caseBuilder.setMultipartRequestAggregate(builder.build());
      //TODO match field
        return caseBuilder.build();
    }

    private static MultipartRequestAggregateCase decodeRequestAggregate(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_02 = 4;
        MultipartRequestAggregateCaseBuilder caseBuilder = new MultipartRequestAggregateCaseBuilder();
        MultipartRequestAggregateBuilder builder = new MultipartRequestAggregateBuilder();
        builder.setTableId(output.readUnsignedByte());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_01);
        builder.setOutPort(output.readUnsignedInt());
        builder.setOutGroup(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_02);
        byte[] cookie = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(cookie);
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(cookieMask);
        builder.setCookieMask(new BigInteger(cookieMask));
        caseBuilder.setMultipartRequestAggregate(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestInputTableFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(3));
        builder.setFlags(new MultipartRequestFlags(true));
        //multipart request for table does not have body
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestPortStatsMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(4));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestPortStats());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong portStatsBody", message.getMultipartRequestBody(), decodeRequestPortStats(out));
    }

    private static MultipartRequestPortStatsCase createRequestPortStats() {
        MultipartRequestPortStatsCaseBuilder caseBuilder = new MultipartRequestPortStatsCaseBuilder();
        MultipartRequestPortStatsBuilder builder = new MultipartRequestPortStatsBuilder();
        builder.setPortNo(2251L);
        caseBuilder.setMultipartRequestPortStats(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestPortStatsCase decodeRequestPortStats(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY = 4;
        MultipartRequestPortStatsCaseBuilder caseBuilder = new MultipartRequestPortStatsCaseBuilder();
        MultipartRequestPortStatsBuilder builder = new MultipartRequestPortStatsBuilder();
        builder.setPortNo(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY);
        caseBuilder.setMultipartRequestPortStats(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestQueueMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(5));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestQueue());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong queueBody", message.getMultipartRequestBody(), decodeRequestQueue(out));
    }

    private static MultipartRequestQueueCase createRequestQueue() {
        MultipartRequestQueueCaseBuilder caseBuilder = new MultipartRequestQueueCaseBuilder();
        MultipartRequestQueueBuilder builder = new MultipartRequestQueueBuilder();
        builder.setPortNo(2256L);
        builder.setQueueId(2211L);
        caseBuilder.setMultipartRequestQueue(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestQueueCase decodeRequestQueue(ByteBuf output) {
        MultipartRequestQueueCaseBuilder caseBuilder = new MultipartRequestQueueCaseBuilder();
        MultipartRequestQueueBuilder builder = new MultipartRequestQueueBuilder();
        builder.setPortNo(output.readUnsignedInt());
        builder.setQueueId(output.readUnsignedInt());
        caseBuilder.setMultipartRequestQueue(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestGroupMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(6));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestGroup());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong groupBody", message.getMultipartRequestBody(), decodeRequestGroup(out));
    }

    private static MultipartRequestGroupCase createRequestGroup() {
        MultipartRequestGroupCaseBuilder caseBuilder = new MultipartRequestGroupCaseBuilder();
        MultipartRequestGroupBuilder builder = new MultipartRequestGroupBuilder();
        builder.setGroupId(new GroupId(2258L));
        caseBuilder.setMultipartRequestGroup(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestGroupCase decodeRequestGroup(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_GROUP_BODY = 4;
        MultipartRequestGroupCaseBuilder caseBuilder = new MultipartRequestGroupCaseBuilder();
        MultipartRequestGroupBuilder builder = new MultipartRequestGroupBuilder();
        builder.setGroupId(new GroupId(output.readUnsignedInt()));
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_GROUP_BODY);
        caseBuilder.setMultipartRequestGroup(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMeterMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(9));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestMeter());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong meterBody", message.getMultipartRequestBody(), decodeRequestMeter(out));
    }

    private static MultipartRequestMeterCase createRequestMeter() {
        MultipartRequestMeterCaseBuilder caseBuilder = new MultipartRequestMeterCaseBuilder();
        MultipartRequestMeterBuilder builder = new MultipartRequestMeterBuilder();
        builder.setMeterId(new MeterId(1121L));
        caseBuilder.setMultipartRequestMeter(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestMeterCase decodeRequestMeter(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_BODY = 4;
        MultipartRequestMeterCaseBuilder caseBuilder = new MultipartRequestMeterCaseBuilder();
        MultipartRequestMeterBuilder builder = new MultipartRequestMeterBuilder();
        builder.setMeterId(new MeterId(output.readUnsignedInt()));
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_METER_BODY);
        caseBuilder.setMultipartRequestMeter(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMeterConfigMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(10));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestMeterConfig());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong meterConfigBody", message.getMultipartRequestBody(), decodeRequestMeterConfig(out));
    }

    private static MultipartRequestMeterConfigCase createRequestMeterConfig() {
        MultipartRequestMeterConfigCaseBuilder caseBuilder = new MultipartRequestMeterConfigCaseBuilder();
        MultipartRequestMeterConfigBuilder builder = new MultipartRequestMeterConfigBuilder();
        builder.setMeterId(new MeterId(1133L));
        caseBuilder.setMultipartRequestMeterConfig(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestMeterConfigCase decodeRequestMeterConfig(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY = 4;
        MultipartRequestMeterConfigCaseBuilder caseBuilder = new MultipartRequestMeterConfigCaseBuilder();
        MultipartRequestMeterConfigBuilder builder = new MultipartRequestMeterConfigBuilder();
        builder.setMeterId(new MeterId(output.readUnsignedInt()));
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY);
        caseBuilder.setMultipartRequestMeterConfig(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestExperimenterMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(0xffff));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestExperimenter());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong experimenterBody", message.getMultipartRequestBody(), decodeRequestExperimenter(out));
    }

    private static MultipartRequestExperimenterCase createRequestExperimenter() {
        MultipartRequestExperimenterCaseBuilder caseBuilder = new MultipartRequestExperimenterCaseBuilder();
        MultipartRequestExperimenterBuilder builder = new MultipartRequestExperimenterBuilder();
        builder.setExperimenter(1133L);
        builder.setExpType(1135L);
        caseBuilder.setMultipartRequestExperimenter(builder.build());
        return caseBuilder.build();
    }

    private static MultipartRequestExperimenterCase decodeRequestExperimenter(ByteBuf output) {
        MultipartRequestExperimenterCaseBuilder caseBuilder = new MultipartRequestExperimenterCaseBuilder();
        MultipartRequestExperimenterBuilder builder = new MultipartRequestExperimenterBuilder();
        builder.setExperimenter(output.readUnsignedInt());
        builder.setExpType(output.readUnsignedInt());
        caseBuilder.setMultipartRequestExperimenter(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestDescMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(0));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestDesc());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
    }

    private static MultipartRequestBody createRequestDesc() {
        MultipartRequestDescCaseBuilder caseBuilder = new MultipartRequestDescCaseBuilder();
        MultipartRequestDescBuilder builder = new MultipartRequestDescBuilder();
        caseBuilder.setMultipartRequestDesc(builder.build());
        return caseBuilder.build();
    }

    /**
     * @throws Exception
     * Testing of {@link MultipartRequestInputFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestTableFeaturesMessageFactory() throws Exception {
        MultipartRequestInputBuilder builder = new MultipartRequestInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setType(MultipartType.forValue(12));
        builder.setFlags(new MultipartRequestFlags(true));
        MultipartRequestTableFeaturesCaseBuilder caseBuilder = new MultipartRequestTableFeaturesCaseBuilder();
        MultipartRequestTableFeaturesBuilder featuresBuilder = new MultipartRequestTableFeaturesBuilder();
        List<TableFeatures> tableFeaturesList = new ArrayList<>();
        TableFeaturesBuilder tableFeaturesBuilder = new TableFeaturesBuilder();
        tableFeaturesBuilder.setTableId((short) 8);
        tableFeaturesBuilder.setName("AAAABBBBCCCCDDDDEEEEFFFFGGGG");
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}));
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}));
        tableFeaturesBuilder.setConfig(new TableConfig(true));
        tableFeaturesBuilder.setMaxEntries(65L);
        List<TableFeatureProperties> properties = new ArrayList<>();
        
        TableFeaturePropertiesBuilder propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTNEXTTABLES);
        NextTableRelatedTableFeaturePropertyBuilder nextPropBuilder =
                new NextTableRelatedTableFeaturePropertyBuilder();
        List<NextTableIds> nextIds = new ArrayList<>();
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 1).build());
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 2).build());
        nextPropBuilder.setNextTableIds(nextIds);
        propBuilder.addAugmentation(NextTableRelatedTableFeatureProperty.class, nextPropBuilder.build());
        properties.add(propBuilder.build());
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTNEXTTABLESMISS);
        nextPropBuilder = new NextTableRelatedTableFeaturePropertyBuilder();
        nextIds = new ArrayList<>();
        nextIds.add(new NextTableIdsBuilder().setTableId((short) 3).build());
        nextPropBuilder.setNextTableIds(nextIds);
        propBuilder.addAugmentation(NextTableRelatedTableFeatureProperty.class, nextPropBuilder.build());
        properties.add(propBuilder.build());
        
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTINSTRUCTIONS);
        InstructionRelatedTableFeaturePropertyBuilder insPropBuilder =
                new InstructionRelatedTableFeaturePropertyBuilder();
        List<Instructions> insIds = new ArrayList<>();
        InstructionsBuilder insBuilder = new InstructionsBuilder();
        insBuilder.setType(WriteActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(GotoTable.class);
        insIds.add(insBuilder.build());
        insPropBuilder.setInstructions(insIds);
        propBuilder.addAugmentation(InstructionRelatedTableFeatureProperty.class, insPropBuilder.build());
        properties.add(propBuilder.build());
        
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTINSTRUCTIONSMISS);
        insPropBuilder = new InstructionRelatedTableFeaturePropertyBuilder();
        insIds = new ArrayList<>();
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(WriteMetadata.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(ApplyActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(Meter.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(ClearActions.class);
        insIds.add(insBuilder.build());
        insBuilder = new InstructionsBuilder();
        insBuilder.setType(GotoTable.class);
        insIds.add(insBuilder.build());
        insPropBuilder.setInstructions(insIds);
        propBuilder.addAugmentation(InstructionRelatedTableFeatureProperty.class, insPropBuilder.build());
        properties.add(propBuilder.build());
        
        tableFeaturesBuilder.setTableFeatureProperties(properties);
        tableFeaturesList.add(tableFeaturesBuilder.build());
        
        tableFeaturesBuilder = new TableFeaturesBuilder();
        tableFeaturesBuilder.setTableId((short) 8);
        tableFeaturesBuilder.setName("AAAABBBBCCCCDDDDEEEEFFFFGGGG");
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}));
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}));
        tableFeaturesBuilder.setConfig(new TableConfig(true));
        tableFeaturesBuilder.setMaxEntries(65L);
        
        properties = new ArrayList<>();
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTMATCH);
        OxmRelatedTableFeaturePropertyBuilder oxmBuilder = new OxmRelatedTableFeaturePropertyBuilder();
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(InPhyPort.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(Nxm0Class.class);
        entriesBuilder.setOxmMatchField(InPort.class);
        entriesBuilder.setHasMask(true);
        entries.add(entriesBuilder.build());
        oxmBuilder.setMatchEntries(entries);
        propBuilder.addAugmentation(OxmRelatedTableFeatureProperty.class, oxmBuilder.build());
        properties.add(propBuilder.build());
        
        tableFeaturesList.add(tableFeaturesBuilder.build());
        
        propBuilder = new TableFeaturePropertiesBuilder();
        propBuilder.setType(TableFeaturesPropType.OFPTFPTAPPLYSETFIELD);
        oxmBuilder = new OxmRelatedTableFeaturePropertyBuilder();
        entries = new ArrayList<>();
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(OpenflowBasicClass.class);
        entriesBuilder.setOxmMatchField(InPhyPort.class);
        entriesBuilder.setHasMask(false);
        entries.add(entriesBuilder.build());
        entriesBuilder = new MatchEntriesBuilder();
        entriesBuilder.setOxmClass(Nxm0Class.class);
        entriesBuilder.setOxmMatchField(InPort.class);
        entriesBuilder.setHasMask(true);
        entries.add(entriesBuilder.build());
        oxmBuilder.setMatchEntries(entries);
        propBuilder.addAugmentation(OxmRelatedTableFeatureProperty.class, oxmBuilder.build());
        properties.add(propBuilder.build());
        
        tableFeaturesList.add(tableFeaturesBuilder.build());
        
        
        featuresBuilder.setTableFeatures(tableFeaturesList);
        caseBuilder.setMultipartRequestTableFeatures(featuresBuilder.build());
        builder.setMultipartRequestBody(caseBuilder.build());
        MultipartRequestInput message = builder.build();

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestInputFactory factory = MultipartRequestInputFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);

        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", 1, out.readUnsignedShort());
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        out.skipBytes(2); // TODO - add assert length
        Assert.assertEquals("Wrong table-id", 8, out.readUnsignedByte());
        out.skipBytes(5);
        Assert.assertEquals("Wrong name", "AAAABBBBCCCCDDDDEEEEFFFFGGGG",
                ByteBufUtils.decodeNullTerminatedString(out, 32));
        byte[] metadataMatch = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataMatch);
        Assert.assertArrayEquals("Wrong metadata-match",
                new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}, metadataMatch);
        byte[] metadataWrite = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataWrite);
        Assert.assertArrayEquals("Wrong metadata-write",
                new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}, metadataWrite);
        Assert.assertEquals("Wrong config", 8, out.readUnsignedInt());
        Assert.assertEquals("Wrong max-entries", 65, out.readUnsignedInt());
        Assert.assertEquals("Wrong property type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong next-table-id", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong next-table-id", 2, out.readUnsignedByte());
        out.skipBytes(2);
        Assert.assertEquals("Wrong property type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong next-table-id", 3, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong property type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong property type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong property length", 24, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong instruction length", 4, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong table-id", 8, out.readUnsignedByte());
        out.skipBytes(5);
        Assert.assertEquals("Wrong name", "AAAABBBBCCCCDDDDEEEEFFFFGGGG",
                ByteBufUtils.decodeNullTerminatedString(out, 32));
        metadataMatch = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataMatch);
        Assert.assertArrayEquals("Wrong metadata-match",
                new byte[] {0x00, 0x01, 0x02, 0x03, 0x01, 0x04, 0x08, 0x01}, metadataMatch);
        metadataWrite = new byte[EncodeConstants.SIZE_OF_LONG_IN_BYTES];
        out.readBytes(metadataWrite);
        Assert.assertArrayEquals("Wrong metadata-write",
                new byte[] {0x00, 0x07, 0x01, 0x05, 0x01, 0x00, 0x03, 0x01}, metadataWrite);
        Assert.assertEquals("Wrong config", 8, out.readUnsignedInt());
        Assert.assertEquals("Wrong max-entries", 65, out.readUnsignedInt());
    }
}
