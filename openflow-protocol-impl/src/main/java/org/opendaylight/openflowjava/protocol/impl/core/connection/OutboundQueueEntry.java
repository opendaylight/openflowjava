/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueEntry {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueEntry.class);
    private FutureCallback<OfHeader> callback;
    private OfHeader message;
    private boolean completed;
    private boolean barrier;
    private volatile boolean committed;

    void commit(final OfHeader message, final FutureCallback<OfHeader> callback) {
        this.message = message;
        this.callback = callback;
        this.barrier = message instanceof BarrierInput;

        // Volatile write, needs to be last
        committed = true;
    }

    void reset() {
        barrier = false;
        callback = null;
        completed = false;
        message = null;

        // Volatile write, needs to be last
        committed = false;
    }

    boolean isBarrier() {
        return barrier;
    }

    boolean isCommitted() {
        return committed;
    }

    boolean isCompleted() {
        return completed;
    }

    OfHeader takeMessage() {
        final OfHeader ret = message;
        if (!barrier) {
            checkCompletionNeed();
        }
        message = null;
        return ret;
    }

    private void checkCompletionNeed() {
        if (callback == null || (message instanceof PacketOutInput)) {
            completed = true;
            if (callback != null) {
                callback.onSuccess(null);
                callback = null;
            }
        }
    }

    boolean complete(final OfHeader response) {
        Preconditions.checkState(!completed, "Attempted to complete a completed message with response %s", response);

        // Multipart requests are special, we have to look at them to see
        // if there is something outstanding and adjust ourselves accordingly
        final boolean reallyComplete;
        if (response instanceof MultipartReplyMessage) {
            reallyComplete = !((MultipartReplyMessage) response).getFlags().isOFPMPFREQMORE();
            LOG.debug("Multipart reply {}", response);
        } else {
            reallyComplete = true;
        }

        completed = reallyComplete;
        if (callback != null) {
            callback.onSuccess(response);
            if (reallyComplete) {
                // We will not need the callback anymore, make sure it can be GC'd
                callback = null;
            }
        }
        LOG.debug("Entry {} completed {} with response {}", this, completed, response);
        return reallyComplete;
    }

    void fail(final OutboundQueueException cause) {
        if (!completed) {
            completed = true;
            if (callback != null) {
                callback.onFailure(cause);
                callback = null;
            }
        } else {
            LOG.warn("Ignoring failure {} for completed message", cause);
        }
    }
}
