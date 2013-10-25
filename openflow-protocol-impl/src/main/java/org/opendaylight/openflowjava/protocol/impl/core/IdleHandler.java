/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SwitchIdleEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author michal.polkorab
 *
 */
public class IdleHandler extends IdleStateHandler{

    /**
     * 
     * @param readerIdleTime
     * @param writerIdleTime
     * @param allIdleTime
     * @param unit
     */
    public IdleHandler(long readerIdleTime, long writerIdleTime,
            long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(IdleHandler.class);

    
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt)
            throws Exception {
        if ((evt.state() == IdleState.READER_IDLE) && (evt.isFirst())) {
            LOGGER.info("Switch idle");
            SwitchIdleEventBuilder builder = new SwitchIdleEventBuilder();
            builder.setInfo("Switch idle");
            ctx.fireChannelRead(builder.build());
        }
    }

}
