/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.openflowjava.protocol.api.keys.MatchEntryDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterErrorDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterMessageDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterMeterBandDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterMultipartReplyMessageDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterMultipartReplyTFDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterQueuePropertyDeserializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.meter.band.header.meter.band.MeterBandExperimenterCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.queue.property.header.QueueProperty;

/**
 * Provides methods for deserialization part of extensibility
 * @author michal.polkorab
 */
public interface DeserializerExtensionProvider {

    /**
     * Unregisters custom deserializer
     * @param key used for deserializer lookup
     * @return true if deserializer was removed,
     *  false if no deserializer was found under specified key
     */
    public boolean unregisterDeserializer(ExperimenterDeserializerKey key);

    /**
     * Registers action deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerActionDeserializer(ExperimenterActionDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers instruction deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerInstructionDeserializer(ExperimenterInstructionDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers match entry deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    public void registerMatchEntryDeserializer(MatchEntryDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers error message deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerErrorDeserializer(ExperimenterErrorDeserializerKey key,
            OFDeserializer<ErrorMessage> deserializer);

    /**
     * Registers experimenter (vendor) message deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerExperimenterMessageDeserializer(ExperimenterMessageDeserializerKey key,
            OFDeserializer<ExperimenterMessage> deserializer);

    /**
     * Registers multipart-reply (stats) message deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerMultipartReplyMessageDeserializer(ExperimenterMultipartReplyMessageDeserializerKey key,
            OFDeserializer<MultipartReplyMessage> deserializer);

    /**
     * Registers multipart-reply table-features message deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerMultipartReplyTFDeserializer(ExperimenterMultipartReplyTFDeserializerKey key,
            OFGeneralDeserializer deserializer);

    /**
     * Registers meter band deserializer (used in multipart-reply meter-config)
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerMeterBandDeserializer(ExperimenterMeterBandDeserializerKey key,
            OFDeserializer<MeterBandExperimenterCase> deserializer);

    /**
     * Registers queue property (QUEUE_GET_CONFIG_REPLY message) deserializer
     * @param key used for deserializer lookup
     * @param deserializer deserializer instance
     */
    void registerQueuePropertyDeserializer(ExperimenterQueuePropertyDeserializerKey key,
            OFDeserializer<QueueProperty> deserializer);
}