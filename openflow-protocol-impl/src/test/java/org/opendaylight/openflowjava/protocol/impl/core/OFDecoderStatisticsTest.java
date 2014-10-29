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
import static org.mockito.Mockito.when;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.statistics.CounterEventTypes;
import org.opendaylight.openflowjava.statistics.StatisticsCounters;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madamjak
 *
 */
public class OFDecoderStatisticsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OFEncoderStatisticsTest.class);

    @Mock ChannelHandlerContext mockChHndlrCtx ;
    @Mock DeserializationFactory mockDeserializationFactory ;
    @Mock DataObject mockDataObject ;

    private OFDecoder ofDecoder ;
    private ByteBuf writeObj;
    private VersionMessageWrapper inMsg;
    private List<Object> outList;
    private StatisticsCounters statCounters;

    /**
     * Sets up test environment
     * 
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ofDecoder = new OFDecoder() ;
        ofDecoder.setDeserializationFactory( mockDeserializationFactory ) ;
        outList = new ArrayList<>();
        statCounters = StatisticsCounters.getInstance();
    }

    @After
    public void tierDown(){
        statCounters.resetCounters();
    }

    @Test
    public void testDecodeSuccesfullCounter() throws InterruptedException {
        int count = 4;
        when(mockDeserializationFactory.deserialize( any(ByteBuf.class), anyShort() )).thenReturn(mockDataObject);
        try {
            for(int i = 0; i<count; i++){
                writeObj = ByteBufUtils.hexStringToByteBuf("16 03 01 00");
                inMsg = new VersionMessageWrapper( (short)8, writeObj );
                ofDecoder.decode(mockChHndlrCtx, inMsg, outList);
            }
        } catch (Exception e) {
            Assert.fail();
        }
        LOGGER.debug("Waiting to event queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad counter value for OFEncoder encode succesfully ", count, statCounters.getCounter(CounterEventTypes.US_DECODE_SUCCESS).getCounterValue());
    }

    /**
     * @throws InterruptedException 
     * 
     */
    @Test
    public void testDecodeFailCounter() throws InterruptedException {
        int count = 2;
        when(mockDeserializationFactory.deserialize( any(ByteBuf.class), anyShort() ))
        .thenThrow(new IllegalArgumentException()) ;
        try {
            for(int i = 0; i<count; i++){
                writeObj = ByteBufUtils.hexStringToByteBuf("16 03 01 00");
                inMsg = new VersionMessageWrapper( (short)8, writeObj );
                ofDecoder.decode(mockChHndlrCtx, inMsg, outList);
            }
        } catch (Exception e) {
            System.out.println("a");
            Assert.fail();
        }
        LOGGER.debug("Waiting to event queue process");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad counter value for OFEncoder encode succesfully ", count, statCounters.getCounter(CounterEventTypes.US_DECODE_FAIL).getCounterValue());

    }
}
