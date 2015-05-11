/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.connection;

import com.google.common.annotations.Beta;
import java.net.InetSocketAddress;
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
    Future<Boolean> disconnect();

    /**
     * @return true, if connection to switch is alive
     */
    boolean isAlive();

    /**
     * @return address of the remote end - address of a switch if connected
     */
    InetSocketAddress getRemoteAddress();
    /**
     * @param messageListener here will be pushed all messages from switch
     */
    void setMessageListener(OpenflowProtocolListener messageListener);

    /**
     * @param systemListener here will be pushed all system messages from library
     */
    void setSystemListener(SystemNotificationsListener systemListener);

    /**
     * Throws exception if any of required listeners is missing
     */
    void checkListeners();

    /**
     * notify listener about connection ready-to-use event
     */
    void fireConnectionReadyNotification();

    /**
     * set listener for connection became ready-to-use event
     * @param connectionReadyListener
     */
    void setConnectionReadyListener(ConnectionReadyListener connectionReadyListener);

    /**
     * sets option for automatic channel reading;
     * if set to false, incoming messages won't be read
     */
    void setAutoRead(boolean autoRead);

    /**
     * @return true, if channel is configured to autoread
     */
    boolean isAutoRead();

    /**
     * Registers a new bypass outbound queue
     * @param handler
     * @param maxQueueDepth
     * @param maxBarrierNanos
     * @return An {@link OutboundQueueHandlerRegistration}
     */
    @Beta
    <T extends OutboundQueueHandler> OutboundQueueHandlerRegistration<T> registerOutboundQueueHandler(OutboundQueueHandler handler,
        int maxQueueDepth, long maxBarrierNanos);
}
