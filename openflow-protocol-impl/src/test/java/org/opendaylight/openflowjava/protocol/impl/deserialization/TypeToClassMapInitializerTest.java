/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
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
public class TypeToClassMapInitializerTest {

    private Map<TypeToClassKey, Class<?>> messageClassMap;

    /**
     * Tests correct map initialization
     */
    @Test
    public void test() {
        messageClassMap = new HashMap<>();
        TypeToClassMapInitializer.initializeTypeToClassMap(messageClassMap);
        short version = EncodeConstants.OF10_VERSION_ID;
        assertEquals("Wrong class", HelloMessage.class, messageClassMap.get(new TypeToClassKey(version, 0)));
        assertEquals("Wrong class", ErrorMessage.class, messageClassMap.get(new TypeToClassKey(version, 1)));
        assertEquals("Wrong class", EchoRequestMessage.class, messageClassMap.get(new TypeToClassKey(version, 2)));
        assertEquals("Wrong class", EchoOutput.class, messageClassMap.get(new TypeToClassKey(version, 3)));
        assertEquals("Wrong class", ExperimenterMessage.class, messageClassMap.get(new TypeToClassKey(version, 4)));
        assertEquals("Wrong class", GetFeaturesOutput.class, messageClassMap.get(new TypeToClassKey(version, 6)));
        assertEquals("Wrong class", GetConfigOutput.class, messageClassMap.get(new TypeToClassKey(version, 8)));
        assertEquals("Wrong class", PacketInMessage.class, messageClassMap.get(new TypeToClassKey(version, 10)));
        assertEquals("Wrong class", FlowRemovedMessage.class, messageClassMap.get(new TypeToClassKey(version, 11)));
        assertEquals("Wrong class", PortStatusMessage.class, messageClassMap.get(new TypeToClassKey(version, 12)));
        assertEquals("Wrong class", MultipartReplyMessage.class, messageClassMap.get(new TypeToClassKey(version, 17)));
        assertEquals("Wrong class", BarrierOutput.class, messageClassMap.get(new TypeToClassKey(version, 19)));
        assertEquals("Wrong class", GetQueueConfigOutput.class, messageClassMap.get(new TypeToClassKey(version, 21)));
        version = EncodeConstants.OF13_VERSION_ID;
        assertEquals("Wrong class", HelloMessage.class, messageClassMap.get(new TypeToClassKey(version, 0)));
        assertEquals("Wrong class", ErrorMessage.class, messageClassMap.get(new TypeToClassKey(version, 1)));
        assertEquals("Wrong class", EchoRequestMessage.class, messageClassMap.get(new TypeToClassKey(version, 2)));
        assertEquals("Wrong class", EchoOutput.class, messageClassMap.get(new TypeToClassKey(version, 3)));
        assertEquals("Wrong class", ExperimenterMessage.class, messageClassMap.get(new TypeToClassKey(version, 4)));
        assertEquals("Wrong class", GetFeaturesOutput.class, messageClassMap.get(new TypeToClassKey(version, 6)));
        assertEquals("Wrong class", GetConfigOutput.class, messageClassMap.get(new TypeToClassKey(version, 8)));
        assertEquals("Wrong class", PacketInMessage.class, messageClassMap.get(new TypeToClassKey(version, 10)));
        assertEquals("Wrong class", FlowRemovedMessage.class, messageClassMap.get(new TypeToClassKey(version, 11)));
        assertEquals("Wrong class", PortStatusMessage.class, messageClassMap.get(new TypeToClassKey(version, 12)));
        assertEquals("Wrong class", MultipartReplyMessage.class, messageClassMap.get(new TypeToClassKey(version, 19)));
        assertEquals("Wrong class", BarrierOutput.class, messageClassMap.get(new TypeToClassKey(version, 21)));
        assertEquals("Wrong class", GetQueueConfigOutput.class, messageClassMap.get(new TypeToClassKey(version, 23)));
        assertEquals("Wrong class", RoleRequestOutput.class, messageClassMap.get(new TypeToClassKey(version, 25)));
        assertEquals("Wrong class", GetAsyncOutput.class, messageClassMap.get(new TypeToClassKey(version, 27)));
    }
}