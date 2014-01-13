/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ControllerRole;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.RoleRequestOutputBuilder;

/**
 * Translates RoleReply messages
 * @author timotej.kubas
 * @author michal.polkorab
 */
public class RoleReplyMessageFactory implements OFDeserializer<RoleRequestOutput>{
    private static RoleReplyMessageFactory instance;
    private static final byte PADDING_IN_ROLE_REPLY_HEADER = 4;
    
    private RoleReplyMessageFactory() {
        // singleton
    }
    
    /**
     * 
     * @return singleton factory
     */
    public static synchronized RoleReplyMessageFactory getInstance(){
        if(instance == null){
            instance = new RoleReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public RoleRequestOutput bufferToMessage(ByteBuf rawMessage, short version) {
        RoleRequestOutputBuilder builder = new RoleRequestOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setRole(ControllerRole.forValue((int) rawMessage.readUnsignedInt()));
        rawMessage.skipBytes(PADDING_IN_ROLE_REPLY_HEADER);
        byte[] generationID = new byte[8];
        rawMessage.readBytes(generationID);
        builder.setGenerationId(new BigInteger(1, generationID));
        return builder.build();
    }
}
