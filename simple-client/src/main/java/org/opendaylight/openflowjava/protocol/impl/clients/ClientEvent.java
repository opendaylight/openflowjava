/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.clients;

/**
 * Uniting interface used for scenario support
 * @author michal.polkorab
 *
 */
public interface ClientEvent {

    /**
     * Common method for triggering events
     * @return true if event executed successfully
     */
    public boolean eventExecuted();
}
