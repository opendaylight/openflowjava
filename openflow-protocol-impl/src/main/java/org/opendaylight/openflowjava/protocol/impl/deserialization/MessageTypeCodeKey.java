/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization;


/**
 * Class used as a key in {@link DecoderTable}
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class MessageTypeCodeKey {

    private final short msgType;
    private final short msgVersion;

    /**
     * @param msgVersion protocol version
     * @param msgType type code of message
     */
    public MessageTypeCodeKey(short msgVersion, short msgType) {
        this.msgType = msgType;
        this.msgVersion = msgVersion;
    }

    /**
     * @return the msgType
     */
    public short getMsgType() {
        return msgType;
    }

    /**
     * @return the msgVersion
     */
    public short getMsgVersion() {
        return msgVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + msgType;
        result = prime * result + msgVersion;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageTypeCodeKey other = (MessageTypeCodeKey) obj;
        if (msgType != other.msgType)
            return false;
        if (msgVersion != other.msgVersion)
            return false;
        return true;
    }
    
}
