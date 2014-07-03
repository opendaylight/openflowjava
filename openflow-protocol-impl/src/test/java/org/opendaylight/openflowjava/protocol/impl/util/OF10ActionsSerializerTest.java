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
import org.opendaylight.openflowjava.util.ByteBufUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Enqueue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetDlSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetNwTos;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetTpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanPcp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.SetVlanVid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.StripVlan;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;

/**
 * @author michal.polkorab
 *
 */
public class OF10ActionsSerializerTest {

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
     * Testing correct serialization of actions (OF v1.0) 
     */
    @Test
    public void test() {
        List<Action> actions = new ArrayList<>();
        ActionBuilder actionBuilder = new ActionBuilder();
        actionBuilder.setType(Output.class);
        PortActionBuilder portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(42L));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
        MaxLengthActionBuilder maxLen = new MaxLengthActionBuilder();
        maxLen.setMaxLength(32);
        actionBuilder.addAugmentation(MaxLengthAction.class, maxLen.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetVlanVid.class);
        VlanVidActionBuilder vlanBuilder = new VlanVidActionBuilder();
        vlanBuilder.setVlanVid(15);
        actionBuilder.addAugmentation(VlanVidAction.class, vlanBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetVlanPcp.class);
        VlanPcpActionBuilder pcpBuilder = new VlanPcpActionBuilder();
        pcpBuilder.setVlanPcp((short) 16);
        actionBuilder.addAugmentation(VlanPcpAction.class, pcpBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(StripVlan.class);
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetDlSrc.class);
        DlAddressActionBuilder dlBuilder = new DlAddressActionBuilder();
        dlBuilder.setDlAddress(new MacAddress("00:00:00:02:03:04"));
        actionBuilder.addAugmentation(DlAddressAction.class, dlBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetDlDst.class);
        dlBuilder = new DlAddressActionBuilder();
        dlBuilder.setDlAddress(new MacAddress("00:00:00:01:02:03"));
        actionBuilder.addAugmentation(DlAddressAction.class, dlBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwSrc.class);
        IpAddressActionBuilder ipBuilder = new IpAddressActionBuilder();
        ipBuilder.setIpAddress(new Ipv4Address("10.0.0.1"));
        actionBuilder.addAugmentation(IpAddressAction.class, ipBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwDst.class);
        ipBuilder = new IpAddressActionBuilder();
        ipBuilder.setIpAddress(new Ipv4Address("10.0.0.3"));
        actionBuilder.addAugmentation(IpAddressAction.class, ipBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetNwTos.class);
        NwTosActionBuilder tosBuilder = new NwTosActionBuilder();
        tosBuilder.setNwTos((short) 204);
        actionBuilder.addAugmentation(NwTosAction.class, tosBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetTpSrc.class);
        portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(6653L));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(SetTpDst.class);
        portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(6633L));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(Enqueue.class);
        portBuilder = new PortActionBuilder();
        portBuilder.setPort(new PortNumber(6613L));
        actionBuilder.addAugmentation(PortAction.class, portBuilder.build());
        QueueIdActionBuilder queueBuilder = new QueueIdActionBuilder();
        queueBuilder.setQueueId(400L);
        actionBuilder.addAugmentation(QueueIdAction.class, queueBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        ExperimenterActionBuilder expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(82L);
        byte[] expData = new byte[]{0, 0, 0, 0, 0, 0, 0, 1};
        expBuilder.setData(expData);
        actionBuilder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        actions.add(actionBuilder.build());
        actionBuilder = new ActionBuilder();
        actionBuilder.setType(Experimenter.class);
        expBuilder = new ExperimenterActionBuilder();
        expBuilder.setExperimenter(102L);
        actionBuilder.addAugmentation(ExperimenterAction.class, expBuilder.build());
        actions.add(actionBuilder.build());
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        ListSerializer.serializeList(actions, EnhancedTypeKeyMakerFactory
                .createActionKeyMaker(EncodeConstants.OF10_VERSION_ID), registry, out);
        
        Assert.assertEquals("Wrong action type", 0, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 42, out.readUnsignedShort());
        Assert.assertEquals("Wrong max-length", 32, out.readUnsignedShort());
        Assert.assertEquals("Wrong action type", 1, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong vlan-vid", 15, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 2, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong vlan-pcp", 16, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong action type", 3, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        out.skipBytes(4);
        Assert.assertEquals("Wrong action type", 4, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        byte[] data = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        out.readBytes(data);
        Assert.assertArrayEquals("Wrong dl-address", ByteBufUtils.macAddressToBytes("00:00:00:02:03:04"), data);
        out.skipBytes(6);
        Assert.assertEquals("Wrong action type", 5, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        data = new byte[EncodeConstants.MAC_ADDRESS_LENGTH];
        out.readBytes(data);
        Assert.assertArrayEquals("Wrong dl-address", ByteBufUtils.macAddressToBytes("00:00:00:01:02:03"), data);
        out.skipBytes(6);
        Assert.assertEquals("Wrong action type", 6, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong ip-address(1)", 10, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(2)", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(3)", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(4)", 1, out.readUnsignedByte());
        Assert.assertEquals("Wrong action type", 7, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong ip-address(1)", 10, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(2)", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(3)", 0, out.readUnsignedByte());
        Assert.assertEquals("Wrong ip-address(4)", 3, out.readUnsignedByte());
        Assert.assertEquals("Wrong action type", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong nw-tos", 204, out.readUnsignedByte());
        out.skipBytes(3);
        Assert.assertEquals("Wrong action type", 9, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 6653, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 10, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 6633, out.readUnsignedShort());
        out.skipBytes(2);
        Assert.assertEquals("Wrong action type", 11, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong port", 6613, out.readUnsignedShort());
        out.skipBytes(6);
        Assert.assertEquals("Wrong queue-id", 400, out.readUnsignedInt());
        Assert.assertEquals("Wrong action type", 65535, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 16, out.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 82, out.readUnsignedInt());
        byte[] tmp = new byte[8];
        out.readBytes(tmp);
        Assert.assertArrayEquals("Wrong data", expData, tmp);
        Assert.assertEquals("Wrong action type", 65535, out.readUnsignedShort());
        Assert.assertEquals("Wrong action length", 8, out.readUnsignedShort());
        Assert.assertEquals("Wrong experimenter", 102, out.readUnsignedInt());
        Assert.assertTrue("Written more bytes than needed", out.readableBytes() == 0);
    }
    
}