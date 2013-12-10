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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;

/**
 * Translates EchoReply messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class EchoReplyInputMessageFactory implements OFSerializer<EchoReplyInput>{

    /** Code type of EchoReply message */
    public static final byte MESSAGE_TYPE = 3;
    private static final int MESSAGE_LENGTH = 8;
    private static EchoReplyInputMessageFactory instance;
    
    private EchoReplyInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized EchoReplyInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new EchoReplyInputMessageFactory();
        }
        return instance;
    }

    @Override
    public void messageToBuffer(short version, ByteBuf out,
            EchoReplyInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(EchoReplyInput message) {
        int length = MESSAGE_LENGTH;
        byte[] data = message.getData();
        if (data != null) {
            length += data.length;
        }
        return length;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }
}
