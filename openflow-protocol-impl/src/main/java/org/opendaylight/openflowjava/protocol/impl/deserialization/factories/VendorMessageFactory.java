/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.deserialization.factories;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistryInjector;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.util.ExperimenterDeserializerKeyFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;

/**
 * @author michal.polkorab
 *
 */
public class VendorMessageFactory implements OFDeserializer<ExperimenterMessage>,
        DeserializerRegistryInjector {

    private static final byte VENDOR_ID_INDEX = 8;
    private DeserializerRegistry deserializerRegistry;

    @Override
    public ExperimenterMessage deserialize(ByteBuf message) {
        long expId = message.getUnsignedInt(message.readerIndex() + VENDOR_ID_INDEX);
        OFDeserializer<ExperimenterMessage> deserializer = deserializerRegistry.getDeserializer(
                ExperimenterDeserializerKeyFactory.createExperimenterMessageDeserializerKey(
                        EncodeConstants.OF10_VERSION_ID, expId));
        return deserializer.deserialize(message);
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        this.deserializerRegistry = deserializerRegistry;
    }
}