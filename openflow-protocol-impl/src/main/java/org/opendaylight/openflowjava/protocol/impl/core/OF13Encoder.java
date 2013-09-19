/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author michal.polkorab
 *
 */
public class OF13Encoder extends MessageToByteEncoder<OfHeader> {

    @Override
    protected void encode(ChannelHandlerContext ctx, OfHeader msg, ByteBuf out)
            throws Exception {
        SerializationFactory.messageToBuffer(msg.getVersion(), out, msg);
        ctx.writeAndFlush(out);
        
    }

}
