/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.openflow.clients;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/** InboundHanler for {@link SecureSimpleClient}
 * 
 * @author michal.polkorab
 */
public class SecureSimpleClientHandler extends ChannelInboundHandlerAdapter/*SimpleChannelInboundHandler<Object>*/ {

    private static final Logger logger = LoggerFactory.getLogger(SecureSimpleClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // TODO Auto-generated method stub
        logger.info(msg.toString());
    }
}