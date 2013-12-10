/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * Class used as a key in {@link EncoderTable}
 * @author michal.polkorab
 * @author timotej.kubas
 * @param <E> message type (class)
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
    public String toString() {
        return "msgVersion: " + msgVersion + " msgType: " + msgType.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        } else if (!other.msgType.isAssignableFrom(msgType))
            return false;
        if (msgVersion != other.msgVersion)
            return false;
        return true;
    }
 
}
