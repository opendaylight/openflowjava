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
import org.opendaylight.openflowjava.protocol.impl.util.VersionAssignableFactory;
import org.opendaylight.openflowjava.util.ExperimenterDeserializerKeyFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;

/**
 * Translates Experimenter messages.
 * OpenFlow protocol versions: 1.3, 1.4, 1.5.
 * @author michal.polkorab
 */
public class ExperimenterMessageFactory extends VersionAssignableFactory implements OFDeserializer<ExperimenterMessage>,
        DeserializerRegistryInjector {

    private DeserializerRegistry deserializerRegistry;

    @Override
    public ExperimenterMessage deserialize(ByteBuf message) {
        final long xid = message.readUnsignedInt();
        final long expId = message.readUnsignedInt();
        final long expType = message.readUnsignedInt();

        OFDeserializer<ExperimenterDataOfChoice> deserializer = deserializerRegistry.getDeserializer(
                ExperimenterDeserializerKeyFactory.createExperimenterMessageDeserializerKey(
                        getVersion(), expId, expType));
        final ExperimenterDataOfChoice vendorData = deserializer.deserialize(message);

        ExperimenterMessageBuilder messageBld = new ExperimenterMessageBuilder()
                .setVersion(getVersion())
                .setXid(xid)
                .setExperimenter(new ExperimenterId(expId))
                .setExpType(expType)
                .setExperimenterDataOfChoice(vendorData);
        return messageBld.build();
    }

    @Override
    public void injectDeserializerRegistry(DeserializerRegistry deserializerRegistry) {
        this.deserializerRegistry = deserializerRegistry;
    }
}