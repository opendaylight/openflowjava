/*
 * Copyright (c) 2013 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.openflowjava.protocol.impl.connection;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionConfiguration;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.api.extensibility.DeserializerRegistry;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageCodeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.MessageTypeKey;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFGeneralSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFSerializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.SerializerRegistry;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterIdDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterIdSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterSerializerKey;
import org.opendaylight.openflowjava.protocol.impl.core.ChannelInitializerFactory;
import org.opendaylight.openflowjava.protocol.impl.core.TcpHandler;
import org.opendaylight.openflowjava.protocol.impl.core.UdpHandler;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializationFactory;
import org.opendaylight.openflowjava.protocol.impl.deserialization.DeserializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializationFactory;
import org.opendaylight.openflowjava.protocol.impl.serialization.SerializerRegistryImpl;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.config.rev140630.TransportProtocol;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.multipart.request.multipart.request.body.MultipartRequestExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.table.features.properties.grouping.TableFeatureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Exposed class for server handling
 * @author mirehak
 * @author michal.polkorab
 */
public class SwitchConnectionProviderImpl implements SwitchConnectionProvider {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SwitchConnectionProviderImpl.class);
    private SwitchConnectionHandler switchConnectionHandler;
    private ServerFacade serverFacade;
    private ConnectionConfiguration connConfig;
    private SerializationFactory serializationFactory;
    private SerializerRegistry serializerRegistry;
    private DeserializerRegistry deserializerRegistry;
    private DeserializationFactory deserializationFactory;

    /** Constructor */
    public SwitchConnectionProviderImpl() {
        serializerRegistry = new SerializerRegistryImpl();
        serializerRegistry.init();
        serializationFactory = new SerializationFactory();
        serializationFactory.setSerializerTable(serializerRegistry);
        deserializerRegistry = new DeserializerRegistryImpl();
        deserializerRegistry.init();
        deserializationFactory = new DeserializationFactory();
        deserializationFactory.setRegistry(deserializerRegistry);
    }

    @Override
    public void setConfiguration(ConnectionConfiguration connConfig) {
        this.connConfig = connConfig;
    }

    @Override
    public void setSwitchConnectionHandler(SwitchConnectionHandler switchConnectionHandler) {
        LOGGER.debug("setSwitchConnectionHandler");
        this.switchConnectionHandler = switchConnectionHandler;
    }

    @Override
    public ListenableFuture<Boolean> shutdown() {
        LOGGER.debug("Shutdown summoned");
        //TODO: provide exception in case of: not started, not configured (already stopped)
        ListenableFuture<Boolean> result = serverFacade.shutdown();
        return result;
    }

    @Override
    public ListenableFuture<Boolean> startup() {
        LOGGER.debug("Startup summoned");
        serverFacade = createAndConfigureServer();
        
        LOGGER.debug("Starting ..");
        ListenableFuture<Boolean> result = null;
        try {
            if (serverFacade == null) {
                throw new IllegalStateException("No server configured");
            }
            if (serverFacade.getIsOnlineFuture().isDone()) {
                throw new IllegalStateException("Server already running");
            }
            if (switchConnectionHandler == null) {
                throw new IllegalStateException("switchConnectionHandler is not set");
            }
            new Thread(serverFacade).start();
            result = serverFacade.getIsOnlineFuture();
        } catch (Exception e) {
            SettableFuture<Boolean> exResult = SettableFuture.create();
            exResult.setException(e);
            result = exResult;
        }
        return result;
    }

    /**
     * @return
     */
    private ServerFacade createAndConfigureServer() {
        LOGGER.debug("Configuring ..");
        ServerFacade server = null;
        ChannelInitializerFactory factory = new ChannelInitializerFactory();
        factory.setSwitchConnectionHandler(switchConnectionHandler);
        factory.setSwitchIdleTimeout(connConfig.getSwitchIdleTimeout());
        factory.setTlsConfig(connConfig.getTlsConfiguration());
        factory.setSerializationFactory(serializationFactory);
        factory.setDeserializationFactory(deserializationFactory);
        factory.setOutboundQueueSize(connConfig.getOutboundQueueSize());
        TransportProtocol transportProtocol = (TransportProtocol) connConfig.getTransferProtocol();
        if (transportProtocol.equals(TransportProtocol.TCP) || transportProtocol.equals(TransportProtocol.TLS)) {
            server = new TcpHandler(connConfig.getAddress(), connConfig.getPort());
            ((TcpHandler) server).setChannelInitializer(factory.createPublishingChannelInitializer());
        } else {
            server = new UdpHandler(connConfig.getAddress(), connConfig.getPort());
            ((UdpHandler) server).setChannelInitializer(factory.createUdpChannelInitializer());
        }
        server.setThreadConfig(connConfig.getThreadConfiguration());
        return server;
    }

    /**
     * @return servers
     */
    public ServerFacade getServerFacade() {
        return serverFacade;
    }

    @Override
    public void close() throws Exception {
        shutdown();
    }

    @Override
    public boolean unregisterSerializer(ExperimenterSerializerKey key) {
        return serializerRegistry.unregisterSerializer((MessageTypeKey<?>) key);
    }

    @Override
    public boolean unregisterDeserializer(ExperimenterDeserializerKey key) {
        return deserializerRegistry.unregisterDeserializer((MessageCodeKey) key);
    }

    @Override
    public void registerActionSerializer(ExperimenterActionSerializerKey key,
            OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerActionDeserializer(ExperimenterActionDeserializerKey key,
            OFGeneralDeserializer deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerInstructionSerializer(ExperimenterInstructionSerializerKey key,
            OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerInstructionDeserializer(ExperimenterInstructionDeserializerKey key,
            OFGeneralDeserializer deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public <OXM_CLASS extends OxmClassBase, OXM_TYPE extends MatchField> void registerMatchEntrySerializer(MatchEntrySerializerKey<OXM_CLASS, OXM_TYPE> key,
            OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerMatchEntryDeserializer(MatchEntryDeserializerKey key,
            OFGeneralDeserializer deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerErrorDeserializer(ExperimenterIdDeserializerKey key,
            OFDeserializer<ErrorMessage> deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerExperimenterMessageDeserializer(ExperimenterIdDeserializerKey key,
            OFDeserializer<ExperimenterMessage> deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerMultipartReplyMessageDeserializer(ExperimenterIdDeserializerKey key,
            OFDeserializer<MultipartReplyMessage> deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerMultipartReplyTFDeserializer(ExperimenterIdDeserializerKey key,
            OFGeneralDeserializer deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerQueuePropertyDeserializer(ExperimenterIdDeserializerKey key,
            OFDeserializer<QueueProperty> deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerMeterBandDeserializer(ExperimenterIdDeserializerKey key,
            OFDeserializer<MeterBandExperimenterCase> deserializer) {
        deserializerRegistry.registerDeserializer(key, deserializer);
    }

    @Override
    public void registerExperimenterMessageSerializer(ExperimenterIdSerializerKey<ExperimenterInput> key,
            OFSerializer<ExperimenterInput> serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerMultipartRequestSerializer(ExperimenterIdSerializerKey<MultipartRequestExperimenterCase> key,
            OFSerializer<MultipartRequestExperimenterCase> serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerMultipartRequestTFSerializer(ExperimenterIdSerializerKey<TableFeatureProperties> key,
            OFGeneralSerializer serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }

    @Override
    public void registerMeterBandSerializer(ExperimenterIdSerializerKey<MeterBandExperimenterCase> key,
            OFSerializer<MeterBandExperimenterCase> serializer) {
        serializerRegistry.registerSerializer(key, serializer);
    }
}