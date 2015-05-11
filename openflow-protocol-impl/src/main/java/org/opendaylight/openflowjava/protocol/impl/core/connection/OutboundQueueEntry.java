/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.util.concurrent.FutureCallback;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueEntry {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueEntry.class);
    private FutureCallback<OfHeader> callback;
    private OfHeader message;
    private boolean completed;
    private volatile boolean committed;

    void commit(final OfHeader message, final FutureCallback<OfHeader> callback) {
        this.message = message;
        this.callback = callback;
        committed = true;
    }

    void reset() {
        callback = null;
        message = null;
        committed = false;
        completed = false;
    }

    boolean isCommitted() {
        return committed;
    }

    OfHeader getMessage() {
        return message;
    }

    boolean complete(final OfHeader response) {
        if (completed) {
            LOG.debug("Ignoring response {} for completed message {}", response, message);
            return false;
        }

        completed = true;
        if (callback != null) {
            callback.onSuccess(response);
        }
        return true;
    }

    void fail(final Throwable cause) {
        if (!completed) {
            completed = true;
            if (callback != null) {
                callback.onFailure(cause);
            }
        } else {
            LOG.warn("Ignoring failure {} for completed message {}", cause, message);
        }
    }
}
