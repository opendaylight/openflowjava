/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.statistics;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madamjak
 *
 */
public final class StatisticsCounters {

    private static final int MAX_QUEUE_ENTRIES = 10000;
    private static final String TIMER_NAME = "SC_Timer";
    public static final int EVENT_QUEUE_PROCESS_DELAY = 500; // time (miliseconds) to queue process
    private static StatisticsCounters instanceHolder;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounters.class);
    
    private Map<CounterEventTypes,Counter> countersMap;
    private ConcurrentLinkedQueue<CounterEventTypes> queueEvents;
    private Timer timerProcessQueue;
    private QueueProcessor queueProcessor;
    
    public static StatisticsCounters  getInstance(){
        if (instanceHolder == null){
            instanceHolder = new StatisticsCounters();
        }
        return instanceHolder;
    }

    private StatisticsCounters() {
        queueEvents = new ConcurrentLinkedQueue<>();
        countersMap = new ConcurrentHashMap<>();
        countersMap.put(CounterEventTypes.DS_ENCODE_FAIL, new Counter());
        countersMap.put(CounterEventTypes.DS_ENCODE_SUCCESS, new Counter());
        countersMap.put(CounterEventTypes.DS_ENTERED_OFJAVA, new Counter());
        countersMap.put(CounterEventTypes.US_DECODE_FAIL, new Counter());
        countersMap.put(CounterEventTypes.US_DECODE_SUCCESS, new Counter());
        countersMap.put(CounterEventTypes.US_MESSAGE_PASS, new Counter());
        countersMap.put(CounterEventTypes.US_RECEIVED_IN_OFJAVA, new Counter());
        timerProcessQueue = new Timer(TIMER_NAME);
        queueProcessor = new QueueProcessor();
        timerProcessQueue.schedule(queueProcessor, EVENT_QUEUE_PROCESS_DELAY);
        LOGGER.debug("StaticsCounters has been created");
    }

    /**
     * Get given counter
     * @param counterEventKey - key to identify counter
     * @return
     */
    public Counter getCounter(CounterEventTypes counterEventKey){
        if(counterEventKey == null){
            throw new IllegalArgumentException("counterEventKey can not be null");
        }
        return countersMap.get(counterEventKey);
    }

    /**
     * Increment value of given counter
     * @param counterEventKey - key to identify counter
     * @return
     */
    public void incrementCounter(CounterEventTypes counterEventKey){
        if(counterEventKey == null){
            throw new IllegalArgumentException("counterEventKey can not be null");
        }
        queueEvents.add(counterEventKey);
//        if(queueEvents.size()>= MAX_QUEUE_ENTRIES){
//            queueProcessor.cancel();
//            timerProcessQueue.purge();
//            queueProcessor = new QueueProcessor();
//            timerProcessQueue.schedule(queueProcessor, 0);
//        }
    }

    /**
     * Set values of all counter to 0 (zero)
     */
    public void resetCounters(){
        queueProcessor.cancel();
        queueEvents.clear();
        countersMap.get(CounterEventTypes.DS_ENCODE_FAIL).reset();
        countersMap.get(CounterEventTypes.DS_ENCODE_SUCCESS).reset();
        countersMap.get(CounterEventTypes.DS_ENTERED_OFJAVA).reset();
        countersMap.get(CounterEventTypes.US_DECODE_FAIL).reset();
        countersMap.get(CounterEventTypes.US_DECODE_SUCCESS).reset();
        countersMap.get(CounterEventTypes.US_MESSAGE_PASS).reset();
        countersMap.get(CounterEventTypes.US_RECEIVED_IN_OFJAVA).reset();
        queueProcessor = new QueueProcessor();
        timerProcessQueue.schedule(queueProcessor, EVENT_QUEUE_PROCESS_DELAY);
        LOGGER.debug("StaticsCounters has been reset");
    }

    private void processQueue(){
        if(queueEvents.isEmpty()){
            return;
        }
        CounterEventTypes cet = queueEvents.poll();
        while(cet != null){
            countersMap.get(cet).incrementCounter();
            cet = queueEvents.poll();
        }
        LOGGER.debug("QueueEvents has been processed");
    }

    private class QueueProcessor extends TimerTask {
        @Override
        public void run() {
            processQueue();
            queueProcessor = new QueueProcessor();
            timerProcessQueue.schedule(queueProcessor, EVENT_QUEUE_PROCESS_DELAY);
        }
    }
}
