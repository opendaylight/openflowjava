/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core.connection;

import java.util.HashMap;
import java.util.Map;

final class OutboundQueueCache {
    private static final OutboundQueueCache INSTANCE = new OutboundQueueCache();

    private final Map<Integer, OutboundQueueCacheSlice> slices = new HashMap<>();

    private OutboundQueueCache() {

    }

    static OutboundQueueCache getInstance() {
        return INSTANCE;
    }

    synchronized OutboundQueueCacheSlice getSlice(final int queueSize) {
        final OutboundQueueCacheSlice oldSlice = slices.get(queueSize);
        if (oldSlice != null) {
            oldSlice.incRef();
            return oldSlice;
        }

        final OutboundQueueCacheSlice newSlice = new OutboundQueueCacheSlice(queueSize);
        slices.put(queueSize, newSlice);
        return newSlice;
    }

    synchronized void putSlice(final OutboundQueueCacheSlice slice) {
        if (slice.decRef()) {
            slices.remove(slice.getQueueSize());
        }
    }
}
