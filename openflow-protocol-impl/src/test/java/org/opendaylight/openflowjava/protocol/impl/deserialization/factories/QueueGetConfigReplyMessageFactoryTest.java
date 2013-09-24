/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.packet.queue.Properties;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.packet.queue.PropertiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.Queues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.get.config.reply.QueuesBuilder;

import com.google.common.collect.ComparisonChain;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class QueueGetConfigReplyMessageFactoryTest {

    @Test
    public void test(){
        
        ByteBuf bb = BufferHelper.buildBuffer(
                                              "00 01 02 03 " + //port
                                              "00 00 00 00 " + //padding
                                              "00 00 00 01 " + //queueId 
                                              "00 00 00 01 " + //port
                                              "00 00 00 00 00 00 00 00 " + //pad
                                              "00 02 " + //property
                                              "00 00 00 00 00 00" //pad
                                             );

        GetQueueConfigOutput builtByFactory = BufferHelper.decodeV13(QueueGetConfigReplyMessageFactory.getInstance(), bb);

        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertTrue("Wrong port",66051L == builtByFactory.getPort().getValue());
        Assert.assertTrue("Wrong queues", true == compareLists(builtByFactory.getQueues(), createQueuesList()));
        
    }
    
    public List<Queues> createQueuesList(){
        
        final byte PADDING_IN_PACKET_QUEUE_HEADER = 6;
        
        List<Queues> queuesList = new ArrayList<Queues>();
        QueuesBuilder qb = new QueuesBuilder();
        
        qb.setQueueId(new QueueId((long) 1));
        qb.setPort(new PortNumber((long) 1));
        qb.setProperties(createPropertiesList());
        
        queuesList.add(qb.build());
        
        return queuesList;
    }
    
    public List<Properties> createPropertiesList(){
        
        final byte PADDING_IN_QUEUE_PROPERTY_HEADER = 4;
        
        List<Properties> propertiesList = new ArrayList<Properties>();
        PropertiesBuilder pb = new PropertiesBuilder();
        
        pb.setProperty(QueueProperty.values()[2]);
        
        propertiesList.add(pb.build());
        
        return propertiesList;
    }
    
    public boolean compareLists(List<Queues> originalList, List<Queues> testList){

        boolean decision = false;
        
        int originalListLength = originalList.size();
        int testListLength = testList.size();
        
        for(int i=0; i<originalListLength; i++){
            
            if(originalList.get(i).getPort().equals(testList.get(i).getPort()))
            {
                decision = true;
            }
            else 
            {
                decision = false;
                break;
            }
            
            if(originalList.get(i).getQueueId().equals(testList.get(i).getQueueId()))
            {
                decision = true;
            }
            else 
            {
                decision = false;
                break;
            }
            
            if(originalList.get(i).getProperties().get(0).getProperty().equals(testList.get(i).getProperties().get(0).getProperty()))
            {
                decision = true;
            }
            else 
            {
                decision = false;
                break;
            }
        }
        
        
        return decision;
    }
    
    /*   public boolean compareLists(List<Queues> originalList, List<Queues> testList){

    boolean decision = false;

    for(Queues ol: originalList){

        for(Queues tl: testList){

            if(tl.getPort().equals(ol.getPort()))
            {
                decision = true;
            }
            else 
            {
                decision = false;
            }

            if(tl.getQueueId().equals(ol.getQueueId()))
            {
                decision = true;
            }
            else 
            {
                decision = false;
            }


            for(Properties tp: tl.getProperties()){
                for(Properties op: ol.getProperties()){
                    if(tp.getProperty().equals(op.getProperty()))
                    {
                        decision = true;
                    }
                    else 
                    {
                        decision = false;
                    }
                }
            }

        }
    }

    return decision;
   }*/
}
