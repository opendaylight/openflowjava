/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortFeatures;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class PortStatusMessageFactoryTest {

    /**
     * Testing {@link PortStatusMessageFactory} for correct translation into POJO
     */
    @Test
    public void test(){
        ByteBuf bb = BufferHelper.buildBuffer("01 " + //reason 
                                              "00 00 00 00 00 00 00 " + //padding
                                              "00 01 02 03 " + //port no
                                              "00 00 00 00 " + //padding in ofp_port1
                                              "08 00 27 00 B0 EB " + //mac address
                                              "00 00 " + //padding in ofp_port2
                                              "00 00 00 0a " + //port config
                                              "00 00 00 05 " + //port state
                                              "00 00 00 81 " + //current features
                                              "00 00 00 81 " + //advertised features
                                              "00 00 00 81 " + //supported features
                                              "00 00 00 81 " + //peer features
                                              "00 00 00 81 " + //curr speed
                                              "00 00 00 80" //max speed
                                              );
        
        PortStatusMessage builtByFactory = BufferHelper.decodeV13(PortStatusMessageFactory.getInstance(), bb);
        
        BufferHelper.checkHeaderV13(builtByFactory);
        Assert.assertEquals("Wrong reason", 0x01, builtByFactory.getReason().getIntValue());
        Assert.assertEquals("Wrong portNumber", 66051L, builtByFactory.getPortNo().longValue());
        Assert.assertEquals("Wrong macAddress", new MacAddress("08002700B0EB"), builtByFactory.getHwAddr());
        Assert.assertEquals("Wrong portConfig", new PortConfig(false, true, false, true), builtByFactory.getConfig());
        Assert.assertEquals("Wrong portState", new PortState(true, false, true), builtByFactory.getState());
        Assert.assertEquals("Wrong portFeatures", new PortFeatures(true, false, false, false,
                                             false, false, false, true, false, false, false, false, 
                                             false, false, false, false), builtByFactory.getCurrentFeatures());
        Assert.assertEquals("Wrong portFeatures", new PortFeatures(true, false, false, false,
                                             false, false, false, true, false, false, false, false, 
                                             false, false, false, false), builtByFactory.getAdvertisedFeatures());
        Assert.assertEquals("Wrong portFeatures", new PortFeatures(true, false, false, false,
                                             false, false, false, true, false, false, false, false, 
                                             false, false, false, false), builtByFactory.getSupportedFeatures());
        Assert.assertEquals("Wrong portFeatures", new PortFeatures(true, false, false, false,
                                             false, false, false, true, false, false, false, false, 
                                             false, false, false, false), builtByFactory.getSupportedFeatures());
        Assert.assertEquals("Wrong currSpeed", 129L, builtByFactory.getCurrSpeed().longValue());
        Assert.assertEquals("Wrong maxSpeed", 128L, builtByFactory.getMaxSpeed().longValue());
    }
}
