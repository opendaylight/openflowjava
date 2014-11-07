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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class to hold and process counters
 * @author madamjak
 *
 */
public final class StatisticsCounters {

    /**
     * Default delay between two writings into log (milliseconds)
     */
    public static final int DEFAULT_LOG_REPORT_PERIOD = 10000;
    /**
     * Minimal delay between two writings into log (milliseconds)
     */
    public static final int MINIMAL_LOG_REPORT_PERIOD = 500;
    private static StatisticsCounters instanceHolder;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounters.class);

    private Timer logReporter;
    private int logReportPeriod;
    private boolean runLogReport;
    private Map<CounterEventTypes, Counter> countersMap;
    private boolean runCounting;
    // array to hold enabled counter types
    private CounterEventTypes[] enabledCounters = {
                    CounterEventTypes.DS_ENCODE_FAIL, 
                    CounterEventTypes.DS_ENCODE_SUCCESS,
                    CounterEventTypes.DS_ENTERED_OFJAVA,
                    CounterEventTypes.DS_FLOW_MODS_ENTERED,
                    CounterEventTypes.DS_FLOW_MODS_SENT,
                    CounterEventTypes.US_DECODE_FAIL, 
                    CounterEventTypes.US_DECODE_SUCCESS, 
                    CounterEventTypes.US_MESSAGE_PASS, 
                    CounterEventTypes.US_RECEIVED_IN_OFJAVA};

    /**
     * Get instance of statistics counters, first created object does not start counting and log reporting
     * @return an instance
     */
    public synchronized static StatisticsCounters getInstance() {
        if (instanceHolder == null) {
            instanceHolder = new StatisticsCounters();
        }
        return instanceHolder;
    }

    private StatisticsCounters() {
        countersMap = new ConcurrentHashMap<>();
        for(CounterEventTypes cet : enabledCounters){
            countersMap.put(cet, new Counter());
        }
        runCounting = false;
        this.logReportPeriod = -1;
        this.runLogReport = false;
        LOGGER.debug("StaticsCounters has been created");
    }

    /**
     * Start counting
     * @param resetCounters - true = statistics counters will be reset before start counting
     * @param reportToLogs - true = statistics counters will periodically write to log
     * @param logReportDelay - delay between two writings into logs in milliseconds (for details see startLogReport(int logReportDelay))
     */
    public void startCounting(boolean resetCounters, boolean reportToLogs, int logReportDelay){
        if (runCounting) {
            return;
        }
        LOGGER.debug("Start counting...");
        if(reportToLogs){
            startLogReport(logReportDelay);
        }
        if(resetCounters){
            resetCounters();
        }
        runCounting = true;
    }

    /**
     * Start counting (counters are set to 0 before start counting)
     * @param reportToLogs - true = statistics counters will periodically write to log
     * @param logReportDelay - delay between two writings into logs in milliseconds (for details see startLogReport(int logReportDelay))
     */
    public void startCounting(boolean reportToLogs, int logReportDelay){
        if (runCounting) {
            return;
        }
        startCounting(true,reportToLogs,logReportDelay);
    }

    /**
     * Stop counting, values in counters are untouched, log reporter is stopped
     */
    public void stopCounting(){
        runCounting = false;
        LOGGER.debug("Stop counting...");
        stopLogReport();
    }

    /**
     * Give an information if counting is running
     * @return true, if counting is running, otherwise false
     */
    public boolean isRunCounting(){
        return runCounting;
    }

    /**
     * Start write statistics into logs, if writing is run calling has no effect. 
     * If method is called without previous setting of report delay than DEFAULT_LOG_REPORT_PERIOD will be used.
     */
    public void startLogReport(){
        if(runLogReport){
            return;
        }
        if(this.logReportPeriod <= 0){
            this.logReportPeriod = DEFAULT_LOG_REPORT_PERIOD;
        }
        if(this.logReportPeriod <= MINIMAL_LOG_REPORT_PERIOD){
            this.logReportPeriod = MINIMAL_LOG_REPORT_PERIOD;
        }
        logReporter = new Timer("SC_Timer");
        logReporter.schedule(new LogReporterTask(this), this.logReportPeriod,this.logReportPeriod);
        runLogReport = true;
        LOGGER.debug("Statistics log reporter has been scheduled with period {} ms", this.logReportPeriod);
    }

    /**
     * Start write statistics into logs with given delay between writings, if writing is run calling has no effect.
     * @param logReportDelay - delay between two writings into logs (milliseconds). 
     *            It is mandatory if reportToLogs is true, value have to be greater than 0 (zero)
     *            If value is smaller than MINIMAL_LOG_REPORT_PERIOD, the value MINIMAL_LOG_REPORT_PERIOD will be used.
     * @exception IllegalArgumentException if logReportDelay is not greater than 0 (zero)
     */
    public void startLogReport(int logReportDelay){
        if(runLogReport){
            return;
        }
        if(logReportDelay <= 0){
            throw new IllegalArgumentException("logReportPeriod have to bee greater than 0 zero");
        }
        if(logReportDelay < MINIMAL_LOG_REPORT_PERIOD){
            this.logReportPeriod = MINIMAL_LOG_REPORT_PERIOD;
        } else {
            this.logReportPeriod = logReportDelay;
        }
        startLogReport();
    }

    /**
     * Stop  write statistics into logs, counting does not stop
     */
    public void stopLogReport(){
        if(runLogReport){
            if(logReporter != null){
                logReporter.cancel();
                LOGGER.debug("Statistics log reporter has been canceled");
            }
            runLogReport = false;
        }
    }

    /**
     * Give an information if log reporter is running (statistics are write into logs).
     * @return true if log reporter writes statistics into log, otherwise false
     */
    public boolean isRunLogReport(){
        return runLogReport;
    }

    /**
     * @return the current delay between two writings into logs
     */
    public int getLogReportPeriod() {
        return logReportPeriod;
    }

    /**
     * @return the enabled counters
     */
    protected CounterEventTypes[] getEnabledCounters() {
        return enabledCounters;
    }
    /**
     * @return the countersMap
     */
    protected Map<CounterEventTypes, Counter> getCountersMap() {
        return countersMap;
    }

    /**
     * Give an information if is given counter is enabled
     * @param counterEventKey
     * @return true if counter has been Enabled, otherwise false
     */
    public boolean isCounterEnabled(CounterEventTypes counterEventKey){
        if (counterEventKey == null) {
            return false;
        }
        return countersMap.containsKey(counterEventKey);
    }

    /**
     * Get counter by counter event type
     * @param counterEventKey key to identify counter (can not be null)
     * @return - Counter object or null if counter has not been enabled
     * @exception - IllegalArgumentException if counterEventKey is null
     */
    public Counter getCounter(CounterEventTypes counterEventKey) {
        if (counterEventKey == null) {
            throw new IllegalArgumentException("counterEventKey can not be null");
        }
        return countersMap.get(counterEventKey);
    }

    /**
     * Increment value of given counter
     * @param counterEventKey key to identify counter
     */
    public void incrementCounter(CounterEventTypes counterEventKey) {
        if(runCounting){
            if (isCounterEnabled(counterEventKey)){
                countersMap.get(counterEventKey).incrementCounter();
            }
        }
    }

    /**
     * Set values of all counter to 0 (zero)
     */
    public void resetCounters() {
        for(CounterEventTypes cet : enabledCounters){
            countersMap.get(cet).reset();
        }
        LOGGER.debug("StaticsCounters has been reset");
    }

    /**
     * internal class to process logReporter
     * @author madamjak
     *
     */
    private static class LogReporterTask extends TimerTask {
        private static final Logger LOG = LoggerFactory.getLogger(LogReporterTask.class);

        private StatisticsCounters sc;
        public LogReporterTask(StatisticsCounters sc) {
            this.sc = sc;
        }

        @Override
        public void run() {
                for(CounterEventTypes cet : sc.getEnabledCounters()){
                    LOG.debug(cet.toString() + ": " + sc.getCountersMap().get(cet).toString());
                }
        }
    }
}
