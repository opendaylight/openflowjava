/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ControllerRole;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModCommand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowModInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class FlowModInputMessageFactoryTest {
    private static final byte PADDING_IN_FLOW_MOD_MESSAGE = 2;
    /**
     * @throws Exception 
     * Testing of {@link FlowModInputMessageFactory} for correct translation from POJO
     */
    @Test
    public void testFlowModInputMessageFactory() throws Exception {
        FlowModInputBuilder builder = new FlowModInputBuilder();
        BufferHelper.setupHeader(builder);
        byte[] cookie = new byte[]{0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        builder.setCookie(new BigInteger(cookie));
        byte[] cookieMask = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        builder.setCookieMask(new BigInteger(cookieMask));
        builder.setTableId(new TableId(65L));
        builder.setCommand(FlowModCommand.forValue(2));
        builder.setIdleTimeout(12);
        builder.setHardTimeout(0);
        builder.setPriority(126);
        builder.setOutPort(new PortNumber(4422L));
        builder.setOutGroup(98L);
        builder.setFlags(new FlowModFlags(true, false, true, false, true));
        FlowModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        FlowModInputMessageFactory factory = FlowModInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, factory.getMessageType(), factory.computeLength());
        Assert.assertEquals("Wrong cookie", message.getCookie().longValue(), out.readLong());
        Assert.assertEquals("Wrong cookieMask", message.getCookieMask().longValue(), out.readLong());
        Assert.assertEquals("Wrong tableId", message.getTableId().getValue().intValue(), out.readByte());
        Assert.assertEquals("Wrong command", message.getCommand().getIntValue(), out.readByte());
        Assert.assertEquals("Wrong idleTimeOut", message.getIdleTimeout().intValue(), out.readShort());
        Assert.assertEquals("Wrong hardTimeOut", message.getHardTimeout().intValue(), out.readShort());
        Assert.assertEquals("Wrong priority", message.getPriority().intValue(), out.readShort());
        Assert.assertEquals("Wrong outPort", message.getOutPort().getValue().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong outGroup", message.getOutGroup().intValue(), out.readUnsignedInt());
        Assert.assertEquals("Wrong flags", message.getFlags(), createFlowModFalgsFromBitmap(out.readShort()));
        out.skipBytes(PADDING_IN_FLOW_MOD_MESSAGE);
        // TODO implementation of match structure
        // TODO implementation of instructions
    }
    
    private static FlowModFlags createFlowModFalgsFromBitmap(short input){
        final Boolean _oFPFFSENDFLOWREM = (input & (1 << 0)) > 0;
        final Boolean _oFPFFCHECKOVERLAP = (input & (1 << 1)) > 0;
        final Boolean _oFPFFRESETCOUNTS = (input & (1 << 2)) > 0; 
        final Boolean _oFPFFNOPKTCOUNTS = (input & (1 << 3)) > 0;
        final Boolean _oFPFFNOBYTCOUNTS = (input & (1 << 4)) > 0;
        return new FlowModFlags(_oFPFFSENDFLOWREM, _oFPFFCHECKOVERLAP, _oFPFFRESETCOUNTS, _oFPFFNOPKTCOUNTS, _oFPFFNOBYTCOUNTS);
    }

}
