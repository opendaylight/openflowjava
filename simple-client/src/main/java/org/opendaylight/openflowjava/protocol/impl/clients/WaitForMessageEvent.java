package org.opendaylight.openflowjava.protocol.impl.clients;

import java.util.Arrays;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WaitForMessageEvent implements ClientEvent {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForMessageEvent.class);
    private byte[] headerExpected;
    private byte[] headerReceived;

    public WaitForMessageEvent(byte[] headerAwaited) {
        this.headerExpected = headerAwaited;
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
        LOGGER.info("Waitformessageevent - headers are same");
        return true;
    }

    public void setHeaderReceived(byte[] headerReceived) {
        this.headerReceived = headerReceived;
    }

    
}
