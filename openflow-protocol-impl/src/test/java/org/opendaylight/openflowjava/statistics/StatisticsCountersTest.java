/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.statistics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madamjak
 *
 */
public class StatisticsCountersTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounters.class);
    private StatisticsCounters statCounters;

    @Before
    public void initTest(){
        statCounters = StatisticsCounters.getInstance();
    }

    @Test
    public void testCounterAll() throws InterruptedException{
        int testCount = 4;
        incrementCounter(CounterEventTypes.DS_ENTERED_OFJAVA,testCount);
        incrementCounter(CounterEventTypes.DS_ENCODE_SUCCESS,testCount);
        incrementCounter(CounterEventTypes.DS_ENCODE_FAIL,testCount);
        incrementCounter(CounterEventTypes.US_DECODE_FAIL,testCount);
        incrementCounter(CounterEventTypes.US_DECODE_SUCCESS,testCount);
        incrementCounter(CounterEventTypes.US_MESSAGE_PASS,testCount);
        incrementCounter(CounterEventTypes.US_RECEIVED_IN_OFJAVA,testCount);
        LOGGER.debug("Waiting to process event queue");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.DS_ENTERED_OFJAVA, testCount, statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.DS_ENCODE_SUCCESS, testCount, statCounters.getCounter(CounterEventTypes.DS_ENCODE_SUCCESS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.DS_ENCODE_FAIL, testCount, statCounters.getCounter(CounterEventTypes.DS_ENCODE_FAIL).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.US_DECODE_FAIL, testCount, statCounters.getCounter(CounterEventTypes.US_DECODE_FAIL).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.US_DECODE_SUCCESS, testCount, statCounters.getCounter(CounterEventTypes.US_DECODE_SUCCESS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.US_MESSAGE_PASS, testCount, statCounters.getCounter(CounterEventTypes.US_MESSAGE_PASS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value " + CounterEventTypes.US_RECEIVED_IN_OFJAVA, testCount, statCounters.getCounter(CounterEventTypes.US_RECEIVED_IN_OFJAVA).getCounterValue());
        statCounters.resetCounters();
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.DS_ENTERED_OFJAVA, 0, statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.DS_ENCODE_SUCCESS, 0, statCounters.getCounter(CounterEventTypes.DS_ENCODE_SUCCESS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.DS_ENCODE_FAIL, 0, statCounters.getCounter(CounterEventTypes.DS_ENCODE_FAIL).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.US_DECODE_FAIL, 0, statCounters.getCounter(CounterEventTypes.US_DECODE_FAIL).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.US_DECODE_SUCCESS, 0, statCounters.getCounter(CounterEventTypes.US_DECODE_SUCCESS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.US_MESSAGE_PASS, 0, statCounters.getCounter(CounterEventTypes.US_MESSAGE_PASS).getCounterValue());
        Assert.assertEquals("Wrong - bad counter value after reset " + CounterEventTypes.US_RECEIVED_IN_OFJAVA, 0, statCounters.getCounter(CounterEventTypes.US_RECEIVED_IN_OFJAVA).getCounterValue());
    }

    @Test
    public void testCounterLastRead() throws InterruptedException{
        int testCount = 4;
        incrementCounter(CounterEventTypes.DS_ENTERED_OFJAVA,testCount);
        LOGGER.debug("Waiting to process event queue");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad last read value.", 0,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterLastReadValue());
        Assert.assertEquals("Wrong - bad value", 4,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue(false));
        Assert.assertEquals("Wrong - bad last read value.", 0,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterLastReadValue());
        Assert.assertEquals("Wrong - bad last read value.", 4,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue());
        Assert.assertEquals("Wrong - bad last read value.", 4,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterLastReadValue());
        incrementCounter(CounterEventTypes.DS_ENTERED_OFJAVA,testCount);
        LOGGER.debug("Waiting to process event queue");
        Thread.sleep(StatisticsCounters.EVENT_QUEUE_PROCESS_DELAY+100);
        Assert.assertEquals("Wrong - bad last read value.", 4,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterLastReadValue());
        Assert.assertEquals("Wrong - bad last read value.", 8,statCounters.getCounter(CounterEventTypes.DS_ENTERED_OFJAVA).getCounterValue());
    }

    private void incrementCounter(CounterEventTypes cet, int count){
        for(int i = 0; i< count; i++){
            statCounters.incrementCounter(cet);
        }
    }
}
