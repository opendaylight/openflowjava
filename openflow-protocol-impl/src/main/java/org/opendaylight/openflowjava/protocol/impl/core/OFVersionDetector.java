/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.statistics.CounterEventTypes;
import org.opendaylight.openflowjava.statistics.StatisticsCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects version of used OpenFlow Protocol and discards unsupported version messages
 * @author michal.polkorab
 */
public class OFVersionDetector extends ByteToMessageDecoder {

    /** Version number of OpenFlow 1.0 protocol */
    private static final byte OF10_VERSION_ID = EncodeConstants.OF10_VERSION_ID;
    /** Version number of OpenFlow 1.3 protocol */
    private static final byte OF13_VERSION_ID = EncodeConstants.OF13_VERSION_ID;
    private static final short OF_PACKETIN = 10;
    private static final Logger LOG = LoggerFactory.getLogger(OFVersionDetector.class);
    private final StatisticsCounters statisticsCounters;
    private volatile boolean filterPacketIns;

    /**
     * Constructor of class.
     */
    public OFVersionDetector() {
        LOG.trace("Creating OFVersionDetector");
        statisticsCounters = StatisticsCounters.getInstance();
    }

    public void setFilterPacketIns(final boolean enabled) {
        filterPacketIns = enabled;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (!in.isReadable()) {
            LOG.debug("not enough data");
            in.release();
            return;
        }

        final byte version = in.readByte();
        if (version == OF13_VERSION_ID || version == OF10_VERSION_ID) {
            LOG.debug("detected version: {}", version);
            if (!filterPacketIns || OF_PACKETIN != in.getUnsignedByte(in.readerIndex())) {
                ByteBuf messageBuffer = in.slice();
                out.add(new VersionMessageWrapper(version, messageBuffer));
                messageBuffer.retain();
            } else {
                LOG.debug("dropped packetin");
                statisticsCounters.incrementCounter(CounterEventTypes.US_DROPPED_PACKET_IN);
            }
        } else {
            LOG.warn("detected version: {} - currently not supported", version);
        }
        in.skipBytes(in.readableBytes());
    }
}
