/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.OF10MatchSerializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueue;

/**
 * @author michal.polkorab
 *
 */
public class OF10StatsRequestMessageFactory implements OFSerializer<MultipartRequestMessage> {

    private static final byte MESSAGE_TYPE = 18;
    private static final int MESSAGE_LENGTH = 16;

    private static OF10StatsRequestMessageFactory instance; 
    
    private OF10StatsRequestMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10StatsRequestMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10StatsRequestMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            MultipartRequestMessage message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getType().getIntValue());
        out.writeShort(createMultipartRequestFlagsBitmask(message.getFlags()));
        if (message.getMultipartRequestBody() instanceof MultipartRequestFlow) {
            encodeFlowBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestAggregate) {
            encodeAggregateBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestPortStats) {
            encodePortBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestQueue) {
            encodeQueueBody(message.getMultipartRequestBody(), out);
        } else if (message.getMultipartRequestBody() instanceof MultipartRequestExperimenter) {
            encodeExperimenterBody(message.getMultipartRequestBody(), out);
        }
    }
    
    @Override
    public int computeLength(MultipartRequestMessage message) {
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
    public int computeBodyLength(MultipartRequestMessage message) {
        int length = 0;
        MultipartType type = message.getType();
        if (type.equals(MultipartType.OFPMPFLOW)) {
            final byte FLOW_BODY_LENGTH = 44;
            length += FLOW_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPAGGREGATE)) {
            final byte AGGREGATE_BODY_LENGTH = 44;
            length += AGGREGATE_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPPORTSTATS)) {
            final byte PORT_STATS_BODY_LENGTH = 8;
            length += PORT_STATS_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPQUEUE)) {
            final byte QUEUE_BODY_LENGTH = 8;
            length += QUEUE_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPEXPERIMENTER)) {
            final byte EXPERIMENTER_BODY_LENGTH = 4;
            MultipartRequestExperimenter body = (MultipartRequestExperimenter) message.getMultipartRequestBody();
            length += EXPERIMENTER_BODY_LENGTH;
            if (body.getData() != null) {
                length += body.getData().length;
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
    
    private static void encodeFlowBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        encodeFlowAndAggregateBody(multipartRequestBody, output);
    }
    
    private static void encodeAggregateBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        encodeFlowAndAggregateBody(multipartRequestBody, output);
    }

    private static void encodeFlowAndAggregateBody(
            MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY = 1;
        MultipartRequestFlow flow = (MultipartRequestFlow) multipartRequestBody;
        OF10MatchSerializer.encodeMatchV10(output, flow.getMatchV10());
        output.writeByte(flow.getTableId().shortValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY, output);
        output.writeShort(flow.getOutPort().intValue());
    }
    
    private static void encodePortBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADDING_IN_MULTIPART_REQUEST_PORT_BODY = 6;
        MultipartRequestPortStats portstats = (MultipartRequestPortStats) multipartRequestBody;
        output.writeShort(portstats.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_PORT_BODY, output);
    }
    
    private static void encodeQueueBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        final byte PADING_IN_QUEUE_BODY = 2;
        MultipartRequestQueue queue = (MultipartRequestQueue) multipartRequestBody;
        output.writeShort(queue.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADING_IN_QUEUE_BODY, output);
        output.writeInt(queue.getQueueId().intValue());
    }
    
    private static void encodeExperimenterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestExperimenter experimenter = (MultipartRequestExperimenter) multipartRequestBody;
        output.writeInt(experimenter.getExperimenter().intValue());
        output.writeBytes(experimenter.getData());
    }
    
}
