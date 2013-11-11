/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.serialization.factories;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.Assert;

import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.deserialization.factories.HelloMessageFactoryTest;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.TableId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.TableModInputBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class TableModInputMessageFactoryTest {
    private static final byte MESSAGE_TYPE = 17;
    private static final byte PADDING_IN_TABLE_MOD_MESSAGE = 3;
    
    /**
     * Testing of {@link TableModInputMessageFactory} for correct translation from POJO
     * @throws Exception 
     */
    @Test
    public void testTableModInput() throws Exception {
        TableModInputBuilder builder = new TableModInputBuilder();
        BufferHelper.setupHeader(builder);
        builder.setTableId(new TableId(9L));
        //TODO - fix test
        builder.setConfig(new PortConfig(true, false, true, false));
        TableModInput message = builder.build();
        
        ByteBuf out = UnpooledByteBufAllocator.DEFAULT.buffer();
        TableModInputMessageFactory factory = TableModInputMessageFactory.getInstance();
        factory.messageToBuffer(HelloMessageFactoryTest.VERSION_YET_SUPPORTED, out, message);
        
        BufferHelper.checkHeaderV13(out, MESSAGE_TYPE, 16);
        Assert.assertEquals("Wrong TableID", message.getTableId().getValue().intValue(), out.readByte());
        out.skipBytes(PADDING_IN_TABLE_MOD_MESSAGE);
        Assert.assertEquals("Wrong PortConfig", message.getConfig(), createPortConfig(out.readInt()));
    }
    
    private static PortConfig createPortConfig(long input){
        final Boolean _portDown   = ((input) & (1<<0)) > 0;
        final Boolean _noRecv    = ((input) & (1<<2)) > 0;
        final Boolean _noFwd       = ((input) & (1<<5)) > 0;
        final Boolean _noPacketIn = ((input) & (1<<6)) > 0;
        return new PortConfig(_noFwd, _noPacketIn, _noRecv, _portDown);
    }
}
