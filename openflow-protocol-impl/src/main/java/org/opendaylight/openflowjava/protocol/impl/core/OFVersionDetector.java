/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that detects version of used OpenFlow Protocol and engages right OFCodec into
 * pipeline.
 *
 * @author michal.polkorab
 */
public class OFVersionDetector extends ByteToMessageDecoder {

    /** Version number of OpenFlow 1.3 protocol */
    public static final byte OF13_VERSION_ID = 0x04;
    private static final Logger LOGGER = LoggerFactory.getLogger(OFVersionDetector.class);

    /**
     * Constructor of class.
     */
    public OFVersionDetector() {
        LOGGER.info("Creating OFVersionDetector");
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        LOGGER.info("Decoding frame");

        if (bb.readableBytes() == 0) {
            LOGGER.info("not enough data");
            bb.release();
            return;
        }
        LOGGER.debug("RI: " + bb.readerIndex());
        byte version = bb.readByte();

        if (version == OF13_VERSION_ID) {
            LOGGER.debug("detected version: " + version);
        } else {
            LOGGER.warn("detected version: " + version + " - currently not supported");
            return;
        }

        ByteBuf messageBuffer = bb.slice();
        list.add(new VersionMessageWrapper(version, messageBuffer));
        messageBuffer.retain();
        bb.skipBytes(bb.readableBytes());
    }

}
