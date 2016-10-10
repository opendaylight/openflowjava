/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.util.VersionAssignableFactory;
import org.opendaylight.openflowjava.util.ExperimenterDeserializerKeyFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadActionCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadInstructionCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadMatchCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.BadRequestCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ErrorType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.GroupModFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.HelloFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MeterModFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortModFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.QueueOpFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.RoleRequestFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.SwitchConfigFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableFeaturesFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableModFailedCode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessageBuilder;

/**
 * Translates Error messages.
 * OpenFlow protocol versions: 1.3, 1.4, 1.5.
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class ErrorMessageFactory extends VersionAssignableFactory implements OFDeserializer<ErrorMessage>,
        DeserializerRegistryInjector {

    private static final String UNKNOWN_CODE = "UNKNOWN_CODE";
    private static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";
    private DeserializerRegistry registry;

    @Override
    public ErrorMessage deserialize(ByteBuf rawMessage) {
        int startIndex = rawMessage.readerIndex();
        ErrorMessageBuilder builder = new ErrorMessageBuilder();
        builder.setVersion(getVersion());
        builder.setXid(rawMessage.readUnsignedInt());
        int type = rawMessage.readUnsignedShort();
        ErrorType errorType = ErrorType.forValue(type);
        if (ErrorType.EXPERIMENTER.equals(errorType)) {
            OFDeserializer<ErrorMessage> deserializer = registry.getDeserializer(
                    ExperimenterDeserializerKeyFactory.createExperimenterErrorDeserializerKey(
                            getVersion(), rawMessage.getUnsignedInt(
                                    rawMessage.readerIndex() + EncodeConstants.SIZE_OF_SHORT_IN_BYTES)));
            rawMessage.readerIndex(startIndex);
            return deserializer.deserialize(rawMessage);
        }
        decodeType(builder, errorType, type);
        decodeCode(rawMessage, builder, errorType);
        int remainingBytes = rawMessage.readableBytes();
        if (remainingBytes > 0) {
            byte[] data = new byte[remainingBytes];
            rawMessage.readBytes(data);
            builder.setData(data);
        }
        return builder.build();
    }

    private static void decodeType(ErrorMessageBuilder builder, ErrorType type, int readValue) {
        if (type != null) {
            builder.setType(type.getIntValue());
            builder.setTypeString(type.name());
        } else {
            builder.setType(readValue);
            builder.setTypeString(UNKNOWN_TYPE);
        }
    }

    private static void decodeCode(ByteBuf rawMessage, ErrorMessageBuilder builder,
            ErrorType type) {
        int code = rawMessage.readUnsignedShort();
        if (type != null) {
            switch (type) {
                case HELLOFAILED:
                    HelloFailedCode helloFailedCode = HelloFailedCode.forValue(code);
                    if (helloFailedCode != null) {
                        setCode(builder, helloFailedCode.getIntValue(), helloFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case BADREQUEST:
                    BadRequestCode badRequestCode = BadRequestCode.forValue(code);
                    if (badRequestCode != null) {
                        setCode(builder, badRequestCode.getIntValue(), badRequestCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case BADACTION:
                    BadActionCode badActionCode = BadActionCode.forValue(code);
                    if (badActionCode != null) {
                        setCode(builder, badActionCode.getIntValue(), badActionCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case BADINSTRUCTION:
                    BadInstructionCode badInstructionCode = BadInstructionCode.forValue(code);
                    if (badInstructionCode != null) {
                        setCode(builder, badInstructionCode.getIntValue(), badInstructionCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case BADMATCH:
                    BadMatchCode badMatchCode = BadMatchCode.forValue(code);
                    if (badMatchCode != null) {
                        setCode(builder, badMatchCode.getIntValue(), badMatchCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case FLOWMODFAILED:
                    FlowModFailedCode flowModFailedCode = FlowModFailedCode.forValue(code);
                    if (flowModFailedCode != null) {
                        setCode(builder, flowModFailedCode.getIntValue(), flowModFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case GROUPMODFAILED:
                    GroupModFailedCode groupModFailedCode = GroupModFailedCode.forValue(code);
                    if (groupModFailedCode != null) {
                        setCode(builder, groupModFailedCode.getIntValue(), groupModFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case PORTMODFAILED:
                    PortModFailedCode portModFailedCode = PortModFailedCode.forValue(code);
                    if (portModFailedCode != null) {
                        setCode(builder, portModFailedCode.getIntValue(), portModFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case TABLEMODFAILED:
                    TableModFailedCode tableModFailedCode = TableModFailedCode.forValue(code);
                    if (tableModFailedCode != null) {
                        setCode(builder, tableModFailedCode.getIntValue(), tableModFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case QUEUEOPFAILED:
                    QueueOpFailedCode queueOpFailedCode = QueueOpFailedCode.forValue(code);
                    if (queueOpFailedCode != null) {
                        setCode(builder, queueOpFailedCode.getIntValue(), queueOpFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case SWITCHCONFIGFAILED:
                    SwitchConfigFailedCode switchConfigFailedCode = SwitchConfigFailedCode.forValue(code);
                    if (switchConfigFailedCode != null) {
                        setCode(builder, switchConfigFailedCode.getIntValue(), switchConfigFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case ROLEREQUESTFAILED:
                    RoleRequestFailedCode roleRequestFailedCode = RoleRequestFailedCode.forValue(code);
                    if (roleRequestFailedCode != null) {
                        setCode(builder, roleRequestFailedCode.getIntValue(), roleRequestFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case METERMODFAILED:
                    MeterModFailedCode meterModFailedCode = MeterModFailedCode.forValue(code);
                    if (meterModFailedCode != null) {
                        setCode(builder, meterModFailedCode.getIntValue(), meterModFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                case TABLEFEATURESFAILED:
                    TableFeaturesFailedCode tableFeaturesFailedCode = TableFeaturesFailedCode.forValue(code);
                    if (tableFeaturesFailedCode != null) {
                        setCode(builder, tableFeaturesFailedCode.getIntValue(), tableFeaturesFailedCode.name());
                    } else {
                        setUnknownCode(builder, code);
                    }
                    break;
                default:
                    setUnknownCode(builder, code);
                    break;
            }
        } else {
            setUnknownCode(builder, code);
        }
    }

    private static void setUnknownCode(ErrorMessageBuilder builder, int readValue) {
        builder.setCode(readValue);
        builder.setCodeString(UNKNOWN_CODE);
    }

    private static void setCode(ErrorMessageBuilder builder, int code, String codeString) {
        builder.setCode(code);
        builder.setCodeString(codeString);
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        this.registry = deserializerRegistry;
    }

}
