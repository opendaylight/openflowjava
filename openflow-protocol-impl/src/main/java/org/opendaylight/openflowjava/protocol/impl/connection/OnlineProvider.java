/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.connection;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author mirehak
 */
public interface OnlineProvider {

    /**
     * @return the isOnlineFuture
     */
    public ListenableFuture<Boolean> getIsOnlineFuture();

}
