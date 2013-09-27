/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.api.connection;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;

/**
 * @author mirehak
 * @author michal.polkorab
 */
public interface ConnectionAdapter extends OpenflowProtocolService {

    /**
     * disconnect corresponding switch
     * @return future set to true, when disconnect completed
     */
    public Future<Boolean> disconnect();
    
    /**
     * @return true, if connection to switch is alive
     */
    public boolean isAlive();
    
    /**
     * @param messageListener here will be pushed all messages from switch
     */
    public void setMessageListener(OpenflowProtocolListener messageListener);
    
    /**
     * @param systemListener here will be pushed all system messages from library
     */
    public void setSystemListener(SystemNotificationsListener systemListener);

    /**
     * Throws exception if any of required listeners is missing
     */
    public void checkListeners();

}
