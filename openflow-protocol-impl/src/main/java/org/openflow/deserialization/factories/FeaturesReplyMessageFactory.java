/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.openflow.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutputBuilder;
import org.openflow.lib.deserialization.OfDeserializer;

/**
 * @author michal.polkorab
 *
 */
public class FeaturesReplyMessageFactory implements OfDeserializer<GetFeaturesOutput>{
    
    private static final byte PADDING_IN_FEATURES_REPLY_HEADER = 2;
    
    private static FeaturesReplyMessageFactory instance;

    private FeaturesReplyMessageFactory() {
        // do nothing, just singleton
    }
    
    /**
     * @return singleton factory
     */
    public static FeaturesReplyMessageFactory getInstance() {
        if (instance == null) {
            instance = new FeaturesReplyMessageFactory();
        }
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.openflow.core.deserialization.OfDeserializer#createMessage(io.netty.buffer.ByteBuf, short)
     */
    @Override
    public GetFeaturesOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetFeaturesOutputBuilder gfob = new GetFeaturesOutputBuilder();
        gfob.setVersion(version);
        gfob.setXid(rawMessage.readUnsignedInt());
        
        // TODO - unsigned long - check for appropriate process
        byte[] datapathId = new byte[8];
        rawMessage.readBytes(datapathId);
        gfob.setDatapathId(new BigInteger(datapathId));
        
        gfob.setBuffers(rawMessage.readUnsignedInt());
        gfob.setTables(rawMessage.readUnsignedByte());
        gfob.setAuxiliaryId(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        gfob.setCapabilities(rawMessage.readUnsignedInt());
        gfob.setReserved(rawMessage.readUnsignedInt());
        return gfob.build();
    }

}
