package org.opendaylight.openflowjava.protocol.impl.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepEvent implements ClientEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SleepEvent.class);
    private long sleepTime;
    
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
