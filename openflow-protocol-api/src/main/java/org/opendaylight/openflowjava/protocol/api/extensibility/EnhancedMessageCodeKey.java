/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.api.extensibility;

/**
 * @author michal.polkorab
 *
 */
public class EnhancedMessageCodeKey extends MessageCodeKey {

    private int msgType2;

    /**
     * Constructor
     * @param version wire protocol version
     * @param value used as distinguisher
     * @param value2 used as detailed distinguisher
     * @param clazz class of object that is going to be deserialized
     */
    public EnhancedMessageCodeKey(short version, int value, int value2, Class<?> clazz) {
        super(version, value, clazz);
        this.msgType2 = value2;
    }

    /**
     * @return msgType2
     */
    public int getMsgType2() {
        return msgType2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        EnhancedMessageCodeKey other = (EnhancedMessageCodeKey) obj;
        if (msgType2 != other.msgType2)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "msgVersion: " + super.getMsgVersion() + " msgType: " + super.getMsgType()
                + " msgType2: " + msgType2 + " class: " + super.getClazz().getName();
    }
}
