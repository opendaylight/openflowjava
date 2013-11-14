/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.RateQueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.RateQueuePropertyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.Queues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.QueuesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueuePropertyBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class QueueGetConfigReplyMessageFactoryTest {

    /**
     * Testing {@link QueueGetConfigReplyMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 00 00 03 00 00 00 00 00 00 00 01 00 00 00 03 00 20 00 00 00 00 00 00 00 02 00 10 00 00 00 00 00 05 00 00 00 00 00 00");
        GetQueueConfigOutput builtByFactory = BufferHelper.decodeV13(QueueGetConfigReplyMessageFactory.getInstance(), bb);
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong port", 3L, builtByFactory.getPort().getValue().longValue());
        Assert.assertEquals("Wrong queues", builtByFactory.getQueues(), createQueuesList());
    }
    
    private static List<Queues> createQueuesList(){
        List<Queues> queuesList = new ArrayList<>();
        QueuesBuilder qb = new QueuesBuilder();
        qb.setQueueId(new QueueId(1L));
        qb.setPort(new PortNumber(3L));
        qb.setQueueProperty(createPropertiesList());
        queuesList.add(qb.build());
        
        return queuesList;
    }
    
    private static List<QueueProperty> createPropertiesList(){
        List<QueueProperty> propertiesList = new ArrayList<>();
        QueuePropertyBuilder pb = new QueuePropertyBuilder();
        pb.setProperty(QueueProperties.forValue(2));
        RateQueuePropertyBuilder rateBuilder = new RateQueuePropertyBuilder();
        rateBuilder.setRate(5);
        pb.addAugmentation(RateQueueProperty.class, rateBuilder.build());
        propertiesList.add(pb.build());
        return propertiesList;
    }
}
