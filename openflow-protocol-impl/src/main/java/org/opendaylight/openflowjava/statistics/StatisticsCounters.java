/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madamjak
 *
 */
public final class StatisticsCounters {

    private static StatisticsCounters instanceHolder;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounters.class);

    private final Counter cntDSEncodeFail;
    private final Counter cntDSEncodeSuccess;
    private final Counter cntDSEnteredOFJava;
    private final Counter cntUSDecodeFail;
    private final Counter cntUSDecodeSuccess;
    private final Counter cntUSMessagePass;
    private final Counter cntUSReceivedOFJava;
    //private Map<CounterEventTypes,Counter> countersMap;

    public static StatisticsCounters  getInstance(){
        if (instanceHolder == null){
            instanceHolder = new StatisticsCounters();
        }
        return instanceHolder;
    }

    private StatisticsCounters() {
        cntDSEncodeFail = new Counter();
        cntDSEncodeSuccess = new Counter();
        cntDSEnteredOFJava = new Counter();
        cntUSDecodeFail = new Counter();
        cntUSDecodeSuccess = new Counter();
        cntUSMessagePass = new Counter();
        cntUSReceivedOFJava = new Counter();
        LOGGER.debug("StaticsCounters (without Map) has been created");
//        countersMap = new ConcurrentHashMap<>();
//        countersMap.put(CounterEventTypes.DS_ENCODE_FAIL, new Counter());
//        countersMap.put(CounterEventTypes.DS_ENCODE_SUCCESS, new Counter());
//        countersMap.put(CounterEventTypes.DS_ENTERED_OFJAVA, new Counter());
//        countersMap.put(CounterEventTypes.US_DECODE_FAIL, new Counter());
//        countersMap.put(CounterEventTypes.US_DECODE_SUCCESS, new Counter());
//        countersMap.put(CounterEventTypes.US_MESSAGE_PASS, new Counter());
//        countersMap.put(CounterEventTypes.US_RECEIVED_IN_OFJAVA, new Counter());
//        LOGGER.debug("StaticsCounters (with Map) has been created");
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
        switch (counterEventKey){
            case DS_ENCODE_FAIL:
                return cntDSEncodeFail;
            case DS_ENCODE_SUCCESS:
                return cntDSEncodeSuccess;
            case DS_ENTERED_OFJAVA:
                return cntDSEnteredOFJava;
            case US_DECODE_FAIL:
                return cntUSDecodeFail;
            case US_DECODE_SUCCESS:
                return cntUSDecodeSuccess;
            case US_MESSAGE_PASS:
                return cntUSMessagePass;
            case US_RECEIVED_IN_OFJAVA:
                return cntUSReceivedOFJava;
            default:
                throw new IllegalArgumentException("unknown counterEventKey");
        }
//        return countersMap.get(counterEventKey);
    }

    /**
     * Increment value of given counter
     * @param counterEventKey - key to identify counter
     * @return
     */
    public void incrementCounter(CounterEventTypes counterEventKey){
        getCounter(counterEventKey).incrementCounter();
    }

    /**
     * Set values of all counter to 0 (zero)
     */
    public void resetCounters(){
        cntDSEncodeFail.reset(); ;
        cntDSEncodeSuccess.reset();
        cntDSEnteredOFJava.reset();
        cntUSDecodeFail.reset();
        cntUSDecodeSuccess.reset();
        cntUSMessagePass.reset();
        cntUSReceivedOFJava.reset();
//        countersMap.get(CounterEventTypes.DS_ENCODE_FAIL).reset();
//        countersMap.get(CounterEventTypes.DS_ENCODE_SUCCESS).reset();
//        countersMap.get(CounterEventTypes.DS_ENTERED_OFJAVA).reset();
//        countersMap.get(CounterEventTypes.US_DECODE_FAIL).reset();
//        countersMap.get(CounterEventTypes.US_DECODE_SUCCESS).reset();
//        countersMap.get(CounterEventTypes.US_MESSAGE_PASS).reset();
//        countersMap.get(CounterEventTypes.US_RECEIVED_IN_OFJAVA).reset();
        LOGGER.debug("StaticsCounters has been reset");
    }
}
