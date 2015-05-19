/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueCacheSlice {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueCacheSlice.class);
    private static final int TAKE_HEAD_MASK = ~257;

    private final ConcurrentLinkedDeque<Reference<OutboundQueueImpl>> cache = new ConcurrentLinkedDeque<>();
    private final AtomicInteger counter = new AtomicInteger();
    private final int queueSize;

    private int refCount = 1;

    OutboundQueueCacheSlice(final int queueSize) {
        Preconditions.checkArgument(queueSize >= 1);
        this.queueSize = queueSize;
    }

    boolean decRef() {
        return --refCount == 0;
    }

    void incRef() {
        refCount++;
    }

    int getQueueSize() {
        return queueSize;
    }

    OutboundQueueImpl getQueue(final OutboundQueueManager<?> manager, final long baseXid) {
        final boolean head = (counter.incrementAndGet() & TAKE_HEAD_MASK) == 0;

        final OutboundQueueImpl ret;
        OutboundQueueImpl cached = null;
        for (;;) {
            final Reference<OutboundQueueImpl> item = head ? cache.pollLast() : cache.pollLast();
            if (item == null) {
                break;
            }

            cached = item.get();
            if (cached != null) {
                ret = cached.reuse(manager, baseXid);
                LOG.trace("Reusing queue {} as {} on manager {}", cached, ret, manager);
                return ret;
            }
        }

        ret = new OutboundQueueImpl(manager, baseXid, queueSize + 1);
        LOG.trace("Allocated new queue {} on manager {}", ret, manager);
        return ret;
    }

    void putQueue(final OutboundQueueImpl queue) {
        if (cache.offer(new SoftReference<>(queue))) {
            LOG.trace("Saving queue {} for later reuse", queue);
        } else {
            LOG.trace("Queue {} thrown away", queue);
        }
    }
}
