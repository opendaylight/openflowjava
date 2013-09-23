/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartRequestFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessageBuilder;

/**
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class MultipartReplyMessageFactory implements OFDeserializer<MultipartReplyMessage> {

    private static MultipartReplyMessageFactory instance;
    private static final byte PADDING_IN_MULTIPART_REPLY_HEADER = 4;
    
    private MultipartReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static MultipartReplyMessageFactory getInstance(){
        if (instance == null){
            instance = new MultipartReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public MultipartReplyMessage bufferToMessage(ByteBuf rawMessage, short version) {
        MultipartReplyMessageBuilder mrmb = new MultipartReplyMessageBuilder();
        mrmb.setVersion(version);
        mrmb.setXid(rawMessage.readUnsignedInt());
        mrmb.setType(MultipartType.values()[rawMessage.readUnsignedShort()]);
        mrmb.setFlags(new MultipartRequestFlags((rawMessage.readUnsignedShort() & 0x01) > 0));
        rawMessage.skipBytes(PADDING_IN_MULTIPART_REPLY_HEADER);
        // TODO - implement body
        //mrmb.setBody(rawMessage.readBytes(rawMessage.readableBytes()).array());
        return mrmb.build();
    } 
}
