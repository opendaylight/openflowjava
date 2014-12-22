/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.core;

import src.main.java.org.opendaylight.openflowjava.protocol.impl.core.connection.ConnectionFacade;

/**
 * Detects version of used OpenFlow Protocol and discards unsupported version messages
 * @author michal.polkorab
 */
public class OFVersionDetector extends ByteToMessageDecoder {

    /** Version number of OpenFlow 1.0 protocol */
    private static final byte OF10_VERSION_ID = EncodeConstants.OF10_VERSION_ID;
    /** Version number of OpenFlow 1.3 protocol */
    private static final byte OF13_VERSION_ID = EncodeConstants.OF13_VERSION_ID;
    private static final Logger LOGGER = LoggerFactory.getLogger(OFVersionDetector.class);

    private ConnectionFacade connectionFacade;
    private boolean firstTlsPass = false;

    /**
     * Constructor of class.
     */
    public OFVersionDetector(ConnectionFacade connectionFacade, boolean tlsPresent) {
        this.connectionFacade = connectionFacade;
        if (tlsPresent) {
            firstTlsPass = true;
        }
        LOGGER.trace("Creating OFVersionDetector");
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf bb, List<Object> list) throws Exception {
    /* Moved upstream, used to be in OFFrameDecoder */
        if (firstTlsPass) {
            connectionFacade.fireConnectionReadyNotification();
            firstTlsPass = false;
        }
        if (bb.readableBytes() == 0) {
            /* Belt and braces - should not occur when using LengthFieldBasedDecoder */
            LOGGER.debug("not enough data");
            bb.release();
            return;
        }
        byte version = bb.readByte();
        if ((version == OF13_VERSION_ID) || (version == OF10_VERSION_ID)) {
            LOGGER.debug("detected version: " + version);
            ByteBuf messageBuffer = bb.slice();
            list.add(new VersionMessageWrapper(version, messageBuffer));
            messageBuffer.retain();
        } else {
            LOGGER.warn("detected version: " + version + " - currently not supported");
        }
        bb.skipBytes(bb.readableBytes());
    }

}
