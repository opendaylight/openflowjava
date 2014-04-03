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
import java.util.Map;

import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerTable;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.match.v10.grouping.MatchV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.MultipartRequestBody;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestAggregateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestDescCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestFlowCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestPortStatsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestQueueCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestTableCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.aggregate._case.MultipartRequestAggregate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.experimenter._case.MultipartRequestExperimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.flow._case.MultipartRequestFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.port.stats._case.MultipartRequestPortStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.multipart.request.queue._case.MultipartRequestQueue;

/**
 * Translates StatsRequest messages
 * @author michal.polkorab
 */
public class OF10StatsRequestInputFactory implements OFSerializer<MultipartRequestInput> {

    private static final byte MESSAGE_TYPE = 16;
    private static final byte FLOW_BODY_LENGTH = 44;
    private static final byte AGGREGATE_BODY_LENGTH = 44;
    private static final byte PORT_STATS_BODY_LENGTH = 8;
    private static final byte QUEUE_BODY_LENGTH = 8;
    private static final byte EXPERIMENTER_BODY_LENGTH = 4;
    private static final byte PADDING_IN_MULTIPART_REQUEST_FLOW_BODY = 1;
    private static final byte PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY = 1;
    private static final byte PADDING_IN_MULTIPART_REQUEST_PORT_BODY = 6;
    private static final byte PADING_IN_QUEUE_BODY = 2;

    private SerializerTable serializerTable;

    @Override
    public void serialize(MultipartRequestInput object, ByteBuf outBuffer) {
        ByteBufUtils.writeOFHeader(MESSAGE_TYPE, object, outBuffer, EncodeConstants.OFHEADER_SIZE);
        outBuffer.writeShort(object.getType().getIntValue());
        outBuffer.writeShort(createMultipartRequestFlagsBitmask(object.getFlags()));
        if (object.getMultipartRequestBody() instanceof MultipartRequestDescCase) {
            encodeDescBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestFlowCase) {
            encodeFlowBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestAggregateCase) {
            encodeAggregateBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestTableCase) {
            encodeTableBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestPortStatsCase) {
            encodePortBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestQueueCase) {
            encodeQueueBody(object.getMultipartRequestBody(), outBuffer);
        } else if (object.getMultipartRequestBody() instanceof MultipartRequestExperimenterCase) {
            encodeExperimenterBody(object.getMultipartRequestBody(), outBuffer);
        }
        ByteBufUtils.updateOFHeaderLength(outBuffer);
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
            length += FLOW_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPAGGREGATE)) {
            length += AGGREGATE_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPPORTSTATS)) {
            length += PORT_STATS_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPQUEUE)) {
            length += QUEUE_BODY_LENGTH;
        } else if (type.equals(MultipartType.OFPMPEXPERIMENTER)) {
            MultipartRequestExperimenterCase bodyCase = (MultipartRequestExperimenterCase) message.getMultipartRequestBody();
            MultipartRequestExperimenter body = bodyCase.getMultipartRequestExperimenter();
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
    
    private void encodeFlowBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestFlowCase flowCase = (MultipartRequestFlowCase) multipartRequestBody;
        MultipartRequestFlow flow = flowCase.getMultipartRequestFlow();
        serializerTable.getSerializer(new MessageTypeKey<>(EncodeConstants.OF10_VERSION_ID, MatchV10.class))
            .serialize(flow.getMatchV10(), output);
        output.writeByte(flow.getTableId().shortValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_FLOW_BODY, output);
        output.writeShort(flow.getOutPort().intValue());
    }
    
    private void encodeAggregateBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestAggregateCase aggregateCase = (MultipartRequestAggregateCase) multipartRequestBody;
        MultipartRequestAggregate aggregate = aggregateCase.getMultipartRequestAggregate();
        serializerTable.getSerializer(new MessageTypeKey<>(EncodeConstants.OF10_VERSION_ID, MatchV10.class))
            .serialize(aggregate.getMatchV10(), output);
        output.writeByte(aggregate.getTableId().shortValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_AGGREGATE_BODY, output);
        output.writeShort(aggregate.getOutPort().intValue());
    }

    private static void encodePortBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestPortStatsCase portstatsCase = (MultipartRequestPortStatsCase) multipartRequestBody;
        MultipartRequestPortStats portstats = portstatsCase.getMultipartRequestPortStats();
        output.writeShort(portstats.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADDING_IN_MULTIPART_REQUEST_PORT_BODY, output);
    }
    
    private static void encodeQueueBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestQueueCase queueCase = (MultipartRequestQueueCase) multipartRequestBody;
        MultipartRequestQueue queue = queueCase.getMultipartRequestQueue();
        output.writeShort(queue.getPortNo().intValue());
        ByteBufUtils.padBuffer(PADING_IN_QUEUE_BODY, output);
        output.writeInt(queue.getQueueId().intValue());
    }
    
    private static void encodeExperimenterBody(MultipartRequestBody multipartRequestBody, ByteBuf output) {
        MultipartRequestExperimenterCase experimenterCase = (MultipartRequestExperimenterCase) multipartRequestBody;
        MultipartRequestExperimenter experimenter = experimenterCase.getMultipartRequestExperimenter();
        output.writeInt(experimenter.getExperimenter().intValue());
        output.writeBytes(experimenter.getData());
    }

    @Override
    public void injectSerializerTable(SerializerTable table) {
        this.serializerTable = table;
    }
    
}
