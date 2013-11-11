/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterQueuePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.RateQueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.RateQueuePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.Queues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.QueuesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueuePropertyBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class QueueGetConfigReplyMessageFactory implements OFDeserializer<GetQueueConfigOutput> {

    private static final byte PADDING_IN_QUEUE_GET_CONFIG_REPLY_HEADER = 4;
    private static final byte PADDING_IN_PACKET_QUEUE_HEADER = 6;
    private static final byte PADDING_IN_QUEUE_PROPERTY_HEADER = 4;
    private static final int PADDING_IN_RATE_QUEUE_PROPERTY = 6;
    private static final int PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY = 4;
    private static final byte PACKET_QUEUE_LENGTH = 16;

    private static QueueGetConfigReplyMessageFactory instance;
    
    private QueueGetConfigReplyMessageFactory() {
        // singleton
    }
    
    /**
     * 
     * @return singleton factory
     */
    public static synchronized QueueGetConfigReplyMessageFactory getInstance(){
        
        if(instance == null){
            instance = new QueueGetConfigReplyMessageFactory();
        }
        return instance;
    }
    
    @Override
    public GetQueueConfigOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetQueueConfigOutputBuilder builder = new GetQueueConfigOutputBuilder();
        builder.setVersion(version);
        builder.setXid((rawMessage.readUnsignedInt()));
        builder.setPort(new PortNumber(rawMessage.readUnsignedInt()));
        rawMessage.skipBytes(PADDING_IN_QUEUE_GET_CONFIG_REPLY_HEADER);
        builder.setQueues(createQueuesList(rawMessage));
        return builder.build();
    }
    
    private static List<Queues> createQueuesList(ByteBuf input){
        List<Queues> queuesList = new ArrayList<>();
        while (input.readableBytes() > 0) {
            QueuesBuilder queueBuilder = new QueuesBuilder();
            queueBuilder.setQueueId(new QueueId(input.readUnsignedInt()));
            queueBuilder.setPort(new PortNumber(input.readUnsignedInt()));
            int length = input.readUnsignedShort();
            input.skipBytes(PADDING_IN_PACKET_QUEUE_HEADER);
            queueBuilder.setQueueProperty(createPropertiesList(input, length - PACKET_QUEUE_LENGTH));
            queuesList.add(queueBuilder.build());
        } 
        return queuesList;
    }
    
    private static List<QueueProperty> createPropertiesList(ByteBuf input, int length){
        int propertiesLength = length;
        List<QueueProperty> propertiesList = new ArrayList<>();
        while (propertiesLength > 0) {
            QueuePropertyBuilder propertiesBuilder = new QueuePropertyBuilder();
            QueueProperties property = QueueProperties.forValue(input.readUnsignedShort());
            propertiesBuilder.setProperty(property);
            int currentPropertyLength = input.readUnsignedShort();
            propertiesLength -= currentPropertyLength;
            input.skipBytes(PADDING_IN_QUEUE_PROPERTY_HEADER);
            if (property.equals(QueueProperties.OFPQTMINRATE) || property.equals(QueueProperties.OFPQTMAXRATE)) {
                RateQueuePropertyBuilder rateBuilder = new RateQueuePropertyBuilder();
                rateBuilder.setRate(input.readUnsignedShort());
                propertiesBuilder.addAugmentation(RateQueueProperty.class, rateBuilder.build());
                input.skipBytes(PADDING_IN_RATE_QUEUE_PROPERTY);
            } else if (property.equals(QueueProperties.OFPQTEXPERIMENTER)) {
                ExperimenterQueuePropertyBuilder expBuilder = new ExperimenterQueuePropertyBuilder();
                expBuilder.setExperimenter(input.readUnsignedInt());
                input.skipBytes(PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY);
                expBuilder.setData(input.readBytes(currentPropertyLength
                        - Integer.SIZE / Byte.SIZE - PADDING_IN_EXPERIMENTER_QUEUE_PROPERTY).array());
                propertiesBuilder.addAugmentation(RateQueueProperty.class, expBuilder.build());
            }
            propertiesList.add(propertiesBuilder.build());
        }
        return propertiesList;
    }

}
