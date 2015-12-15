/*
 * Copyright (c) 2015 NetIDE Consortium and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.serialization.factories.PacketOutInputMessageFactory;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.action.grouping.action.choice.PopVlanCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.action.grouping.action.choice.PushVlanCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.action.grouping.action.choice.push.vlan._case.PushVlanActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev150203.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketOutInputBuilder;

/**
 * @author giuseppex.petralia@intel.com
 *
 */
public class PacketOutInputMessageFactoryTest {
    private OFDeserializer<PacketOutInput> factory;

    @Before
    public void startUp() {
        DeserializerRegistry registry = new DeserializerRegistryImpl();
        registry.init();
        factory = registry
                .getDeserializer(new MessageCodeKey(EncodeConstants.OF13_VERSION_ID, 13, PacketOutInput.class));

    }

    @Test
    public void test() throws Exception {
        PacketOutInput expectedMessage = createMessage();
        SerializerRegistry registry = new SerializerRegistryImpl();
        registry.init();
        PacketOutInputMessageFactory serializer = new PacketOutInputMessageFactory();
        serializer.injectSerializerRegistry(registry);
        ByteBuf originalBuffer = UnpooledByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(expectedMessage, originalBuffer);

        // TODO: Skipping first 4 bytes due to the way deserializer is
        // implemented
        // Skipping version, type and length from OF header
        originalBuffer.skipBytes(4);
        PacketOutInput deserializedMessage = BufferHelper.deserialize(factory, originalBuffer);
        Assert.assertEquals("Wrong version", expectedMessage.getVersion(), deserializedMessage.getVersion());
        Assert.assertEquals("Wrong XId", expectedMessage.getXid(), deserializedMessage.getXid());
        Assert.assertEquals("Wrong buffer Id", expectedMessage.getBufferId(), deserializedMessage.getBufferId());
        Assert.assertEquals("Wrong In Port", expectedMessage.getInPort().getValue(),
                deserializedMessage.getInPort().getValue());
        Assert.assertEquals("Wrong Numbers of actions", expectedMessage.getAction().size(),
                deserializedMessage.getAction().size());
        int i = 0;
        for (Action a : expectedMessage.getAction()) {
            Assert.assertEquals("Wrong action", a, deserializedMessage.getAction().get(i));
            i++;
        }
        Assert.assertArrayEquals("Wrong data", expectedMessage.getData(), deserializedMessage.getData());
    }

    private PacketOutInput createMessage() throws Exception {
        PacketOutInputBuilder builder = new PacketOutInputBuilder();
        BufferHelper.setupHeader(builder, EncodeConstants.OF13_VERSION_ID);
        builder.setBufferId(256L);
        builder.setInPort(new PortNumber(256L));
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        PushVlanCaseBuilder pushVlanCaseBuilder = new PushVlanCaseBuilder();
        PushVlanActionBuilder pushVlanBuilder = new PushVlanActionBuilder();
        pushVlanBuilder.setEthertype(new EtherType(new EtherType(25)));
        pushVlanCaseBuilder.setPushVlanAction(pushVlanBuilder.build());
        actionBuilder.setActionChoice(pushVlanCaseBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setActionChoice(new PopVlanCaseBuilder().build());
        actions.add(actionBuilder.build());
        builder.setAction(actions);
        actionBuilder = new ActionBuilder();
        actionBuilder.setActionChoice(new PopVlanCaseBuilder().build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setActionChoice(new PopVlanCaseBuilder().build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setActionChoice(new PopVlanCaseBuilder().build());
        actions.add(actionBuilder.build());
        builder.setAction(actions);
        builder.setData(ByteBufUtils.hexStringToBytes("00 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14"));
        return builder.build();
    }
}
