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
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionDeserializerKey;

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
}
