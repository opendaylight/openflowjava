/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.api.connection;

/**
 * @author mirehak
 *
 */
public interface ConnectionReadyListener {

    /**
     * fired when connection becomes ready-to-use
     */
    public void onConnectionReady();
}
