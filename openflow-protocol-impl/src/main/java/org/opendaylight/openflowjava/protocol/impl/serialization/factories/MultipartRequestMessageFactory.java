/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableConfig;
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

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartRequestMessageFactory implements OFSerializer<MultipartRequestMessage> {
    private static final byte MESSAGE_TYPE = 18;
    private static final int MESSAGE_LENGTH = 16;
    private static MultipartRequestMessageFactory instance; 
    private static final byte PADDING_IN_MULTIPART_REQUEST_MESSAGE = 4;
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
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestExperimenter) {
            encodeExperimenterBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestTableFeatures) {
            encodeTableFeaturesBody(message.getMultipartRequestBody(), out);
        }
    }
    
    @Override
    public int computeLength() {
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
        //TODO match field
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
        //TODO match field
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
            //TODO properties
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
