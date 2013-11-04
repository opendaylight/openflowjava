/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author michal.polkorab
 *
 */
public class RoleRequestInputMessageFactory implements OFSerializer<RoleRequestInput> {

    /** Code type of RoleRequest message */
    public static final byte MESSAGE_TYPE = 24;
    private static final int MESSAGE_LENGTH = 24;
    private static final byte PADDING_IN_ROLE_REQUEST_MESSAGE = 4;
    private static RoleRequestInputMessageFactory instance;
    
    private RoleRequestInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized RoleRequestInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new RoleRequestInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            RoleRequestInput message) {
        ByteBufUtils.writeOFHeader(instance, message, out);
        out.writeInt(message.getRole().getIntValue());
        ByteBufUtils.padBuffer(PADDING_IN_ROLE_REQUEST_MESSAGE, out);
        out.writeLong(message.getGenerationId().longValue());
    }

    @Override
    public int computeLength(RoleRequestInput message) {
        return MESSAGE_LENGTH;
    }

    @Override
    public byte getMessageType() {
        return MESSAGE_TYPE;
    }

}
