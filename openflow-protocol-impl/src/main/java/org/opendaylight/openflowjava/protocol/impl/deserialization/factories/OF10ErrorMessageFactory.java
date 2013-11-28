/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadActionCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadRequestCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ErrorTypeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFailedCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloFailedCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortModFailedCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueOpFailedCodeV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessageBuilder;

/**
 * Translates Error messages (OpenFlow v1.0)
 * @author michal.polkorab
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
        ErrorTypeV10 type = ErrorTypeV10.forValue(rawMessage.readUnsignedShort());
        decodeType(builder, type);
        decodeCode(rawMessage, builder, type);
        if (rawMessage.readableBytes() > 0) {
            builder.setData(rawMessage.readBytes(rawMessage.readableBytes()).array());
        }
        return builder.build();
    }

    private static void decodeCode(ByteBuf rawMessage, ErrorMessageBuilder builder,
            ErrorTypeV10 type) {
        switch (type) {
        case HELLOFAILED:
        {
            HelloFailedCodeV10 code = HelloFailedCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        case BADREQUEST:
        {
            BadRequestCodeV10 code = BadRequestCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        case BADACTION:
        {
            BadActionCodeV10 code = BadActionCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        case FLOWMODFAILED:
        {
            FlowModFailedCodeV10 code = FlowModFailedCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        case PORTMODFAILED:
        {
            PortModFailedCodeV10 code = PortModFailedCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        case QUEUEOPFAILED:
        {
            QueueOpFailedCodeV10 code = QueueOpFailedCodeV10.forValue(rawMessage.readUnsignedShort());
            if (code != null) {
                builder.setCode(code.name());
            }
            break;
        }
        default:
            builder.setCode("UNKNOWN_CODE");
            break;
        }
    }

    private static void decodeType(ErrorMessageBuilder builder, ErrorTypeV10 type) {
        if (type != null) {
            builder.setType(type.name());
        } else {
            builder.setType("UNKNOWN_TYPE");
        }
    }

}
