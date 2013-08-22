/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openflow.example.TCPHandler.COMPONENT_NAMES;

/** Class that detects version of used OpenFlow Protocol and engages right OFCodec into 
 *  pipeline
 *
 * @author michal.polkorab
 */
public class OFVersionDetector extends ReplayingDecoder<Void> {

    private static final Logger logger = LoggerFactory.getLogger(OFVersionDetector.class);
    
    /**
     *  Constructor of class
     */
    public OFVersionDetector() {
        logger.info("OFVD - Creating OFVersionDetector");
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        logger.info("OFVD - Decoding frame");

        logger.debug("OFVD: RI: " + bb.readerIndex());
        byte version = bb.readByte();
        short length = bb.getShort(2);
        
        logger.debug("OFVD - Length is: " + length);

        if (version == 4) {
            logger.debug("OFVD - detected version: " + version);
            if (chc.pipeline().get(COMPONENT_NAMES.OF_CODEC.name()) == null) {
                logger.info("OFVD - Engaging OF13Codec");
                chc.pipeline().addLast(COMPONENT_NAMES.OF_CODEC.name(), new OF13Codec());
            } else {
                logger.debug("OFVD - OF13Codec already in pipeline");
            }
        } else {
            logger.warn("OFVD - detected version: " + version + " - currently not supported");
        }

        list.add(bb.readBytes(length-1));

    }
    
}
