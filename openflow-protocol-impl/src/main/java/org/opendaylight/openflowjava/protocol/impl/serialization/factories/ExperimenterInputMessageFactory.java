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
import org.opendaylight.openflowjava.protocol.impl.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;

/**
 * Translates Experimenter messages
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class ExperimenterInputMessageFactory implements OFSerializer<ExperimenterInput>{

    /** Code type of Experimenter message */
    public static final byte MESSAGE_TYPE = 4;
    private static final byte MESSAGE_LENGTH = 8;
    private static ExperimenterInputMessageFactory instance;
    
    private ExperimenterInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized ExperimenterInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new ExperimenterInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            ExperimenterInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getExperimenter().intValue());
        out.writeInt(message.getExpType().intValue());
        byte[] data = message.getData();
        if (data != null) {
            out.writeBytes(data);
        }
    }

    @Override
    public int computeLength(ExperimenterInput message) {
        int length = MESSAGE_LENGTH + 2 * (EncodeConstants.SIZE_OF_INT_IN_BYTES);
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
