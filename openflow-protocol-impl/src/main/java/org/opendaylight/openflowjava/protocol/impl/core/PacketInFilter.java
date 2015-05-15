/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link MessageToMessageDecoder} which looks at a {@link VersionMessageWrapper} message
 * and checks if it contains a PACKETIN message. Such messages are thrown away. This handler
 * is stateless and thus can be shared across multiple connections.
 */
@Sharable
public final class PacketInFilter extends MessageToMessageDecoder<VersionMessageWrapper> {
    private static final Logger LOG = LoggerFactory.getLogger(PacketInFilter.class);
    private static final short PACKETIN_TYPE = 10;
    private static final PacketInFilter INSTANCE = new PacketInFilter();

    private PacketInFilter() {
        // Hidden to prevent instantiation
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return Singleton instance.
     */
    public static PacketInFilter getInstance() {
        return INSTANCE;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final VersionMessageWrapper msg, final List<Object> out) {
        switch (msg.getVersion()) {
        case EncodeConstants.OF10_VERSION_ID:
        case EncodeConstants.OF13_VERSION_ID:
            final ByteBuf buf = msg.getMessageBuffer();
            if (PACKETIN_TYPE == buf.getUnsignedByte(0)) {
                LOG.trace("Filtered PacketIn message {}", msg);
                buf.release();
                return;
            }
        }

        out.add(msg);
    }
}
