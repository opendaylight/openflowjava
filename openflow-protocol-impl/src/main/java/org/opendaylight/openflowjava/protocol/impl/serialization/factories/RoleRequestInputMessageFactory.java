/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.core.OFFrameDecoder;
import org.opendaylight.openflowjava.protocol.impl.serialization.OFSerializer;
import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;

/**
 * @author michal.polkorab
 *
 */
public class RoleRequestInputMessageFactory implements OFSerializer<RoleRequestInput> {

    /** Code type of RoleRequest message */
    public static final byte MESSAGE_TYPE = 24;
    private static final byte PADDING_IN_ROLE_REQUEST_MESSAGE = 4;
    private static RoleRequestInputMessageFactory instance;
    
    private RoleRequestInputMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static RoleRequestInputMessageFactory getInstance() {
        if (instance == null) {
            instance = new RoleRequestInputMessageFactory();
        }
        return instance;
    }
    
    @Override
    public void messageToBuffer(short version, ByteBuf out,
            RoleRequestInput message) {
        out.writeByte(message.getVersion());
        out.writeByte(MESSAGE_TYPE);
        out.writeShort(OFFrameDecoder.LENGTH_OF_HEADER);
        out.writeInt(message.getXid().intValue());
        // TODO - finish implementation after enum support needed funcionality
        //out.writeInt(message.getRole());
        ByteBufUtils.padBuffer(PADDING_IN_ROLE_REQUEST_MESSAGE, out);
        out.writeLong(message.getGenerationId().longValue());
    }

}
