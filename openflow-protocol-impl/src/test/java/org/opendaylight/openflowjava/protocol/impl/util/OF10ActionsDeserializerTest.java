/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowjava.protocol.impl.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.DlAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.IpAddressAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.MaxLengthAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.NwTosAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.PortAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.QueueIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanPcpAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.VlanVidAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionsList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.actions.list.Action;

/**
 * @author michal.polkorab
 *
 */
public class OF10ActionsDeserializerTest {

    /**
     * Testing correct deserialization of actions (OF v1.0)
     */
    @Test
    public void test() {
        ByteBuf message = BufferHelper.buildBuffer("00 00 00 08 00 10 20 00 "
                + "00 01 00 08 10 10 00 00 "
                + "00 02 00 08 25 00 00 00 "
                + "00 03 00 08 00 00 00 00 "
                + "00 04 00 10 01 02 03 04 05 06 00 00 00 00 00 00 "
                + "00 05 00 10 02 03 04 05 06 07 00 00 00 00 00 00 "
                + "00 06 00 08 0A 00 00 01 "
                + "00 07 00 08 0B 00 00 02 "
                + "00 08 00 08 01 00 00 00 "
                + "00 09 00 08 00 02 00 00 "
                + "00 0A 00 08 00 03 00 00 "
                + "00 0B 00 10 00 04 00 00 00 00 00 00 00 00 00 30 "
                + "FF FF 00 08 00 00 12 34");
        
        message.skipBytes(4); // skip XID
        List<ActionsList> actions = OF10ActionsDeserializer.createActionsList(message);
        Assert.assertEquals("Wrong number of actions", 13, actions.size());
        Action action1 = actions.get(0).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.Output", action1.getType().getName());
        Assert.assertEquals("Wrong port", 16,
                action1.getAugmentation(PortAction.class).getPort().getValue().intValue());
        Assert.assertEquals("Wrong max-length", 8192,
                action1.getAugmentation(MaxLengthAction.class).getMaxLength().intValue());
        Action action2 = actions.get(1).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetVlanVid", action2.getType().getName());
        Assert.assertEquals("Wrong vlan-vid", 4112,
                action2.getAugmentation(VlanVidAction.class).getVlanVid().intValue());
        Action action3 = actions.get(2).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetVlanPcp", action3.getType().getName());
        Assert.assertEquals("Wrong vlan-pcp", 37,
                action3.getAugmentation(VlanPcpAction.class).getVlanPcp().intValue());
        Action action4 = actions.get(3).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.StripVlan", action4.getType().getName());
        Action action5 = actions.get(4).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetDlSrc", action5.getType().getName());
        Assert.assertArrayEquals("Wrong dl-src", ByteBufUtils.macAddressToBytes("01:02:03:04:05:06"), 
                ByteBufUtils.macAddressToBytes(action5.getAugmentation(DlAddressAction.class).getDlAddress().getValue()));
        Action action6 = actions.get(5).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetDlDst", action6.getType().getName());
        Assert.assertArrayEquals("Wrong dl-dst", ByteBufUtils.macAddressToBytes("02:03:04:05:06:07"), 
                ByteBufUtils.macAddressToBytes(action6.getAugmentation(DlAddressAction.class).getDlAddress().getValue()));
        Action action7 = actions.get(6).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetNwSrc", action7.getType().getName());
        Assert.assertEquals("Wrong nw-src", new Ipv4Address("10.0.0.1"),
                action7.getAugmentation(IpAddressAction.class).getIpAddress());
        Action action8 = actions.get(7).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetNwDst", action8.getType().getName());
        Assert.assertEquals("Wrong nw-dst", new Ipv4Address("11.0.0.2"),
                action8.getAugmentation(IpAddressAction.class).getIpAddress());
        Action action9 = actions.get(8).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetNwTos", action9.getType().getName());
        Assert.assertEquals("Wrong nw-tos", 1, action9.getAugmentation(NwTosAction.class).getNwTos().intValue());
        Action action10 = actions.get(9).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetTpSrc", action10.getType().getName());
        Assert.assertEquals("Wrong port", 2, action10.getAugmentation(PortAction.class)
                .getPort().getValue().intValue());
        Action action11 = actions.get(10).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.SetTpDst", action11.getType().getName());
        Assert.assertEquals("Wrong port", 3, action11.getAugmentation(PortAction.class)
                .getPort().getValue().intValue());
        Action action12 = actions.get(11).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.Enqueue", action12.getType().getName());
        Assert.assertEquals("Wrong port", 4, action12.getAugmentation(PortAction.class)
                .getPort().getValue().intValue());
        Assert.assertEquals("Wrong queue-id", 48,
                action12.getAugmentation(QueueIdAction.class).getQueueId().intValue());
        Action action13 = actions.get(12).getAction();
        Assert.assertEquals("Wrong action type", "org.opendaylight.yang.gen.v1.urn.opendaylight"
                + ".openflow.common.action.rev130731.Experimenter", action13.getType().getName());
        Assert.assertEquals("Wrong port", 4660, action13.getAugmentation(ExperimenterAction.class)
                .getExperimenter().intValue());
    }

}
