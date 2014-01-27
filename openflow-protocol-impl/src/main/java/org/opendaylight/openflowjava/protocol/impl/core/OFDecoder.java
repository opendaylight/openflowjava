/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms OpenFlow Protocol messages to POJOs
 * @author michal.polkorab
 */
public class OFDecoder extends MessageToMessageDecoder<VersionMessageWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OFDecoder.class);

    /**
     * Constructor of class
     */
    public OFDecoder() {
        LOGGER.trace("Creating OF 1.3 Decoder");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, VersionMessageWrapper msg,
            List<Object> out) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("VersionMessageWrapper received");
            LOGGER.debug("<< " + ByteBufUtils.byteBufToHexString(msg.getMessageBuffer()));
        }
        DataObject dataObject = null;
        try {
            dataObject = DeserializationFactory.bufferToMessage(msg.getMessageBuffer(),
                    msg.getVersion());
        } catch(Exception e) {
            LOGGER.error("Message deserialization failed");
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (dataObject == null) {
            LOGGER.warn("Translated POJO is null");
            return;
        }
        msg.getMessageBuffer().discardReadBytes();
        out.add(dataObject);
    }
}
