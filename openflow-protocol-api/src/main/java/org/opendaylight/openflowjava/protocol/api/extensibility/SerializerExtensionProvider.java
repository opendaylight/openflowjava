/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.extensibility;

import org.opendaylight.openflowjava.protocol.api.keys.MatchEntrySerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterInstructionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterSerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.MatchField;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OxmClassBase;


/**
 * Provides methods for serialization part of extensibility
 * @author michal.polkorab
 */
public interface SerializerExtensionProvider {

    /**
     * Unregisters custom serializer
     * @param key used for serializer lookup
     * @return true if serializer was removed,
     *  false if no serializer was found under specified key
     */
    public boolean unregisterSerializer(ExperimenterSerializerKey key);

    /**
     * Registers action serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public void registerActionSerializer(ExperimenterActionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers instruction serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public void registerInstructionSerializer(ExperimenterInstructionSerializerKey key,
            OFGeneralSerializer serializer);

    /**
     * Registers match entry serializer
     * @param key used for serializer lookup
     * @param serializer serializer implementation
     */
    public <OXM_CLASS extends OxmClassBase, OXM_TYPE extends MatchField> void registerMatchEntrySerializer(
            MatchEntrySerializerKey<OXM_CLASS, OXM_TYPE> key,OFGeneralSerializer serializer);
}