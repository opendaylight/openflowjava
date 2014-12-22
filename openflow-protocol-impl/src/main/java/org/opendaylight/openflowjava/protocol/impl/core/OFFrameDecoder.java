/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.core;


/**
 * Decodes incoming messages into message frames.
 * @author michal.polkorab
 */
public class OFFrameDecoder extends LengthFieldBasedFrameDecoder {

    /** Length of OpenFlow 1.3 header */
    public static final int LENGTH_OF_HEADER = 8;
    private static final int LENGTH_INDEX_IN_HEADER = 2;
    private static final int LENGTH_SIZE_IN_HEADER = 2;
    private static final int MAX_LENGTH = 65535;
    private static final Logger LOGGER = LoggerFactory.getLogger(OFFrameDecoder.class);

    /**
     * Constructor of class.
     * @param connectionFacade  ConnectionFacade that will be notified
     * with ConnectionReadyNotification after TLS has been successfully set up.
     * @param tlsPresent true is TLS is required, false otherwise
     */
    public OFFrameDecoder() {
        super(
                MAX_LENGTH,
                LENGTH_INDEX_IN_HEADER,
                LENGTH_SIZE_IN_HEADER,
                - (LENGTH_INDEX_IN_HEADER + LENGTH_SIZE_IN_HEADER),
                0);
        LOGGER.trace("Creating OFFrameDecoder");
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
}
