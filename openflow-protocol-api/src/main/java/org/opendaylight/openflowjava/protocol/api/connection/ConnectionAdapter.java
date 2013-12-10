/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


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

    /**
     * notify listener about connection ready-to-use event
     */
    public void fireConnectionReadyNotification();

    /**
     * set listener for connection became ready-to-use event  
     * @param connectionReadyListener
     */
    public void setConnectionReadyListener(ConnectionReadyListener connectionReadyListener);

}
