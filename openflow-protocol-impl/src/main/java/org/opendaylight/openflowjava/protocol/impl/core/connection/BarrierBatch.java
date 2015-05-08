/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BarrierBatch {
    private static final AtomicIntegerFieldUpdater<BarrierBatch> CURRENT_OFFSET_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(BarrierBatch.class, "currentOffset");
    private static final Logger LOG = LoggerFactory.getLogger(BarrierBatch.class);
    private final AtomicReferenceArray<AbstractRequest> requests;
    private final long firstXid;
    private volatile int currentOffset;
    private int flushOffset;

    private BarrierBatch(final long firstXid, final int size) {
        this.requests = new AtomicReferenceArray<>(size);
        this.firstXid = firstXid;
    }

    static BarrierBatch initial(final int maxBatchSize) {
        Preconditions.checkArgument(maxBatchSize >= 0, "Maximum batch size has to be a positive number");

        return new BarrierBatch(ThreadLocalRandom.current().nextLong(), maxBatchSize);
    }

    /**
     * Reserve an element in the batch.
     *
     * @return -1 if reservation would exceed capacity, otherwise offset of
     *         reserved element.
     */
    int reserveRequest() {
        final int offset = CURRENT_OFFSET_UPDATER.getAndIncrement(this);
        if (offset >= requests.length()) {
            LOG.debug("Overflown batch {} size {} offset {}", this, requests.length(), offset);
            return -1;
        }

        LOG.debug("Reserved offset {} out of {}", offset, requests.length());
        return offset;
    }

    private void checkOffset(final int offset) {
        Preconditions.checkArgument(offset >= 0, "Offset has not be non-negative");
        Preconditions.checkArgument(offset <= currentOffset, "Offset %s has not been reserved", offset);
    }

    /**
     * Commit an element to a particular offset
     */
    void commitRequest(final int offset, final AbstractRequest request) {
        checkOffset(offset);

        final boolean success = requests.compareAndSet(offset, null, request);
        Preconditions.checkArgument(success, "Request at offset %s has already been committed", offset);
    }

    long offsetToXid(final int offset) {
        checkOffset(offset);

        final long ret = firstXid + offset;
        LOG.debug("Offset {} translated to XID {}", offset, ret);
        return ret;
    }

    /**
     * Convert an XID to an an offset. This method does not throw exceptions, as the XIDs
     * may come from an untrusted source.
     *
     * @param xid XID which needs to be translated
     * @return Offset corresponding to specified XID, or -1 if the XID is not within this batch.
     */
    int xidToOffset(final long xid) {
        final long maybeOffset = xid - firstXid;
        if (maybeOffset < 0) {
            LOG.debug("XID {} is below base {}", xid, firstXid);
            return -1;
        }
        if (maybeOffset >= currentOffset) {
            LOG.debug("XID {} points to unallocated offset {}", xid, maybeOffset);
            return -1;
        }

        return (int)maybeOffset;
    }

    BarrierBatch createNextBatch() {
        // We shift the XID space by at least one to allow for a barrier request event when
        // we have a full batch.
        return new BarrierBatch(firstXid + requests.length() + ThreadLocalRandom.current().nextInt() + 1, requests.length());
    }

    private AbstractRequest barrierRequest() {
        // FIXME: Create a barrier request
        return null;
    }

    AbstractRequest nextRequest() {
        // The simple case: we have exhausted all possible messages. Now it's time
        // to issue the barrier request and mark the buffer as done
        if (flushOffset == requests.length()) {
            flushOffset++;
            return barrierRequest();
        }

        // Stop if we do not have an outstanding request
        if (isEmpty()) {
            return null;
        }

        flushOffset++;
        for (;;) {
            final AbstractRequest ret = requests.get(flushOffset);
            if (ret != null) {
                return ret;
            }

            LockSupport.parkNanos(1000L);
        }
    }

    boolean isComplete() {
        return flushOffset > requests.length();
    }

    boolean isEmpty() {
        return flushOffset >= currentOffset;
    }

    void onFailure(final Throwable cause) {
        for (int i = 0; i < flushOffset; ++i) {
            final AbstractRequest req = requests.getAndSet(i, null);
            if (req != null) {
                req.onFailure(cause);
            }
        }
    }

    void onSuccess() {
        for (int i = 0; i < flushOffset; ++i) {
            final AbstractRequest req = requests.getAndSet(i, null);
            if (req != null) {
                req.onSuccess();
            }
        }
    }
}
