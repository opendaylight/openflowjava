/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;

/**
 * Translates SetConfig messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class SetConfigMessageFactory implements OFSerializer<SetConfigInput> {

    /** Code type of SetConfig message */
    public static final byte MESSAGE_TYPE = 9;
    private static final int MESSAGE_LENGTH = 12;
    private static SetConfigMessageFactory instance;
    
    private SetConfigMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized SetConfigMessageFactory getInstance() {
        if (instance == null) {
            instance = new SetConfigMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            SetConfigInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeShort(message.getFlags().getIntValue());
        out.writeShort(message.getMissSendLen());
    }

    @Override
    public int computeLength(SetConfigInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
