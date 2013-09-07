/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.connection;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolService;

/**
 * @author mirehak
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
}
