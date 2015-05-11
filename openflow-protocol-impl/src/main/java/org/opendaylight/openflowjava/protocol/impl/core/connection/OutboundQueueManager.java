/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandler;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueueHandlerRegistration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yangtools.concepts.AbstractObjectRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueManager<T extends OutboundQueueHandler> extends AbstractObjectRegistration<T> implements OutboundQueueHandlerRegistration<T> {
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueManager.class);
    private static final int QUEUE_CACHE_SIZE = 4;

    private final Queue<OutboundQueueImpl> queueCache = new ArrayDeque<>(QUEUE_CACHE_SIZE);
    private final Queue<OutboundQueueImpl> activeQueues = new LinkedList<>();
    private final AtomicLong lastXid = new AtomicLong();
    private final int queueSize;

    private OutboundQueueImpl currentQueue;
    private long lastBarrierNanos = System.nanoTime();
    private int nonBarrierMessages;

    OutboundQueueManager(final T handler, final int queueSize) {
        super(handler);
        Preconditions.checkArgument(queueSize >= 0);
        this.queueSize = queueSize;

        createQueue();
    }

    @Override
    protected void removeRegistration() {
        getInstance().onConnectionQueueChanged(null);

        // FIXME: what else before a shutdown?
    }

    private void retireQueue(final OutboundQueueImpl queue) {
        if (queueCache.offer(queue)) {
            LOG.debug("Saving queue {} for later reuse", queue);
        } else {
            LOG.debug("Queue {} thrown away", queue);
        }
    }

    private void createQueue() {
        final long baseXid = lastXid.getAndAdd(queueSize);

        final OutboundQueueImpl cached = queueCache.poll();
        final OutboundQueueImpl queue;
        if (cached != null) {
            queue = cached.reuse(baseXid);
            LOG.debug("Reusing queue {} as {}", cached, queue);
        } else {
            queue = new OutboundQueueImpl(this, baseXid, queueSize);
            LOG.debug("Allocated new queue {}", queue);
        }

        activeQueues.add(queue);
        currentQueue = queue;
        getInstance().onConnectionQueueChanged(queue);
    }

    boolean flushEntry(final ChannelHandlerContext ctx) {
        if (currentQueue.isFlushed()) {
            LOG.debug("Queue {} is fully flushed", currentQueue);
            createQueue();
        }

        final OfHeader message = currentQueue.flushEntry();
        if (message == null) {
            return false;
        }

        if (message instanceof BarrierInput) {
            nonBarrierMessages = 0;
            lastBarrierNanos = System.nanoTime();
        } else {
            nonBarrierMessages++;
            if (nonBarrierMessages >= queueSize) {
                final Long xid = currentQueue.reserveEntry();
                if (xid != null) {
                    currentQueue.commitEntry(xid, getInstance().createBarrierRequest(xid), null);
                    LOG.debug("Scheduled barrier request after {} non-barrier messages", nonBarrierMessages);
                    nonBarrierMessages = 0;
                } else {
                    LOG.debug("Failed to schedule barrier request, will retry");
                }
            }
        }

        ctx.write(message);
        return true;
    }

    /**
     * Invoked whenever a message comes in from the switch. Runs matching
     * on all active queues in an attempt to complete a previous request.
     *
     * @param message Potential response message
     * @return True if the message matched a previous request, false otherwise.
     */
    boolean onMessage(final OfHeader message) {
        Iterator<OutboundQueueImpl> it = activeQueues.iterator();
        while (it.hasNext()) {
            final OutboundQueueImpl queue = it.next();
            final int offset = queue.completionOffset(message);

            if (offset >= 0) {
                LOG.debug("Queue {} accepted response {}", queue, message);

                // This has been a barrier request, we need to flush all
                // previous queues
                if (queue.isBarrier(offset) && activeQueues.size() > 1) {
                    LOG.debug("Queue {} indicated request was a barrier", queue);

                    it = activeQueues.iterator();
                    while (it.hasNext()) {
                        final OutboundQueueImpl q = it.next();
                        if (queue.equals(q)) {
                            // Ensures this iterator points to the same queue
                            break;
                        }

                        LOG.debug("Queue {} is implied finished", q);
                        q.completeAll();
                        it.remove();
                        retireQueue(q);
                    }
                }

                if (queue.isFinished()) {
                    LOG.debug("Queue {} is finished", queue);
                    it.remove();
                    retireQueue(queue);
                }

                return true;
            }
        }

        LOG.debug("Failed to find completion for message {}", message);
        return false;
    }

    void ensureFlushing(final OutboundQueueImpl queue) {
        Preconditions.checkState(currentQueue.equals(queue));

        // FIXME: invoke the flusher
    }
}
