/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.ExperimenterMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.GetAsyncReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.GetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.MultipartReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10BarrierReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10EchoReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10EchoRequestMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10ErrorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10FeaturesReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10FlowRemovedMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10GetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10HelloMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.OF10StatsReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PacketInMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.PortStatusMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.QueueGetConfigReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.RoleReplyMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.VendorMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.SimpleDeserializerRegistryHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetAsyncOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetQueueConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutput;

/**
 * @author michal.polkorab
 *
 */
public final class MessageDeserializerInitializer {

    private MessageDeserializerInitializer() {
        throw new UnsupportedOperationException("Utility class shouldn't be instantiated");
    }

    /**
     * Registers message deserializers
     *
     * @param registry
     *            registry to be filled with deserializers
     */
    public static void registerMessageDeserializers(DeserializerRegistry registry) {
        // register OF v1.0 message deserializers
        SimpleDeserializerRegistryHelper helper = new SimpleDeserializerRegistryHelper(EncodeConstants.OF10_VERSION_ID,
                registry);
        helper.registerDeserializer(0, null, HelloMessage.class, new OF10HelloMessageFactory());
        helper.registerDeserializer(1, null, ErrorMessage.class, new OF10ErrorMessageFactory());
        helper.registerDeserializer(2, null, EchoRequestMessage.class, new OF10EchoRequestMessageFactory());
        helper.registerDeserializer(3, null, EchoOutput.class, new OF10EchoReplyMessageFactory());
        helper.registerDeserializer(4, null, ExperimenterMessage.class, new VendorMessageFactory());
        helper.registerDeserializer(6, null, GetFeaturesOutput.class, new OF10FeaturesReplyMessageFactory());
        helper.registerDeserializer(8, null, GetConfigOutput.class, new OF10GetConfigReplyMessageFactory());
        helper.registerDeserializer(10, null, PacketInMessage.class, new OF10PacketInMessageFactory());
        helper.registerDeserializer(11, null, FlowRemovedMessage.class, new OF10FlowRemovedMessageFactory());
        helper.registerDeserializer(12, null, PortStatusMessage.class, new OF10PortStatusMessageFactory());
        helper.registerDeserializer(17, null, MultipartReplyMessage.class, new OF10StatsReplyMessageFactory());
        helper.registerDeserializer(19, null, BarrierOutput.class, new OF10BarrierReplyMessageFactory());
        helper.registerDeserializer(21, null, GetQueueConfigOutput.class, new OF10QueueGetConfigReplyMessageFactory());

        // register Of v1.3 message deserializers
        helper = new SimpleDeserializerRegistryHelper(EncodeConstants.OF13_VERSION_ID, registry);
        helper.registerDeserializer(0, null, HelloMessage.class, new HelloMessageFactory());
        helper.registerDeserializer(1, null, ErrorMessage.class, new ErrorMessageFactory());
        helper.registerDeserializer(2, null, EchoRequestMessage.class, new EchoRequestMessageFactory());
        helper.registerDeserializer(3, null, EchoOutput.class, new EchoReplyMessageFactory());
        helper.registerDeserializer(4, null, ExperimenterMessage.class, new ExperimenterMessageFactory());
        helper.registerDeserializer(6, null, GetFeaturesOutput.class, new FeaturesReplyMessageFactory());
        helper.registerDeserializer(8, null, GetConfigOutput.class, new GetConfigReplyMessageFactory());
        helper.registerDeserializer(10, null, PacketInMessage.class, new PacketInMessageFactory());
        helper.registerDeserializer(11, null, FlowRemovedMessage.class, new FlowRemovedMessageFactory());
        helper.registerDeserializer(12, null, PortStatusMessage.class, new PortStatusMessageFactory());
        helper.registerDeserializer(19, null, MultipartReplyMessage.class, new MultipartReplyMessageFactory());
        helper.registerDeserializer(21, null, BarrierOutput.class, new BarrierReplyMessageFactory());
        helper.registerDeserializer(23, null, GetQueueConfigOutput.class, new QueueGetConfigReplyMessageFactory());
        helper.registerDeserializer(25, null, RoleRequestOutput.class, new RoleReplyMessageFactory());
        helper.registerDeserializer(27, null, GetAsyncOutput.class, new GetAsyncReplyMessageFactory());
    }
}
