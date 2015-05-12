/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.openflowjava.statistics.CounterEventTypes;
import org.opendaylight.openflowjava.statistics.StatisticsCounters;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutboundQueueEntry {
    private static final StatisticsCounters COUNTERS = StatisticsCounters.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueEntry.class);
    private static final ByteBufAllocator BUFFER_ALLOCATOR = new PooledByteBufAllocator(false);
    private final ByteBuf buffer = BUFFER_ALLOCATOR.buffer();
    private FutureCallback<OfHeader> callback;
    private Object pdu;
    private OfHeader message;
    private boolean completed;
    private volatile boolean committed;

    void commit(final SerializationFactory factory, final InetSocketAddress address, final OfHeader message, final FutureCallback<OfHeader> callback) {
        try {
            factory.messageToBuffer(message.getVersion(), buffer, message);
        } catch (Exception e) {
            LOG.info("Failed to serialize message {}", message, e);
            COUNTERS.incrementCounter(CounterEventTypes.DS_ENCODE_FAIL);
            callback.onFailure(e);
            buffer.clear();
            committed = true;
            return;
        }

        if (message instanceof FlowModInput){
            COUNTERS.incrementCounter(CounterEventTypes.DS_FLOW_MODS_SENT);
        }
        COUNTERS.incrementCounter(CounterEventTypes.DS_ENCODE_SUCCESS);

        final Object encoded;
        if (address == null) {
            encoded = buffer;
        } else {
            encoded = new DatagramPacket(buffer, address);
        }

        this.message = message;
        this.callback = callback;
        pdu = encoded;
        committed = true;
    }

    void reset() {
        buffer.clear();
        callback = null;
        completed = false;
        message = null;
        pdu = null;

        // volatile, needs to be last for safety reasons
        committed = false;
    }

    Object getPdu() {
        return pdu;
    }

    boolean isBarrier() {
        return message instanceof BarrierInput;
    }

    boolean isCommitted() {
        return committed;
    }

    boolean isCompleted() {
        return completed;
    }

    boolean complete(final OfHeader response) {
        Preconditions.checkState(!completed, "Attempted to complete a completed message %s with response %s", message, response);

        // Multipart requests are special, we have to look at them to see
        // if there is something outstanding and adjust ourselves accordingly
        final boolean reallyComplete;
        if (response instanceof MultipartReplyMessage) {
            reallyComplete = !((MultipartReplyMessage) response).getFlags().isOFPMPFREQMORE();
        } else {
            reallyComplete = true;
        }

        completed = reallyComplete;
        if (callback != null) {
            callback.onSuccess(response);
        }
        return reallyComplete;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("message", message).toString();
    }
}
