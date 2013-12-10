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

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
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

    /**
     * Constructor of class.
     */
    public OFFrameDecoder() {
        LOGGER.debug("Creating OFFrameDecoder");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        int readableBytes = bb.readableBytes();
        if (readableBytes < LENGTH_OF_HEADER) {
            LOGGER.debug("skipping bb - too few data for header: " + readableBytes);
            return;
        }
        
        int length = bb.getUnsignedShort(bb.readerIndex() + LENGTH_INDEX_IN_HEADER);
        LOGGER.debug("length of actual message: {}", length);
        
        if (readableBytes < length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("skipping bb - too few data for msg: " +
                        readableBytes + " < " + length);
                LOGGER.debug("bb: " + ByteBufUtils.byteBufToHexString(bb));
                LOGGER.debug("readableBytes: " + readableBytes);
            }
            
            return;
        } else {
            LOGGER.debug("[enough bytes] readableBytes: " + readableBytes);
        }
        LOGGER.info("OF Protocol message received, type:{}", bb.getByte(bb.readerIndex() + 1));
        
        ByteBuf messageBuffer = bb.slice(bb.readerIndex(), length);
        list.add(messageBuffer);
        messageBuffer.retain();
        bb.skipBytes(length);
    }

}
