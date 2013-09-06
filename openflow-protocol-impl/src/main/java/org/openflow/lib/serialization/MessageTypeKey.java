/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.lib.serialization;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 * @param <E> message type (class)
 *
 */
public class MessageTypeKey<E extends DataObject> {

    private final Class<E> msgType;
    private final short msgVersion;
    
    /**
     * @param msgVersion protocol version
     * @param msgType type of message
     */
    public MessageTypeKey(short msgVersion, Class<E> msgType) {
        super();
        this.msgType = msgType;
        this.msgVersion = msgVersion;
    }
    
    /**
     * @return msgVersion
     */
    public short getMsgVersion() {
        return msgVersion;
    }

    /**
     * @return the msgType
     */
    public Class<E> getMsgType() {
        return msgType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msgType == null) ? 0 : msgType.hashCode());
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
        MessageTypeKey<?> other = (MessageTypeKey<?>) obj;
        if (msgType == null) {
            if (other.msgType != null)
                return false;
        } else if (!msgType.equals(other.msgType))
            return false;
        if (msgVersion != other.msgVersion)
            return false;
        return true;
    }
 
}
