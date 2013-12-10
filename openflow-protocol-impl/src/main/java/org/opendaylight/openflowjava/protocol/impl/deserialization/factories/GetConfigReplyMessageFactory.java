/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.impl.deserialization.OFDeserializer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.SwitchConfigFlag;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.GetConfigOutputBuilder;

/**
 * Translates GetConfigReply messages (both OpenFlow v1.0 and OpenFlow v1.3)
 * @author michal.polkorab
 * @author timotej.kubas
 */
public class GetConfigReplyMessageFactory implements OFDeserializer<GetConfigOutput> {

    private static GetConfigReplyMessageFactory instance;
    
    private GetConfigReplyMessageFactory() {
        // singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized GetConfigReplyMessageFactory getInstance(){
        if(instance == null){
            instance = new GetConfigReplyMessageFactory();
        }
        return instance;
    }

    @Override
    public GetConfigOutput bufferToMessage(ByteBuf rawMessage, short version) {
        GetConfigOutputBuilder builder = new GetConfigOutputBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setFlags(SwitchConfigFlag.forValue(rawMessage.readUnsignedShort()));
        builder.setMissSendLen(rawMessage.readUnsignedShort());
        return builder.build();
    }
}
