/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestGroupBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestMeterConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableFeaturesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features.TableFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.table.features.TableFeaturesBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestMessageFactoryTest {
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(1));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestFlow());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong flow", message.getMultipartRequestBody(), decodeRequestFlow(out));
    }
    
    private static MultipartRequestFlow createRequestFlow() {
        MultipartRequestFlowBuilder builder = new MultipartRequestFlowBuilder();
        builder.setTableId((short) 8);
        builder.setOutPort(85L);
        builder.setOutGroup(95L);
        byte[] cookie = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookieMask(new BigInteger(cookieMask));
        MultipartRequestFlow flow = builder.build();
        //TODO match field
        return flow;
    }
    
    private static MultipartRequestFlow decodeRequestFlow(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY_02 = 4;
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
        MultipartRequestFlow flow = builder.build();
        return flow;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMessageAggregateBodyFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(2));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestAggregate());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
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
    
    
    private static MultipartRequestAggregate createRequestAggregate() {
        MultipartRequestAggregateBuilder builder = new MultipartRequestAggregateBuilder();
        builder.setTableId((short) 8);
        builder.setOutPort(85L);
        builder.setOutGroup(95L);
        byte[] cookie = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookieMask(new BigInteger(cookieMask));
        MultipartRequestAggregate aggregate = builder.build();
      //TODO match field
        return aggregate;
    }
    
    private static MultipartRequestAggregate decodeRequestAggregate(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_01 = 3;
        final byte PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY_02 = 4;
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
        MultipartRequestAggregate flow = builder.build();
        return flow;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMessageTableFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(3));
        builder.setFlags(new MultipartRequestFlags(true));
        //multipart request for table does not have body
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestPortStatsMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(4));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestPortStats());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong portStatsBody", message.getMultipartRequestBody(), decodeRequestPortStats(out));
    }
    
    private static MultipartRequestPortStats createRequestPortStats() {
        MultipartRequestPortStatsBuilder builder = new MultipartRequestPortStatsBuilder();
        builder.setPortNo(2251L);
        MultipartRequestPortStats portStats = builder.build();
        return portStats;
    }
    
    private static MultipartRequestPortStats decodeRequestPortStats(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY = 4;
        MultipartRequestPortStatsBuilder builder = new MultipartRequestPortStatsBuilder();
        builder.setPortNo(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_PORTSTATS_BODY);
        MultipartRequestPortStats portRequest = builder.build();
        return portRequest;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestQueueMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(5));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestQueue());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong queueBody", message.getMultipartRequestBody(), decodeRequestQueue(out));
    }
    
    private static MultipartRequestQueue createRequestQueue() {
        MultipartRequestQueueBuilder builder = new MultipartRequestQueueBuilder();
        builder.setPortNo(2256L);
        builder.setQueueId(2211L);
        MultipartRequestQueue queue = builder.build();
        return queue;
    }
    
    private static MultipartRequestQueue decodeRequestQueue(ByteBuf output) {
        MultipartRequestQueueBuilder builder = new MultipartRequestQueueBuilder();
        builder.setPortNo(output.readUnsignedInt());
        builder.setQueueId(output.readUnsignedInt());
        MultipartRequestQueue queue = builder.build();
        return queue;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestGroupMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(6));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestGroup());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong groupBody", message.getMultipartRequestBody(), decodeRequestGroup(out));
    }
    
    private static MultipartRequestGroup createRequestGroup() {
        MultipartRequestGroupBuilder builder = new MultipartRequestGroupBuilder();
        builder.setGroupId(2258L);
        MultipartRequestGroup group = builder.build();
        return group;
    }
    
    private static MultipartRequestGroup decodeRequestGroup(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_GROUP_BODY = 4;
        MultipartRequestGroupBuilder builder = new MultipartRequestGroupBuilder();
        builder.setGroupId(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_GROUP_BODY);
        MultipartRequestGroup group = builder.build();
        return group;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMeterMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(9));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestMeter());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong meterBody", message.getMultipartRequestBody(), decodeRequestMeter(out));
    }
    
    private static MultipartRequestMeter createRequestMeter() {
        MultipartRequestMeterBuilder builder = new MultipartRequestMeterBuilder();
        builder.setMeterId(1121L);
        MultipartRequestMeter meter = builder.build();
        return meter;
    }
    
    private static MultipartRequestMeter decodeRequestMeter(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_BODY = 4;
        MultipartRequestMeterBuilder builder = new MultipartRequestMeterBuilder();
        builder.setMeterId(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_METER_BODY);
        MultipartRequestMeter meter = builder.build();
        return meter;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestMeterConfigMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(10));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestMeterConfig());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong meterConfigBody", message.getMultipartRequestBody(), decodeRequestMeterConfig(out));
    }
    
    private static MultipartRequestMeterConfig createRequestMeterConfig() {
        MultipartRequestMeterConfigBuilder builder = new MultipartRequestMeterConfigBuilder();
        builder.setMeterId(1133L);
        MultipartRequestMeterConfig meterConfig = builder.build();
        return meterConfig;
    }
    
    private static MultipartRequestMeterConfig decodeRequestMeterConfig(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY = 4;
        MultipartRequestMeterConfigBuilder builder = new MultipartRequestMeterConfigBuilder();
        builder.setMeterId(output.readUnsignedInt());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_METER_CONFIG_BODY);
        MultipartRequestMeterConfig meterConfig = builder.build();
        return meterConfig;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestExperimenterMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(0xffff));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestExperimenter());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        Assert.assertEquals("Wrong experimenterBody", message.getMultipartRequestBody(), decodeRequestExperimenter(out));
    }
    
    private static MultipartRequestExperimenter createRequestExperimenter() {
        MultipartRequestExperimenterBuilder builder = new MultipartRequestExperimenterBuilder();
        builder.setExperimenter(1133L);
        builder.setExpType(1135L);
        MultipartRequestExperimenter experimenter = builder.build();
        return experimenter;
    }
    
    private static MultipartRequestExperimenter decodeRequestExperimenter(ByteBuf output) {
        MultipartRequestExperimenterBuilder builder = new MultipartRequestExperimenterBuilder();
        builder.setExperimenter(output.readUnsignedInt());
        builder.setExpType(output.readUnsignedInt());
        MultipartRequestExperimenter experimenter = builder.build();
        return experimenter;
    }
    
    /**
     * @throws Exception
     * Testing of {@link MultipartRequestMessageFactory} for correct translation from POJO
     */
    @Test
    public void testMultipartRequestTableFeaturesMessageFactory() throws Exception {
        MultipartRequestMessageBuilder builder = new MultipartRequestMessageBuilder();
        BufferHelper.setupHeader(builder);
        builder.setType(MultipartType.forValue(0xffff));
        builder.setFlags(new MultipartRequestFlags(true));
        builder.setMultipartRequestBody(createRequestTableFeatures());
        MultipartRequestMessage message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        MultipartRequestMessageFactory factory = MultipartRequestMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength(message));
        Assert.assertEquals("Wrong type", message.getType().getIntValue(), out.readUnsignedShort());
        Assert.assertEquals("Wrong flags", message.getFlags(), decodeMultipartRequestFlags(out.readShort()));
        out.skipBytes(PADDING_IN_MULTIPART_REQUEST_MESSAGE);
        
        MultipartRequestTableFeatures messageTableFeatures = (MultipartRequestTableFeatures) message.getMultipartRequestBody();
        Assert.assertEquals("Wrong tableFeaturesBody", messageTableFeatures.getTableFeatures(), decodeRequestTableFeatures(out).getTableFeatures());
    }
    
    private static MultipartRequestTableFeatures createRequestTableFeatures() {
        MultipartRequestTableFeaturesBuilder builder = new MultipartRequestTableFeaturesBuilder();
        List<TableFeatures> tableFeaturesList = new ArrayList<>();
        TableFeaturesBuilder tableFeaturesBuilder = new TableFeaturesBuilder(); 
        tableFeaturesBuilder.setTableId((short) 8);
        tableFeaturesBuilder.setName("AAAABBBBCCCCDDDDEEEEFFFFGGGG");
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(new byte[] {0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}));
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(new byte[] {0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}));
        tableFeaturesBuilder.setConfig(new TableConfig(true));
        tableFeaturesBuilder.setMaxEntries(65L);
        TableFeatures tableFeature = tableFeaturesBuilder.build();
        tableFeaturesList.add(tableFeature);
        builder.setTableFeatures(tableFeaturesList);
        MultipartRequestTableFeatures tableFeaturesRequest = builder.build();
        return tableFeaturesRequest;
    }
    
    private static MultipartRequestTableFeatures decodeRequestTableFeatures(ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY = 5;
        final byte OFP_MAX_TABLE_NAME_LEN = 32;
        MultipartRequestTableFeaturesBuilder builder = new MultipartRequestTableFeaturesBuilder();
        List<TableFeatures> tableFeaturesList = new ArrayList<>();
        TableFeaturesBuilder tableFeaturesBuilder = new TableFeaturesBuilder();
        tableFeaturesBuilder.setTableId(output.readUnsignedByte());
        output.skipBytes(PADDING_IN_MULTIPART_REQUEST_TABLE_FEATURES_BODY);
        byte[] tableNameBytes = new byte[OFP_MAX_TABLE_NAME_LEN];
        output.readBytes(tableNameBytes);
        String tableName = new String(tableNameBytes);
        tableFeaturesBuilder.setName(tableName.trim());
        byte[] metadataMatch = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(metadataMatch);
        tableFeaturesBuilder.setMetadataMatch(new BigInteger(metadataMatch));
        byte[] metadataWrite = new byte[Long.SIZE/Byte.SIZE];
        output.readBytes(metadataWrite);
        tableFeaturesBuilder.setMetadataWrite(new BigInteger(metadataWrite));
        tableFeaturesBuilder.setConfig(decodeTableConfig(output.readInt()));
        tableFeaturesBuilder.setMaxEntries(output.readUnsignedInt());
        TableFeatures tableFeature = tableFeaturesBuilder.build();
        tableFeaturesList.add(tableFeature);
        builder.setTableFeatures(tableFeaturesList);
        MultipartRequestTableFeatures tableFeaturesRequest = builder.build();
        return tableFeaturesRequest;
    }
    
    private static TableConfig decodeTableConfig(int input) {
        final Boolean _oFPTCDEPRECATEDMASK = (input & (1 << 3)) > 0;        
        return new TableConfig(_oFPTCDEPRECATEDMASK);
    }
    
}
