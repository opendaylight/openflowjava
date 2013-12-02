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

    private static final byte CODE_LENGTH = 2;
	private static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";
    private static final String UNKNOWN_CODE = "UNKNOWN_CODE";
    private static final int NO_CORRECT_ENUM_FOUND_VALUE = -1;
    
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
    
    private static void decodeType(ErrorMessageBuilder builder, ErrorTypeV10 type) {
        if (type != null) {
            builder.setType(type.getIntValue());
            builder.setTypeString(type.name());
        } else {
            builder.setType(NO_CORRECT_ENUM_FOUND_VALUE);
            builder.setTypeString(UNKNOWN_TYPE);
        }
    }

    private static void decodeCode(ByteBuf rawMessage, ErrorMessageBuilder builder,
            ErrorTypeV10 type) {
    	if (type != null) {
    		switch (type) {
    		case HELLOFAILED:
    		{
    			HelloFailedCodeV10 code = HelloFailedCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		case BADREQUEST:
    		{
    			BadRequestCodeV10 code = BadRequestCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		case BADACTION:
    		{
    			BadActionCodeV10 code = BadActionCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		case FLOWMODFAILED:
    		{
    			FlowModFailedCodeV10 code = FlowModFailedCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		case PORTMODFAILED:
    		{
    			PortModFailedCodeV10 code = PortModFailedCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		case QUEUEOPFAILED:
    		{
    			QueueOpFailedCodeV10 code = QueueOpFailedCodeV10.forValue(rawMessage.readUnsignedShort());
    			if (code != null) {
    				setCode(builder, code.getIntValue(), code.name());
    			} else {
    				setUnknownCode(builder);
    			}
    			break;
    		}
    		default:
    			setUnknownCode(builder);
    			break;
    		}
    	} else {
    		rawMessage.skipBytes(CODE_LENGTH);
    		setUnknownCode(builder);
    	}
    }
    
    private static void setUnknownCode(ErrorMessageBuilder builder) {
    	builder.setCode(NO_CORRECT_ENUM_FOUND_VALUE);
		builder.setCodeString(UNKNOWN_CODE);
    }
    
    private static void setCode(ErrorMessageBuilder builder, int code, String codeString) {
    	builder.setCode(code);
		builder.setCodeString(codeString);
    }

}
