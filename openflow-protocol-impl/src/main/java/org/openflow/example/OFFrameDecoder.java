/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michal.polkorab
 */
public class OFFrameDecoder extends ByteToMessageDecoder {
    
    private final int MESSAGE_TYPES = 29;
    private final byte LATEST_WIRE_PROTOCOL = 4;
    private static final Logger logger = LoggerFactory.getLogger(OFFrameDecoder.class);
    
    public OFFrameDecoder(){
        logger.info("OFFD - creating OFFrameDecoder");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("OFFD - Unexpected exception from downstream.", cause);
        ctx.close();
    }
    
    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
        if (bb.readableBytes() < 8){
            return;
        }
        bb.markReaderIndex();
        byte version = bb.readByte();
        byte type = bb.readByte();
        short length = bb.readShort();
        int xid = bb.readInt();

        if ((type > MESSAGE_TYPES) || (version > LATEST_WIRE_PROTOCOL) || (length < 8)){
            bb.discardReadBytes();
            logger.info("OFFD - Non-OF Protocol message received (discarding)");
            return;
        } else {
            logger.info("OFFD - OF Protocol message received");
        }
        
        logger.info("OFFD - Wire protocol version: " + version);
        
        if (version == 4){
            if (chc.pipeline().get("ofversiondetector") == null){
                logger.info("OFFD - adding OFVD (for version " + version + " wire protocol)");
                chc.pipeline().addLast("ofversiondetector", new OFVersionDetector());
            } else {
                logger.info("OFFD - OFVD already in pipeline");
            }
        } else {
            logger.warn("OFFD - Received version is not supported");
        }
        
        logger.debug("OFFD: Version: " + version + " type: " + type + " length: " + length + " xid: " + xid);
        bb.resetReaderIndex();
        
        List<String> zoznam = chc.pipeline().names();
        logger.debug("OFFD: " + zoznam.toString());
        list.add(bb.readBytes(length));
    }
    
}
