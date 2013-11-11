/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ErrorType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessageBuilder;

/**
 * @author michal.polkorab
 *
 */
public class OF10ErrorMessageFactory implements OFDeserializer<ErrorMessage> {

private static OF10ErrorMessageFactory instance;
    
    private OF10ErrorMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10ErrorMessageFactory getInstance() {
        if (instance == null) {
            instance = new OF10ErrorMessageFactory();
        }
        return instance;
    }
    
    @Override
    public ErrorMessage bufferToMessage(ByteBuf rawMessage, short version) {
        ErrorMessageBuilder builder = new ErrorMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setType(ErrorType.forValue(rawMessage.readUnsignedShort()));
        builder.setCode(rawMessage.readUnsignedShort());
        if (rawMessage.readableBytes() > 0) {
            builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        }
        return builder.build();
    }

}
