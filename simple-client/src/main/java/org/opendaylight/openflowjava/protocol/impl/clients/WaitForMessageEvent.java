/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

import java.util.Arrays;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing waiting on message
 * @author michal.polkorab
 */
public class WaitForMessageEvent implements ClientEvent {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForMessageEvent.class);
    private byte[] headerExpected;
    private byte[] headerReceived;

    /**
     * @param headerExpected header (first 8 bytes) of expected message
     */
    public WaitForMessageEvent(byte[] headerExpected) {
        this.headerExpected = headerExpected;
    }

    @Override
    public boolean eventExecuted() {
        if (headerReceived == null) {
            return false;
        }
        if (!Arrays.equals(headerExpected, headerReceived)) {
            LOGGER.debug("expected msg: " + ByteBufUtils.bytesToHexString(headerExpected));
            LOGGER.debug("received msg: " + ByteBufUtils.bytesToHexString(headerReceived));
            return false;
        }
        LOGGER.info("Headers OK");
        return true;
    }

    /**
     * @param headerReceived header (first 8 bytes) of expected message
     */
    public void setHeaderReceived(byte[] headerReceived) {
        this.headerReceived = headerReceived;
    }

    
}
