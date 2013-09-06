/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.core.deserialization;

/**
 * @author michal.polkorab
 *
 */
public class MessageTypeKey {

    private final short msgType;
    private final short msgVersion;

    /**
     * @param msgVersion protocol version
     * @param msgType type code of message
     */
    public MessageTypeKey(short msgVersion, short msgType) {
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
        MessageTypeKey other = (MessageTypeKey) obj;
        if (msgType != other.msgType)
            return false;
        if (msgVersion != other.msgVersion)
            return false;
        return true;
    }
    
}
