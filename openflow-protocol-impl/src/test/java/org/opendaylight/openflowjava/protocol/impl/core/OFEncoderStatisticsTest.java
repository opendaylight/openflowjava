/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.core.connection.MessageListenerWrapper;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.openflowjava.statistics.CounterEventTypes;
import org.opendaylight.openflowjava.statistics.StatisticsCounters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madamjak
 *
 */
public class OFEncoderStatisticsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OFEncoderStatisticsTest.class);

    @Mock ChannelHandlerContext mockChHndlrCtx ;
    @Mock SerializationFactory mockSerializationFactory ;
    @Mock MessageListenerWrapper wrapper;
    @Mock OfHeader mockMsg ;
    @Mock ByteBuf mockOut ;
    @Mock Future<Void> future;
    @Mock GenericFutureListener<Future<Void>> listener;

    private StatisticsCounters statCounters;
    private OFEncoder ofEncoder;

    @Before
    public void initTest(){
        MockitoAnnotations.initMocks(this);
        ofEncoder = new OFEncoder() ;
        ofEncoder.setSerializationFactory(mockSerializationFactory) ;
        statCounters = StatisticsCounters.getInstance();
    }

    @After
    public void tierDown(){
        statCounters.resetCounters();
    }

    @Test
    public void testEncodeSuccessCounter() throws InterruptedException{
        int count = 4;
        when(mockOut.readableBytes()).thenReturn(1);
        when(wrapper.getMsg()).thenReturn(mockMsg);
        when(wrapper.getMsg().getVersion()).thenReturn((short) EncodeConstants.OF13_VERSION_ID);
        try {
            for(int i = 0; i< count; i++){
                ofEncoder.encode(mockChHndlrCtx, wrapper, mockOut);
            }
        } catch (Exception e) {
            Assert.fail();
        }
        LOGGER.debug("Waiting to event queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad counter value for OFEncoder encode succesfully ", count, statCounters.getCounter(CounterEventTypes.DS_ENCODE_SUCCESS).getCounterValue());
    }

    @Test
    public void testEncodeEncodeFailCounter() throws InterruptedException {
        int count = 2;
        when(wrapper.getMsg()).thenReturn(mockMsg);
        when(wrapper.getListener()).thenReturn(listener);
        when(wrapper.getMsg().getVersion()).thenReturn((short) EncodeConstants.OF13_VERSION_ID);
        doThrow(new IllegalArgumentException()).when(mockSerializationFactory).messageToBuffer(anyShort(),any(ByteBuf.class), any(DataObject.class));
        try {
            for(int i = 0; i< count; i++){
                ofEncoder.encode(mockChHndlrCtx, wrapper, mockOut);
            }
        } catch (Exception e) {
            Assert.fail();
        }
        LOGGER.debug("Waiting to event queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad counter value for OFEncoder fail encode", count, statCounters.getCounter(CounterEventTypes.DS_ENCODE_FAIL).getCounterValue());
    }
}
