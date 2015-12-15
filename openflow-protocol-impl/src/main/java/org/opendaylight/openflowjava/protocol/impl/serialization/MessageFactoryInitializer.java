/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.serialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.BarrierInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoOutputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoReplyInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.ExperimenterInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.ExperimenterMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.FlowModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetAsyncReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetAsyncRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetFeaturesInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetFeaturesOutputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GetQueueConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.GroupModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.HelloInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MeterModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.MultipartRequestInputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10BarrierInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10FlowModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10HelloInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PortModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10QueueGetConfigInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10StatsReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.OF10StatsRequestInputFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PortModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.RoleReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.RoleRequestInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetAsyncInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.SetConfigMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.TableModInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.VendorInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.CommonMessageRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoReplyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GroupModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MeterModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetAsyncInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.SetConfigInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;

/**
 * @author michal.polkorab
 *
 */
public final class MessageFactoryInitializer {

    private MessageFactoryInitializer() {
        throw new UnsupportedOperationException("Utility class shouldn't be instantiated");
    }

    /**
     * Registers message serializers into provided registry
     *
     * @param serializerRegistry
     *            registry to be initialized with message serializers
     */
    public static void registerMessageSerializers(SerializerRegistry serializerRegistry) {
        // register OF v1.0 message serializers
        short version = EncodeConstants.OF10_VERSION_ID;
        CommonMessageRegistryHelper registryHelper = new CommonMessageRegistryHelper(version, serializerRegistry);
        registryHelper.registerSerializer(BarrierInput.class, new OF10BarrierInputMessageFactory());
        registryHelper.registerSerializer(EchoInput.class, new EchoInputMessageFactory());
        registryHelper.registerSerializer(EchoReplyInput.class, new EchoReplyInputMessageFactory());
        registryHelper.registerSerializer(ExperimenterInput.class, new VendorInputMessageFactory());
        registryHelper.registerSerializer(FlowModInput.class, new OF10FlowModInputMessageFactory());
        registryHelper.registerSerializer(GetConfigInput.class, new GetConfigInputMessageFactory());
        registryHelper.registerSerializer(GetFeaturesInput.class, new GetFeaturesInputMessageFactory());
        registryHelper.registerSerializer(GetQueueConfigInput.class, new OF10QueueGetConfigInputMessageFactory());
        registryHelper.registerSerializer(HelloInput.class, new OF10HelloInputMessageFactory());
        registryHelper.registerSerializer(MultipartRequestInput.class, new OF10StatsRequestInputFactory());
        registryHelper.registerSerializer(PacketOutInput.class, new OF10PacketOutInputMessageFactory());
        registryHelper.registerSerializer(PortModInput.class, new OF10PortModInputMessageFactory());
        registryHelper.registerSerializer(SetConfigInput.class, new SetConfigMessageFactory());

        registryHelper.registerSerializer(ErrorMessage.class, new ErrorMessageFactory());
        registryHelper.registerSerializer(EchoRequestMessage.class, new EchoRequestMessageFactory());
        registryHelper.registerSerializer(EchoOutput.class, new EchoOutputMessageFactory());
        registryHelper.registerSerializer(GetFeaturesOutput.class, new OF10FeaturesReplyMessageFactory());
        registryHelper.registerSerializer(GetConfigOutput.class, new GetConfigReplyMessageFactory());
        registryHelper.registerSerializer(PacketInMessage.class, new OF10PacketInMessageFactory());
        registryHelper.registerSerializer(FlowRemovedMessage.class, new OF10FlowRemovedMessageFactory());
        registryHelper.registerSerializer(PortStatusMessage.class, new OF10PortStatusMessageFactory());
        registryHelper.registerSerializer(MultipartReplyMessage.class, new OF10StatsReplyMessageFactory());
        registryHelper.registerSerializer(BarrierOutput.class, new OF10BarrierReplyMessageFactory());
        registryHelper.registerSerializer(GetQueueConfigOutput.class, new OF10QueueGetConfigReplyMessageFactory());

        // register OF v1.3 message serializers
        version = EncodeConstants.OF13_VERSION_ID;
        registryHelper = new CommonMessageRegistryHelper(version, serializerRegistry);
        registryHelper.registerSerializer(BarrierInput.class, new BarrierInputMessageFactory());
        registryHelper.registerSerializer(EchoInput.class, new EchoInputMessageFactory());
        registryHelper.registerSerializer(EchoReplyInput.class, new EchoReplyInputMessageFactory());
        registryHelper.registerSerializer(ExperimenterInput.class, new ExperimenterInputMessageFactory());
        registryHelper.registerSerializer(FlowModInput.class, new FlowModInputMessageFactory());
        registryHelper.registerSerializer(GetAsyncInput.class, new GetAsyncRequestMessageFactory());
        registryHelper.registerSerializer(GetConfigInput.class, new GetConfigInputMessageFactory());
        registryHelper.registerSerializer(GetFeaturesInput.class, new GetFeaturesInputMessageFactory());
        registryHelper.registerSerializer(GetQueueConfigInput.class, new GetQueueConfigInputMessageFactory());
        registryHelper.registerSerializer(GroupModInput.class, new GroupModInputMessageFactory());
        registryHelper.registerSerializer(HelloInput.class, new HelloInputMessageFactory());
        registryHelper.registerSerializer(MeterModInput.class, new MeterModInputMessageFactory());
        registryHelper.registerSerializer(MultipartRequestInput.class, new MultipartRequestInputFactory());
        registryHelper.registerSerializer(PacketOutInput.class, new PacketOutInputMessageFactory());
        registryHelper.registerSerializer(PortModInput.class, new PortModInputMessageFactory());
        registryHelper.registerSerializer(RoleRequestInput.class, new RoleRequestInputMessageFactory());
        registryHelper.registerSerializer(SetAsyncInput.class, new SetAsyncInputMessageFactory());
        registryHelper.registerSerializer(SetConfigInput.class, new SetConfigMessageFactory());
        registryHelper.registerSerializer(TableModInput.class, new TableModInputMessageFactory());

        registryHelper.registerSerializer(EchoOutput.class, new EchoOutputMessageFactory());
        registryHelper.registerSerializer(PacketInMessage.class, new PacketInMessageFactory());
        registryHelper.registerSerializer(PacketOutInput.class, new PacketOutInputMessageFactory());
        registryHelper.registerSerializer(GetFeaturesOutput.class, new GetFeaturesOutputFactory());
        registryHelper.registerSerializer(EchoRequestMessage.class, new EchoRequestMessageFactory());
        registryHelper.registerSerializer(MultipartReplyMessage.class, new MultipartReplyMessageFactory());
        registryHelper.registerSerializer(HelloMessage.class, new HelloMessageFactory());
        registryHelper.registerSerializer(ErrorMessage.class, new ErrorMessageFactory());
        registryHelper.registerSerializer(ExperimenterMessage.class, new ExperimenterMessageFactory());
        registryHelper.registerSerializer(GetConfigOutput.class, new GetConfigReplyMessageFactory());
        registryHelper.registerSerializer(FlowRemovedMessage.class, new FlowRemovedMessageFactory());
        registryHelper.registerSerializer(PortStatusMessage.class, new PortStatusMessageFactory());
        registryHelper.registerSerializer(BarrierOutput.class, new BarrierReplyMessageFactory());
        registryHelper.registerSerializer(GetQueueConfigOutput.class, new QueueGetConfigReplyMessageFactory());
        registryHelper.registerSerializer(RoleRequestOutput.class, new RoleReplyMessageFactory());
        registryHelper.registerSerializer(GetAsyncOutput.class, new GetAsyncReplyMessageFactory());
    }
}
