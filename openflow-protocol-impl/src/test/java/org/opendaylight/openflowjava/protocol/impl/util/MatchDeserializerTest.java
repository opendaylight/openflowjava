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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv4AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.Ipv6AddressMatchEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.oxm.fields.MatchEntries;

/**
 * @author michal.polkorab
 *
 */
public class MatchDeserializerTest {
    
    /**
     * Testing match deserialization
     */
    @Test
    public void testIpv4Address() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("80 00 18 04 00 01 02 03");
        
        List<MatchEntries> list = MatchDeserializer.createMatchEntry(buffer, 8);
        MatchEntries entry = list.get(0);
        Assert.assertEquals("Wrong Ipv4 address format", new Ipv4Address("0.1.2.3"),
                entry.getAugmentation(Ipv4AddressMatchEntry.class).getIpv4Address());
    }
    
    /**
     * Testing match deserialization
     */
    @Test
    public void testIpv6Address() {
        ByteBuf buffer = ByteBufUtils.hexStringToByteBuf("80 00 34 10 00 00 00 01 00 02 00 03 00 04 00 05 00 06 0F 07");
        
        List<MatchEntries> list = MatchDeserializer.createMatchEntry(buffer, 20);
        MatchEntries entry = list.get(0);
        Assert.assertEquals("Wrong Ipv6 address format", new Ipv6Address("0000:0001:0002:0003:0004:0005:0006:0F07"),
                entry.getAugmentation(Ipv6AddressMatchEntry.class).getIpv6Address());
    }

}
