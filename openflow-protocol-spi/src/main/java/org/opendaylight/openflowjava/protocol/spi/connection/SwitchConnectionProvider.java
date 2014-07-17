/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.spi.connection;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterSerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author mirehak
 * @author michal.polkorab
 *
 */
public interface SwitchConnectionProvider extends AutoCloseable {

    /**
     * @param configuration [protocol, port, address and supported features]
     */
    void setConfiguration(ConnectionConfiguration configuration);
    
    /**
     * start listening to switches, but please don't forget to do
     * {@link #setSwitchConnectionHandler(SwitchConnectionHandler)} first
     * @return future, triggered to true, when listening channel is up and running
     */
    ListenableFuture<Boolean> startup();
    
    /**
     * stop listening to switches
     * @return future, triggered to true, when all listening channels are down
     */
    ListenableFuture<Boolean> shutdown();
    
    /**
     * @param switchConHandler instance being informed when new switch connects
     */
    void setSwitchConnectionHandler(SwitchConnectionHandler switchConHandler);

    /**
     * Registers custom serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public void registerSerializer(ExperimenterSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers custom deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerDeserializer(ExperimenterDeserializerKey key, OFGeneralDeserializer deserializer);

    /**
     * Unregisters custom serializer
     * @param key used for serializer lookup
     * @return true if serializer was removed,
     *  false if no serializer was found under specified key
     */
    public boolean unregisterSerializer(ExperimenterSerializerKey key);

    /**
     * Unregisters custom deserializer
     * @param key used for deserializer lookup
     * @return true if deserializer was removed,
     *  false if no deserializer was found under specified key
     */
    public boolean unregisterDeserializer(ExperimenterDeserializerKey key);

    /**
     * Registers action serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public void registerActionSerializer(ExperimenterActionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers action deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerActionDeserializer(ExperimenterActionDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers instruction serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public void registerInstructionSerializer(ExperimenterInstructionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers instruction deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerInstructionDeserializer(ExperimenterInstructionDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers match entry serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public <OXM_CLASS extends OxmClassBase, OXM_TYPE extends MatchField> void registerMatchEntrySerializer(
            MatchEntrySerializerKey<OXM_CLASS, OXM_TYPE> key,OFGeneralSerializer serializer);

    /**
     * Registers match entry deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerMatchEntryDeserializer(MatchEntryDeserializerKey key,
            OFGeneralDeserializer deserializer);
}
