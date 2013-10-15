/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.packet.queue.Properties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.packet.queue.PropertiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.Queues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.QueuesBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class QueueGetConfigReplyMessageFactory implements OFDeserializer<GetQueueConfigOutput> {

    private static QueueGetConfigReplyMessageFactory instance;
    private static final byte PADDING_IN_QUEUE_GET_CONFIG_REPLY_HEADER = 4;
    private static final byte PADDING_IN_PACKET_QUEUE_HEADER = 6;
    private static final byte PADDING_IN_QUEUE_PROPERTY_HEADER = 4;
    
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
        List<Queues> queuesList = new ArrayList<Queues>();
        QueuesBuilder queueBuilder = new QueuesBuilder();
        while (input.readableBytes() > 0) {
            queueBuilder.setQueueId(new QueueId(input.readUnsignedInt()));
            queueBuilder.setPort(new PortNumber(input.readUnsignedInt()));
            input.skipBytes(2);
            input.skipBytes(PADDING_IN_PACKET_QUEUE_HEADER);
            queueBuilder.setProperties(createPropertiesList(input));
            queuesList.add(queueBuilder.build());
        } 
        return queuesList;
    }
    
    private static List<Properties> createPropertiesList(ByteBuf propertiesInput){
        List<Properties> propertiesList = new ArrayList<Properties>();
        PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
        propertiesBuilder.setProperty(QueueProperty.forValue(propertiesInput.readUnsignedShort()));
        propertiesInput.skipBytes(2);
        propertiesInput.skipBytes(PADDING_IN_QUEUE_PROPERTY_HEADER);
        propertiesList.add(propertiesBuilder.build());
        return propertiesList;
    }

}
