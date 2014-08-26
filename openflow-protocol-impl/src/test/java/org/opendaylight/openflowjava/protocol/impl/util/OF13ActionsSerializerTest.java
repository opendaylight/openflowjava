/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.EthertypeActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.GroupIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MplsTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.OxmFieldsActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortNumberMatchEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlIn;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.CopyTtlOut;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.DecNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PopVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushMpls;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushPbb;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.PushVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetMplsTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetQueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntries;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.grouping.MatchEntriesBuilder;

/**
 * @author michal.polkorab
 * 
 */
public class OF13ActionsSerializerTest {

    private SerializerRegistry registry;

    /**
     * Initializes serializer table and stores correct factory in field
     */
    @Before
    public void startUp() {
        registry = new SerializerRegistryImpl();
        registry.init();
    }

    /**
     * Testing correct serialization of actions
     */
    @Test
    public void test() {
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder port = new PortActionBuilder();
        port.setPort(new PortNumber(42L));
        actionBuilder.addAugmentation(PortAction.class, port.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(52);
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(CopyTtlOut.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(CopyTtlIn.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetMplsTtl.class);
        MplsTtlActionBuilder mplsTtl = new MplsTtlActionBuilder();
        mplsTtl.setMplsTtl((short) 4);
        actionBuilder.addAugmentation(MplsTtlAction.class, mplsTtl.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(DecMplsTtl.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PushVlan.class);
        EthertypeActionBuilder etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(16));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PopVlan.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PushMpls.class);
        etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(17));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PopMpls.class);
        etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(18));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetQueue.class);
        QueueIdActionBuilder queueId = new QueueIdActionBuilder();
        queueId.setQueueId(1234L);
        actionBuilder.addAugmentation(QueueIdAction.class, queueId.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(Group.class);
        GroupIdActionBuilder group = new GroupIdActionBuilder();
        group.setGroupId(555L);
        actionBuilder.addAugmentation(GroupIdAction.class, group.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwTtl.class);
        NwTtlActionBuilder nwTtl = new NwTtlActionBuilder();
        nwTtl.setNwTtl((short) 8);
        actionBuilder.addAugmentation(NwTtlAction.class, nwTtl.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(DecNwTtl.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetField.class);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder matchBuilder = new MatchEntriesBuilder();
        matchBuilder.setOxmClass(OpenflowBasicClass.class);
        matchBuilder.setOxmMatchField(InPort.class);
        matchBuilder.setHasMask(false);
        PortNumberMatchEntryBuilder portBuilder = new PortNumberMatchEntryBuilder();
        portBuilder.setPortNumber(new PortNumber(1L));
        matchBuilder.addAugmentation(PortNumberMatchEntry.class, portBuilder.build());
        entries.add(matchBuilder.build());
        matchEntries.setMatchEntries(entries);
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PushPbb.class);
        etherType = new EthertypeActionBuilder();
        etherType.setEthertype(new EtherType(19));
        actionBuilder.addAugmentation(EthertypeAction.class, etherType.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(PopPbb.class);
        actions.add(actionBuilder.build());
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        ListSerializer.serializeList(actions, TypeKeyMakerFactory
                .createActionKeyMaker(EncodeConstants.OF13_VERSION_ID), registry, out);
        
        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong action port", 42, out.readUnsignedInt());
        Assert.assertEquals("Wrong action max-length", 52, out.readUnsignedShort());
        out.skipBytes(6);
        Assert.assertEquals("Wrong action type", 11, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 12, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 15, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action mpls-ttl", 4, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong action type", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 17, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action ethertype", 16, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 18, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 19, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action ethertype", 17, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 20, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action ethertype", 18, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 21, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action queue-id", 1234, out.readUnsignedInt());
        Assert.assertEquals("Wrong action type", 22, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action group", 555, out.readUnsignedInt());
        Assert.assertEquals("Wrong action type", 23, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action nw-ttl", 8, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong action type", 24, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 25, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong match entry class", 0x8000, out.readUnsignedShort());
        Assert.assertEquals("Wrong match entry field & mask", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong match entry length", 4, out.readUnsignedByte());
        Assert.assertEquals("Wrong match entry value", 1, out.readUnsignedInt());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 26, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action ethertype", 19, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 27, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertTrue("Unread data", out.readableBytes() == 0);
    }

    /**
     * Testing correct serialization of actions
     */
    @Test
    public void testHeaders() {
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder port = new PortActionBuilder();
        port.setPort(new PortNumber(42L));
        actionBuilder.addAugmentation(PortAction.class, port.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(52);
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetField.class);
        OxmFieldsActionBuilder matchEntries = new OxmFieldsActionBuilder();
        List<MatchEntries> entries = new ArrayList<>();
        MatchEntriesBuilder matchBuilder = new MatchEntriesBuilder();
        matchBuilder.setOxmClass(OpenflowBasicClass.class);
        matchBuilder.setOxmMatchField(InPort.class);
        matchBuilder.setHasMask(false);
        PortNumberMatchEntryBuilder portBuilder = new PortNumberMatchEntryBuilder();
        portBuilder.setPortNumber(new PortNumber(1L));
        matchBuilder.addAugmentation(PortNumberMatchEntry.class, portBuilder.build());
        entries.add(matchBuilder.build());
        matchEntries.setMatchEntries(entries);
        actionBuilder.addAugmentation(OxmFieldsAction.class, matchEntries.build());
        actions.add(actionBuilder.build());

        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        ListSerializer.serializeHeaderList(actions, TypeKeyMakerFactory
                .createActionKeyMaker(EncodeConstants.OF13_VERSION_ID), registry, out);

        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 25, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 4, out.readUnsignedShort());
        Assert.assertTrue("Unread data", out.readableBytes() == 0);
    }
}