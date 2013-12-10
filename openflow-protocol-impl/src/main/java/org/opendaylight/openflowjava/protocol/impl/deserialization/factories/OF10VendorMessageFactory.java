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
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessageBuilder;

/**
 * Translates Vendor messages (OpenFlow v1.0)
 * @author michal.polkorab
 */
public class OF10VendorMessageFactory implements OFDeserializer<ExperimenterMessage> {

private static OF10VendorMessageFactory instance;
    
    private OF10VendorMessageFactory() {
        //singleton
    }
    
    /**
     * @return singleton factory
     */
    public static synchronized OF10VendorMessageFactory getInstance(){
        if (instance == null){
           instance = new OF10VendorMessageFactory(); 
        }
        return instance;
    }

    @Override
    public ExperimenterMessage bufferToMessage(ByteBuf rawMessage, short version) {
        ExperimenterMessageBuilder builder = new ExperimenterMessageBuilder();
        builder.setVersion(version);
        builder.setXid(rawMessage.readUnsignedInt());
        builder.setExperimenter(rawMessage.readUnsignedInt());
        int remainingBytes = rawMessage.readableBytes();
        if (remainingBytes > 0) {
            builder.setData(rawMessage.readBytes(remainingBytes).array());
        }
        return builder.build();
    }
}
