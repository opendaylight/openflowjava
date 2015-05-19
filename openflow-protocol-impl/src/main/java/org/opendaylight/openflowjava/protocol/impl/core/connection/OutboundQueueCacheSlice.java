/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableSoftReference;
import com.google.common.base.Preconditions;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueCacheSlice {
    private static final class QueueRef extends FinalizableSoftReference<OutboundQueueImpl> {
        private final Collection<?> cache;

        protected QueueRef(final FinalizableReferenceQueue queue, final Collection<?> cache, final OutboundQueueImpl referent) {
            super(referent, queue);
            this.cache = Preconditions.checkNotNull(cache);
        }

        @Override
        public void finalizeReferent() {
            cache.remove(this);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueCacheSlice.class);
    private final FinalizableReferenceQueue refQueue = new FinalizableReferenceQueue();

    private final ConcurrentLinkedDeque<QueueRef> cache = new ConcurrentLinkedDeque<>();
    private final int queueSize;
    private int refCount = 1;

    OutboundQueueCacheSlice(final int queueSize) {
        Preconditions.checkArgument(queueSize >= 1);
        this.queueSize = queueSize;
    }

    void remove(final QueueRef queueRef) {
        cache.remove(queueRef);
    }

    boolean decRef() {
        if (--refCount == 0) {
            refQueue.close();
            return true;
        } else {
            return false;
        }
    }

    void incRef() {
        refCount++;
    }

    int getQueueSize() {
        return queueSize;
    }

    OutboundQueueImpl getQueue(final OutboundQueueManager<?> manager, final long baseXid) {
        final OutboundQueueImpl ret;
        OutboundQueueImpl cached = null;
        for (;;) {
            final Reference<OutboundQueueImpl> item = cache.pollLast();
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
        if (cache.offer(new QueueRef(refQueue, cache, queue))) {
            LOG.trace("Saving queue {} for later reuse", queue);
        } else {
            LOG.trace("Queue {} thrown away", queue);
        }
    }
}
