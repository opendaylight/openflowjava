/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.core;

import io.netty.buffer.ByteBuf;

/**
 * @author michal.polkorab
 *
 */
public class VersionMessageWrapper {

    private short version;
    private ByteBuf messageBuffer;
    
    /**
     * Constructor
     * @param version version decoded in {@link OFVersionDetector}
     * @param messageBuffer message received from {@link OFFrameDecoder}
     */
    public VersionMessageWrapper(short version, ByteBuf messageBuffer) {
        this.version = version;
        this.messageBuffer = messageBuffer;
    }

    /**
     * @return the version version decoded in {@link OFVersionDetector}
     */
    public short getVersion() {
        return version;
    }

    /**
     * @return the messageBuffer message received from {@link OFFrameDecoder}
     */
    public ByteBuf getMessageBuffer() {
        return messageBuffer;
    }
    
    
}
