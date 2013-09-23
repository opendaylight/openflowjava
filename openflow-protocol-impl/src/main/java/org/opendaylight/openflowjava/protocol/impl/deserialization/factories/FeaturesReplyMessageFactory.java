/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetFeaturesOutputBuilder;

/**
 * @author michal.polkorab
 *
 */
public class FeaturesReplyMessageFactory implements OFDeserializer<GetFeaturesOutput>{
    
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
    
    @Override
    public GetFeaturesOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetFeaturesOutputBuilder builder = new GetFeaturesOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        byte[] datapathId = new byte[Long.SIZE/Byte.SIZE];
        rawMessage.readBytes(datapathId);
        builder.setDatapathId(new BigInteger(datapathId));
        builder.setBuffers(rawMessage.readUnsignedInt());
        builder.setTables(rawMessage.readUnsignedByte());
        builder.setAuxiliaryId(rawMessage.readUnsignedByte());
        rawMessage.skipBytes(PADDING_IN_FEATURES_REPLY_HEADER);
        builder.setCapabilities(rawMessage.readUnsignedInt());
        builder.setReserved(rawMessage.readUnsignedInt());
        return builder.build();
    }

}
