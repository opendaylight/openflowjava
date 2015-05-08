/*
 * Copyright (c) 2015 Robert Varga and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BarrierBatch {
    private static final AtomicIntegerFieldUpdater<BarrierBatch> CURRENT_OFFSET_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(BarrierBatch.class, "currentOffset");
    private static final Logger LOG = LoggerFactory.getLogger(BarrierBatch.class);
    private static final long COMMIT_PARK_NANOS = 1L;

    private final ConcurrentMap<Long, AbstractRequest> xidToRequest = new ConcurrentHashMap<>();
    private final AtomicReferenceArray<AbstractRequest> requests;
    private volatile int currentOffset;
    private int flushOffset;

    private BarrierBatch(final int maxBatchSize) {
        Preconditions.checkArgument(maxBatchSize >= 0, "Maximum batch size has to be a positive number");
        requests = new AtomicReferenceArray<>(maxBatchSize);
    }

    static BarrierBatch initial(final int maxBatchSize) {
        return new BarrierBatch(maxBatchSize);
    }

    BarrierBatch createNextBatch() {
        return new BarrierBatch(requests.length());
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

        final AbstractRequest previous = xidToRequest.putIfAbsent(request.getXid(), request);
        if (previous != null) {
            LOG.warn("XID {} is already allocated to request {}", request.getXid(), previous);
        }

        final boolean success = requests.compareAndSet(offset, null, request);
        if (!success) {
            xidToRequest.remove(request.getXid(), request);
            throw new IllegalArgumentException(String.format("Request at offset %s has already been committed as %s", offset, requests.get(offset)));
        }

        LOG.debug("Offset {} committed as XID {} request {}", offset, request.getXid(), request);
    }

    boolean isComplete() {
        return flushOffset >= requests.length();
    }

    boolean writeRequest(final Channel channel, final InetSocketAddress address) {
        Preconditions.checkState(!isComplete());

        // The simple case: we have exhausted all possible messages. Now it's time
        // to issue the barrier request and mark the buffer as done
        boolean retry = true;
        while (retry) {
            final AbstractRequest req = requests.get(flushOffset);
            if (req == null) {
                retry = false;
                LockSupport.parkNanos(COMMIT_PARK_NANOS );
                continue;
            }

            flushOffset++;

            final ChannelFuture p;
            if (address == null) {
                p = channel.write(req.getMessage());
            } else {
                p = channel.write(new DatagramPacket(req.getMessage(), address));
            }
            p.addListener(req);
            return true;
        }

        return false;
    }

    boolean isEmpty() {
        return flushOffset >= currentOffset;
    }

    void onFailure(final Throwable cause) {
        for (final AbstractRequest req : xidToRequest.values()) {
            req.onFailure(cause);
        }
    }

    void onSuccess() {
        for (final AbstractRequest req : xidToRequest.values()) {
            req.onSuccess();
        }
    }

}
