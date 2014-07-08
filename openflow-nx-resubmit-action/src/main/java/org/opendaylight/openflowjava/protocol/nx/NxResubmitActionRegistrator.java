/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx;

import java.util.List;

import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionDeserializerKey;
import org.opendaylight.openflowjava.protocol.api.keys.experimenter.ExperimenterActionSerializerKey;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.nx.deserialization.NxActionResubmitDeserializer;
import org.opendaylight.openflowjava.protocol.nx.serialization.NxActionResubmitSerializer;
import org.opendaylight.openflowjava.protocol.spi.connection.SwitchConnectionProvider;

/**
 * @author michal.polkorab
 *
 */
public class NxResubmitActionRegistrator implements AutoCloseable {

    /** Nicira experimenter ID */
    public static final Long NICIRA_EXPERIMENTER_ID = 0x00002320L;
    private List<SwitchConnectionProvider> providers;

    /**
     * @param providers providers that shall be filled with serializers
     */
    public void registerNxResubmitSerializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            this.providers = providers;
            for (SwitchConnectionProvider provider : providers) {
                /* In case of handling multiple actions, instructions and other structures which
                 * are differentiated by vendor / experimenter subtype, vendor has to
                 * switch / choose between these subtypes.
                 * 
                 * This has to be done in this way because of experimenter headers, which
                 * provide only vendor / experimenter ID. Subtype position may be different
                 * for different vendors (or not present at all) - that's why vendor has to
                 * handle it in his own implementations.
                 */
                provider.registerActionSerializer(new ExperimenterActionSerializerKey(
                        EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID),
                        new NxActionResubmitSerializer());
            }
        }
    }

    /**
     * @param providers providers that shall be filled with deserializers
     */
    public void registerNxResubmitDeserializer(List<SwitchConnectionProvider> providers) {
        if (providers != null) {
            this.providers = providers;
            for (SwitchConnectionProvider provider : providers) {
                /* In case of handling multiple actions, instructions and other structures which
                 * are differentiated by vendor / experimenter subtype, vendor has to
                 * switch / choose between these subtypes.
                 * 
                 * This has to be done in this way because of experimenter headers, which
                 * provide only vendor / experimenter ID. Subtype position may be different
                 * for different vendors (or not present at all) - that's why vendor has to
                 * handle it in his own implementations.
                 */
                provider.registerActionDeserializer(new ExperimenterActionDeserializerKey(
                        EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID),
                        new NxActionResubmitDeserializer());
            }
        }
    }

    @Override
    public void close() throws Exception {
        for (SwitchConnectionProvider provider : providers) {
            // unregister serializer
            provider.unregisterSerializer(new ExperimenterActionSerializerKey(
                    EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID));
            // unregister deserializer
            provider.unregisterDeserializer(new ExperimenterActionDeserializerKey(
                    EncodeConstants.OF13_VERSION_ID, NICIRA_EXPERIMENTER_ID));
        }
    }
}