/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfigV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeaturesV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortStateV10;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;

/**
 * @author michal.polkorab
 *
 */
public class OF10PortStatusMessageFactoryTest {

    /**
     * Testing {@link OF10PortStatusMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("00 00 00 00 00 00 00 00 "
                + "00 10 01 01 05 01 04 02 41 4C 4F 48 41 00 00 00 00 00 00 00 00 00 00 "
                + "00 00 00 00 15 00 00 01 01 00 00 00 31 00 00 04 42 00 00 03 0C 00 00 08 88");
        PortStatusMessage builtByFactory = BufferHelper.decodeV10(OF10PortStatusMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV10(builtByFactory);
        Assert.assertEquals("Wrong reason", PortReason.OFPPRADD, builtByFactory.getReason());
        Assert.assertEquals("Wrong port - port-no", 16, builtByFactory.getPortNo().intValue());
        Assert.assertEquals("Wrong builtByFactory - hw-addr", new MacAddress("01:01:05:01:04:02"), builtByFactory.getHwAddr());
        // TODO - null-terminated string
        //Assert.assertEquals("Wrong builtByFactory - name", new String("ALOHA"), builtByFactory.getName());
        Assert.assertEquals("Wrong builtByFactory - config", new PortConfigV10(true, false, false, true, false, false, true),
                builtByFactory.getConfigV10());
        Assert.assertEquals("Wrong builtByFactory - state", new PortStateV10(false, true, false, false, false, true, false, false),
                builtByFactory.getStateV10());
        Assert.assertEquals("Wrong builtByFactory - curr", new PortFeaturesV10(false, false, false, false, true, true, true,
                false, false, false, false, false), builtByFactory.getCurrentFeaturesV10());
        Assert.assertEquals("Wrong builtByFactory - advertised", new PortFeaturesV10(false, false, true, true, false, false,
                false, false, false, false, true, false), builtByFactory.getAdvertisedFeaturesV10());
        Assert.assertEquals("Wrong builtByFactory - supbuiltByFactoryed", new PortFeaturesV10(true, true, false, false, false, false,
                false, true, false, true, false, false), builtByFactory.getSupportedFeaturesV10());
        Assert.assertEquals("Wrong builtByFactory - peer", new PortFeaturesV10(true, false, false, false, false, false, false,
                false, true, false, false, true), builtByFactory.getPeerFeaturesV10());
    }

}
