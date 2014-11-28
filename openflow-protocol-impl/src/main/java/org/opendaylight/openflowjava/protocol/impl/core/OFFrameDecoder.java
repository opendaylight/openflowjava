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

import org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionFacade;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes incoming messages into message frames.
 * @author michal.polkorab
 */
public class OFFrameDecoder extends ByteToMessageDecoder {

    /** Length of OpenFlow 1.3 header */
    public static final byte LENGTH_OF_HEADER = 8;
    private static final byte LENGTH_INDEX_IN_HEADER = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(OFFrameDecoder.class);
    private ConnectionFacade connectionFacade;
    private boolean firstTlsPass = false;

    /**
     * Constructor of class.
     * @param connectionFacade  ConnectionFacade that will be notified
     * with ConnectionReadyNotification after TLS has been successfully set up.
     * @param tlsPresent true is TLS is required, false otherwise
     */
    public OFFrameDecoder(ConnectionFacade connectionFacade, boolean tlsPresent) {
        LOGGER.trace("Creating OFFrameDecoder");
        if (tlsPresent) {
            firstTlsPass = true;
        }
        this.connectionFacade = connectionFacade;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof io.netty.handler.ssl.NotSslRecordException) {
            LOGGER.warn("Not an TLS record exception - please verify TLS configuration.");
        } else {
            LOGGER.warn("Unexpected exception from downstream.", cause);
        }
        LOGGER.warn("Closing connection.");
        ctx.close();
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        if (firstTlsPass) {
            connectionFacade.fireConnectionReadyNotification();
            firstTlsPass = false;
        }
        int readableBytes = bb.readableBytes();
        if (readableBytes < LENGTH_OF_HEADER) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("skipping bytebuf - too few bytes for header: " + readableBytes + " < " + LENGTH_OF_HEADER );
                LOGGER.debug("bb: " + ByteBufUtils.byteBufToHexString(bb));
            }
            return;
        }

        int length = bb.getUnsignedShort(bb.readerIndex() + LENGTH_INDEX_IN_HEADER);
        LOGGER.debug("length of actual message: {}", length);

        if (readableBytes < length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("skipping bytebuf - too few bytes for msg: " +
                        readableBytes + " < " + length);
                LOGGER.debug("bytebuffer: " + ByteBufUtils.byteBufToHexString(bb));
            }
            return;
        }
        LOGGER.debug("OF Protocol message received, type:{}", bb.getByte(bb.readerIndex() + 1));

        ByteBuf messageBuffer = bb.slice(bb.readerIndex(), length);
        list.add(messageBuffer);
        messageBuffer.retain();
        bb.skipBytes(length);
    }

}
