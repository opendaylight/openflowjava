/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing sleep (wait) event
 * 
 * @author michal.polkorab
 */
public class SleepEvent implements ClientEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SleepEvent.class);
    private long sleepTime;

    /**
     * 
     * @param sleepTime time of {@link Thread#sleep(long)} in milliseconds
     */
    public SleepEvent(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public boolean eventExecuted() {
        try {
            Thread.sleep(sleepTime);
            LOGGER.debug("Sleeping");
            return true;
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
